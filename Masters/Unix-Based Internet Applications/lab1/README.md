# Lab 1 - Booting

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1

1. Created PXE Server VM in VirtualBox based on Ubuntu Server 24.04 with the following config

   ![](https://i.postimg.cc/g0Tw70gs/image.png)

2. From the network setting of the machine, enabled two network adapters

   - The first one uses NAT, for internet connectivity
   - The second one uses "Internal Network", to make a private network with the PXE client

3. On the server machine, install and configure `dnsmasq` (which will provide us with DHCP and TFTP servers to be used for PXE functionality)

   > Main reference: https://wiki.debian.org/PXEBootInstall#Simple_way_-_using_Dnsmasq

   ```bash
   # Install dnsmasq
   sudo apt update
   sudo apt install dnsmasq
   
   # Configure dnsmasq to
   #  - Enable DHCP to lease addresses from the specified dhcp-range at the specified interface
   #  - Enable TFTP service to serve bootloader files from the specified tftp-root
   #  - Enable PXE services for BIOS and UEFI modes
   
   cat > /etc/dnsmasq.conf <<- EOF
   interface=enp0s8 # Interface for "Internal Network" from 'ip a' output
   domain=pxe-server.local
   dhcp-range=192.168.0.3,192.168.0.253,255.255.255.0,1h
   dhcp-boot=pxelinux.0,pxeserver,192.168.0.2
   enable-tftp
   tftp-root=/srv/tftp
   pxe-service=x86PC,"PXELINUX (BIOS)","pxelinux.0"
   pxe-service=X86-64_EFI,"PXE (UEFI)","grubx64.efi"
   EOF
   
   # Download netboot images for Debian bookworm (latest stable)
   #  and place them in the root directory of the TFTP server.
   #  may need to change owner and permissions for the files to avoid permission issues
   cd /srv/tftp
   wget http://ftp.debian.org/debian/dists/bookworm/main/installer-amd64/current/images/netboot/netboot.tar.gz
   tar -xzvf netboot.tar.gz
   rm netboot.tar.gz
   
   # Create symlinks for grub files
   ln -s debian-installer/amd64/grubx64.efi .
   ln -s debian-installer/amd64/grub .
   
   # Restart dnsmasq
   sudo systemctl restart dnsmasq
   
   # Inspect the status and logs of dnsmasq
   sudo systemctl status dnsmasq
   journalctl -xeu dnsmasq.service | less
   
   # In my case, I faced an error: port 53 was already allocated
   sudo lsof -i: 53 # To see who is using the port, it was systemd-resolved
   sudo systemctl stop systemd-resolved # stopped it for now, may disable if needed
   
   # For convenience, configure static IP address for the PXE server machine
   #  outside the range of addresses to be leased by the DHCP server 
   sudo ip a add 192.168.0.2/24 dev enp0s8
   sudo ip link set dev enp0s8 up 
   ```

- In VirtualBox, create another machine without an ISO starter image.

  - Call it `pxe-client` with one network adapter of type "Internal Network"

  - Change boot order from Settings -> System to use "Network", then "Hard Disk" booting.

    ![](https://i.postimg.cc/dQmpdyXd/pxeclient.png)

  - Boot the machine and say your prayers

    ![](https://i.postimg.cc/fLRSbrbH/image.png)

    

- As seen from server logs, the DHCP and TFTP servers ran successfully

  - The PXE client got the IP address of `192.168.147` from the DHCP server
  - Then proceeded to download the required files from TFTP server root at `/srv/tftp`

  ![](https://i.postimg.cc/dVZBsDf7/image.png)

- Follow the graphical installation steps to setup a persistent install.

- **Question: ** why not run our DHCP in the main network?

  Because of how DHCP works:

  - A new client joining the network would broadcast a "DHCP_DISCOVER" message to all devices in the network
  - Multiple DHCP servers in the network would generally cause problems because a new client may get multiple offers from different servers offering different addresses.
  - We want our PXE client to get (and accept) only the offer from the DHCP service provided by our PXE server so it can then communicate with the TFTP service to get bootloader files.
  - For that to work flawlessly without causing trouble in the main network, it's better to isolate both VMs in a separate "Internal Network".

  > Reference: https://en.wikipedia.org/wiki/Dynamic_Host_Configuration_Protocol

## Task 2

1. Briefly explain UEFI with secure boot enabled, UEFI without secure boot, and BIOS PXE
   booting approaches. How do they work? Explain with a simple diagram.

   - **In UEFI with secure boot**, the firmware initializes the hardware, then verifies the signature of binaries used by the bootloader against the database with hashes of trusted binaries. It then proceeded with the loading of EFI files that load the kernel from GPT partitions.

   - **If UEFI is used with the secure boot option disabled**, the same steps are carried, except now we do not verify the used binaries, which may be problematic if a malicious boot code was injected. 

   - **For the legacy (BIOS) booting option:** after initializing the hardware, the booting process would be carried in a certain order (e.g. disk, network, CD, USB)

     - If PXE is used with the default configuration, a DHCP request would be sent to get an IP for the client machine, then it would try to connect to a TFTP server to download and run bootloader files.

     - The regular BIOS booting would then be carried by launching the bootloader code.

       ![](https://www.wisecleaner.com/images/think-tank/310/UEFI-BIOS.png)

   >  References:
   >
   > - https://www.freecodecamp.org/news/uefi-vs-bios/
   > - https://wiki.ubuntu.com/UEFI/SecureBoot

2. What is a GPT? What is its general layout? Explain each element. What is the role of a partition table?

   - GUID Partition Table (GPT) is a partitioning scheme superseding the older MBR system and is used during the UEFI booting mode. It supports larger disk sizes with more partitions.

     > GUID stands for [Globally Unique Identifier](https://en.wikipedia.org/wiki/Universally_unique_identifier)

   - General layout includes

     - **Protective MBR:** safeguard for backward compatibility (more details in point 4)
     - **Primary GPT header:** storing info about partitions' structure and layout, followed by 128 individual entry for each potential partition.
     - **Partition entries:** containing data from the actual partitions (e.g., C:/, D:/, ... in Windows).
     - **Secondary (Backup) GPT header:** storing a backup of the primary GPT header for recovery in case the it got overwritten or corrupted.

     > In the diagram, LBA stands for [Logical Block Addressing](https://en.wikipedia.org/wiki/Logical_block_addressing), a more human-friendly numbering system for referencing disk blocks.

     ![](https://upload.wikimedia.org/wikipedia/commons/thumb/0/07/GUID_Partition_Table_Scheme.svg/360px-GUID_Partition_Table_Scheme.svg.png)

   - The partition table (entries 1-128 in the diagram above) is used for
     - Keeping track of all disk partitions (max. 128) with their metadata (name, size, type, location)
     - Also contains flags for marking certain properties for the partition (e.g., to indicate a bootable one to be used for loading the OS).

   > Reference: https://en.wikipedia.org/wiki/GUID_Partition_Table

3. What is gdisk? How does it work? What can you do with it? Provide a simple practice

   - `gdisk` is a CLI tool available in Linux that can be used to manage GPT disks.

   - It works by extracting information from the GPT header and is able to read/write data to it.

   - It can be used to perform typical disk tasks like creating, renaming, resizing, or deleting partitions; as well as viewing disk information and changing partition types.

   - Let's use gdisk to inspect the partition layout to inspect information about `/dev/sda` (VirtualBox emulated HDD storage device). The output shows information about the partition table, it's entries (start, end, size of each partition), and the other possibilities provided by `gdisk`.

     ![](https://i.postimg.cc/xdw7nTz0/image.png)

   > Reference: https://linux.die.net/man/8/gdisk

4. What is a Protective MBR and why is it in the GPT?

   - Some legacy systems that are unaware of GPT and can only process MBR disks may falsely misinterpret GPT disks as corrupt and attempt to erase or modify their content.
   - The presence of protective MBR as the first sector in GPT disks would preserve compatibility with such tools and ensure the won't modify or erase drive contents as it would appear to them to be an MBR disk of unknown type and no empty space.

   > References:
   >
   > - https://en.wikipedia.org/wiki/GUID_Partition_Table
   >
   > - https://en.wikipedia.org/wiki/Master_boot_record

## Task 3

1. Verify the GPT schema of your Ubuntu machine.

   - As shown in task 2.3, we can use a utility like `gdisk` to manipulate GPT disks on an Ubuntu machine as follows. The command shows GPT information for the SSD on my main machine

     ![](https://i.postimg.cc/bJCZZJQt/image.png)

2. Use the `dd` utility to dump the Protective MBR and GPT into a file in your home directory. The dump should contain up to first partition entry (Inclusive).

   - Used `lsblk` to identify devices and get the device file name for my main SSD (`/dev/nvme0n1`)

   - Proceeded to use `dd` (disk dump) tool to dump the first 2 logical blocks and the first partition entry to files in my home directory

     ```bash
     # Dump protective MBR (LBA 0)
     sudo dd if=/dev/nvme0n1 of=/home/ahmed/pmbr.img bs=512 count=1
     
     # Dump Primary GPT Header (LBA 1)
     sudo dd if=/dev/nvme0n1 of=/home/ahmed/gpth.img bs=512 skip=1 count=1
     
     # Dump the entry for the first partition
     sudo dd if=/dev/nvme0n1 of=/home/ahmed/entry1.img bs=128 skip=8 count=1
     
     # Merge outputs into one file (if needed)
     cat ~/pmbr.img ~/gpth.img ~/entry1.img > merged.img
     ```

3. Load the dump file into a hex dump utility (e.g. 010 editor) to look at the raw data in the file.

   Showing relevant (non-zero) portions of the dumped files

   ![](https://i.postimg.cc/dV2pwTY4/image.png)

4. Understand and fully annotate the Protective MBR, GPT header and first partition entry in the report. This means you must describe the purpose of every field, and translate all fields that have a numerical value into human-readable, decimal format.

   **Protective MBR** [[ref.](https://en.wikipedia.org/wiki/GUID_Partition_Table#Protective_MBR_(LBA_0))]

   The output (first command) above indicates that a single partition is present, encompassing the entire GPT drive (as much as it can be represented in MBR).

   > This can be seen in the output above at byte 0x1c2 with content `0xee` [standing for](https://en.wikipedia.org/wiki/Partition_type#PID_EEh) Protective MBR.

   The MBR signature `0x55aa` is also shown (bytes 0x1fe, 0x1ff) which indicates the end of the MBR section.

   **GPT Header** [[ref.](https://en.wikipedia.org/wiki/GUID_Partition_Table#Partition_table_header_(LBA_1))]

   Colors speak better than words, all fields and their description are annotated as follows.

   ![](https://i.postimg.cc/T1vvqRkm/colors.png)

   **First Partition Entry** [[ref.](https://en.wikipedia.org/wiki/GUID_Partition_Table#Partition_entries_(LBA_2%E2%80%9333))]

   ![](https://i.postimg.cc/sXq7TLS5/noice.png)

   **Questions:**

   - 4.1) At what byte index from the start of the disk do the partition table entries start?
     - For GPT disks, the first entry would start at LBA 2 (after 512 * 2 = 1024 byte).
     - That is, at byte 1025 = `0x401`
   - 4.2) At what byte index would the partition table start if your server had a so-called “4K native” (4Kn) disk?
     - For a 4K native disk (4096 byte block size), LBA 2 would start after 2 * 4096 = 8192.
     - That is, at byte 8193 = `0x2001`

5. Name two differences between primary and logical partitions in an MBR partitioning scheme.

   - MBR partitioning scheme allows the HDD to be divided into 4 **primary** partitions, one of which can be marked as **extended** and be further divided into multiple **logical** partitions.

     | Primary Partitions (MBR)                       | Logical Partitions (MBR)                  |
     | ---------------------------------------------- | ----------------------------------------- |
     | There can be 4 of them at maximum              | Much more, but limitation is OS-dependent |
     | Can be marked as "bootable" for loading the OS | Cannot be marked as "bootable"            |
