# Lab 1 - Data Acquisition

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Environment Setup

1. Download [FTKImager](https://d1kpmuwb7gvu1i.cloudfront.net/ftkimager.3.1.1_fedora64.tar.gz) and [CAINE 13.0](https://deb.parrot.sh/direct/parrot/iso/caine/caine13.0.iso)

2. Download and inspect [evidence](https://mega.nz/file/CdtQjKgC#90sNQGrcrlJxcFKa-Cs-ZoitWuMwQ8k4MEaZd6qZLKU) file

   ![image-20250320213620120](https://i.imgur.com/vnNgPeP.png)

3. Decompress evidence into a raw (dd) format using FTK Imager.

   - Exporting in Linux (using FTK Imager v3.1.1 CLI)

     ![image-20250321150618360](https://i.imgur.com/cFNRZBp.png)

   - Exporting in Windows (using FTK Imager v4.7.3.81) gave same results.

      ![image_2025-03-21_14-55-09](https://i.imgur.com/uZBXRb7.png)

     ![image_2025-03-21_14-56-02](https://i.imgur.com/cuepi7n.png)

4. Note down the hashes from FTKImager

   ```ini
   [Computed Hashes]
    MD5 checksum:    50decb45c3d56ffe1a3c538bb7898fd9
    SHA1 checksum:   e0839afe9e275b2c39c1a1eb15c74ae019ab9e55
   ```

5. Run CAINE 13.0 in QEMU with `virt-manager`

   ![image-20250320232048653](https://i.imgur.com/4vZZJt8.png)

5. Burn the raw image to my USB

   ![image-20250321212102725](https://i.imgur.com/vyzYhIQ.png)

   ![image-20250321213841392](https://i.imgur.com/D2Phmd5.png)

## Task 2 - Imaging

### 2.1. Retrieving an Image

1- Discuss how you can retrieve an image from an, currently off-line, USB stick in a forensically sound manner. Create and describe this method.

Given a USB stick containing evidence, one should follow a defined procedure to correctly handle the evidence in a forensically-sound manner.

| Step              | Description                                                  |
| ----------------- | ------------------------------------------------------------ |
| 1. Secure the USB | - Inspect the device for any physical damage.<br />- Package and label the device as necessary<br />- Note down any observations/findings |
| 2. Document       | - Fill/Update relevant documents (e.g., timestamped chain of custody) |
| 3. Connect        | - Connect USB to a forensic hardware/software device/system<br />- Ensure evidence is not tampered by any means (no write operations). |
| 4. Image          | - Create a forensic image of the evidence<br />- Use popular OSS/certified imaging tools (e.g., Guymager)<br />- Generate cryptographic hashes (MD5, SHA) of the resulting image |
| 5. Backup         | - Backup the image and its hash in case you need to share/copy later |
| 7. Analyze        | - Start working on analysis of the forensic image<br />- Mount it in read-only and use analysis tools (e.g., autopsy) |
| 8. Report         | - Create a report with timeline of interesting findings      |

### 2.2. CAINE Tools

2- Write a one-line description, or note a useful feature for the following tools included in CAINE: Guymager, Disk Image Mounter, dcfldd / dc3dd, kpartx.

- **Guymager:** creates forensic images and performs disk cloning.
- **Disk Image Mounter:** mounts ISO contents so it can be viewed.
- **dcfldd/dc3dd:** enhanced versions of UNIX dd (used to convert/dump disk images).
- **kpartx:** enhanced version of UNIX partx (used to create device maps).

### 2.3. Retrieving an image

3- Follow your method to retrieve the image from the drive.

1. Obtained the USB: No physical damage identified

2. Documented the timestamp of retrieval and updated the chain of custody

3. Connected the USB to CAINE 13.0 (ensuring the device is not automounted)

4. Created a forensic image of the USB with Guymager v.0.8.13

   ![image-20250321180631490](https://i.imgur.com/3aub0Ev.png)

   ![image-20250321180726485](https://i.imgur.com/mBQ88XG.png)

5. Rest of the steps are implemented in Task 4

### 2.4. CAINE Features

4- Read about CAINE Linux and its features while waiting on the dump to finish.

> **Why would you use a Forensic distribution?**

- Unlike regular distros, forensic ones come with popular investigative software pre-installed.
- The environment also comes pre-configured with default settings useful for a forensic analyst (e.g., disabled auto-mounting to prevent accidental write operations that spoil evidence)

> **When to use a live environment and when to use an installed one?**

- Live environment is useful for quick, one-time tasks where a fresh environment is needed.
- Installed environment is useful when we need to persist data and tool configs for regular use.

> **What are the policies of CAINE? [[ref.](https://www.caine-live.net/page8/page8.html)]**

- **Mounting:** the system never automatically mounts any device, user has to mount a connected device using one of these methods
  - **Using "Mounter" GUI tool**
  - **From Caja (file manager):** clicking on a device icon automatically mounts with these options: `ro,noatime,noexec,nosuid,nodev,noload` (same options when using the GUI tool).
  - **From terminal:** using `mount` command.
- **Un-mounting**: a user should unmount a device before unplugging it to avoid problems.
  - **Using "Mounter" GUI tool**
  - **From Caja (file manager):** need to run it as root with `sudo caja`
  - **From terminal:** `sudo umount`

### 2.5. Creating a timeline

- Created a timeline using NBTempoX

  ![image-20250321201356382](https://i.imgur.com/4ZUQJyh.png)

  ![image-20250321201635509](https://i.imgur.com/6VotEzi.png)

  ![image-20250321203455920](https://i.imgur.com/5JnD7Mr.png)

## Task 3 - Verification

- E01 to RAW Export using `ewfexport`

  ![image-20250321191534707](https://i.imgur.com/YbXh8ME.png)

  ![image-20250321191618496](https://i.imgur.com/Tqz7wS4.png)

### 3.1. Steps to verify my procedure

To be sent to the teammate along with metadata file `evidence1.dd.001.txt`.

1. Obtain the USB and check for physical damage

2. Fill/Update relevant documents (e.g., timestamped chain of custody)
2. Connect USB to a forensic hardware/software device/system
2. Verify the matadata and hashes match.

### 3.2. Verify teammate procedure

1. Obtained the USB and checked for physical damage. Looks OK.

2. Updated the chain of custody

3. Connected USB to my host without mounting. Identified information about the disk: block size and partitions.

   ![image-20250321235147539](https://i.imgur.com/YXIvBmh.png)

4. Redirected the USB to CAINE VM, again without mounting.

   ![image-20250321235635869](https://i.imgur.com/a3mOMdG.png)

5. Given the size of the written image, we can verify the hash. It matches the received metadata

   ![image-20250321231136599](https://i.imgur.com/d4qWpju.png)

## Task 4 - Technical Analysis

### 4.1. Mounting Image

Mounted the image in read-only for analysis.

![image-20250322022837015](https://i.imgur.com/Jj7aBN8.png)

### 4.2. Image details:

- Size ~ 3096MB

- File System (FS) type: NTFS (Windows Machine)

- It has MBR boot sector as shown above from the output of `file` command.

- Contents for a machine with user account named "Thomas Ehrhart"

  ![](https://i.imgur.com/TJihTKR.png)

### 4.3. Analysis

Going through some interesting findings from the machine:

1. Autopsy case

   ![image-20250322002226958](https://i.imgur.com/EkInLhx.png)

1. Timeline of events, as shown in Autopsy server

   - Most activity in August 2016: mostly firefox-related for the user profile `m3k5a7px`

     ![image-20250322021857757](https://i.imgur.com/H7ltd54.png)

3. Suspicious image files from OneDrive downloads.

   ![image-20250322022252261](https://i.imgur.com/asP91yK.png)

3. Interesting finding: a file in Documents. In the XML metadata there was some text: `many secerts`

   ![image-20250322010946097](https://i.imgur.com/oFdFph0.png)

4. Browser form autofill data (SQLite database) 

   ![image-20250322021355060](https://i.imgur.com/CbOW1an.png)

5. VeraCrypt related findings

   1. Google search for "Artifact hiding" (in browser cache)
   1. VeraCrypt Installer exe (in Downloads)
   1. Config files (in AppData/Roaming/VeraCrypt), shows U:/ as the last selected drive
   1. Encrypted file `myDokuments` (in Documents folder)

   ![image-20250322021609072](https://i.imgur.com/jjNF8n9.png)

6. Potential directions for further investigation:
   - Look for attacks or decryption keys to view encrypted documents.
   - Try to extract relevant info regarding the drive U:/ from registry/system files.

