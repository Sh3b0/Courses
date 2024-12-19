# Lab 2 - UEFI Secure Boot

> Ahmed Nouralla - a.shaaban@innopolis.university 

[TOC]

## Overview

The overall task is to verify that Ubuntu uses the chain of trust described [here](https://wiki.ubuntu.com/UEFI/SecureBoot/Testing#:~:text=In%20order%20to,during%20kernel%20boot) during the secure boot process.

## Task 1 - Firmware Databases

- Install EFI tools and OpenSSL

  ```bash
  apt install efitools openssl
  ```

- Show available EFI variables

  ```bash
  $ efi-readvar | grep Variable
  Variable PK, length 955
  Variable KEK, length 2519
  Variable db, length 5131
  Variable dbx, length 21293
  Variable MokList has no entries
  ```

- Short Explanation for each variable:

  - **Platform Key (PK):** used to verify the trust between the hardware and the firmware that runs on it. 
    - In my case, it contained a certificate signed by "Acer Root CA", my device manufacturer.
  - **Signature Databases (DB):** contains signatures for trusted efi binaries.
  - **Blacklist Signatures Database (DBX):** contains signatures for explicitly untrusted efi binaries. Useful for revoking previously-trusted keys.
  - **Key Exchange Key (KEK):** a key used to add/update entries in DB/DBX.
    - The platform key is used to verify KEK, which in turn can be used to update DB/DBX.
  - **Machine Owner Key List (MokList): ** for adding user-defined keys. For example, signing a custom device driver. 

- Showing PK, KEK, and the first entry in DB

  ![](https://i.postimg.cc/pLMLxR70/image.png)

- If we were to extract that data, we can use `efi-readvar` to dumb the signature database (DB) as an EFI Signature List (ESL) file.

  ```bash
  efi-readvar -v db -o shim.esl
  ```

- The package `efitools` also provide a command `sig-lists-to-certs` which, as the name suggests, would convert our ESLs to cert files (5 of them were extracted in that case)

  ```bash
  $ sig-list-to-certs shim.esl db
  X509 Header sls=1543, header=0, sig=1499
  file db-0.der: Guid 77fa9abd-0359-4d32-bd60-28f4e78f784b
  Written 1499 bytes
  X509 Header sls=951, header=0, sig=907
  file db-1.der: Guid 92fcafcd-c861-4b8b-aff2-a3d5a3e093f8
  Written 907 bytes
  X509 Header sls=803, header=0, sig=759
  file db-2.der: Guid 73f64a94-c5fc-4e09-9c3b-5104b5e49515
  Written 759 bytes
  X509 Header sls=785, header=0, sig=741
  file db-3.der: Guid 73f64a94-c5fc-4e09-9c3b-5104b5e49515
  Written 741 bytes
  X509 Header sls=1049, header=0, sig=1005
  file db-4.der: Guid 92fcafcd-c861-4b8b-aff2-a3d5a3e093f8
  Written 1005 bytes
  ```

- We can open the `.der` file in a certificate viewer, or use `openssl x509` to convert it from `.der` format to a human-readable `txt` format.

  ```bash
  openssl x509 -inform der -in db-0.der -noout -text > cert-0.txt
  ```

1. Showing relevant information from the certificate

   ![](https://i.postimg.cc/63Yn8zgm/image.png)

2. The certificate shown **is not the root** in the chain of trust, the **platform key** is, as described above.

## Task 2 - SHIM

3. To verify that the system indeed boots the SHIM (first stage bootloader), we use `efibootmgr` tool which shows the boot order.

   - It shows that `shim64.efi` is loaded from the first partition on my SSD (identified by its GUID).

   - From the output of `df` or any partition manager like Gparted, I can see that the first partition in my machine (`/dev/nvme0n1p1`) is mounted at `/boot/efi`

   - Therefore, the SHIM is loaded at `/boot/efi/EFI/ubuntu/shimx64.efi` 

     ![](https://i.postimg.cc/qMfyzt50/image.png)

4. To verify the SHIM file is signed with `Microsoft UEFI CA`, we can use `sbverify` as follows

   ![](https://i.postimg.cc/KcCrBp46/image.png)

5. The EFI binaries being signed, verified, and loaded in Linux, are executables in WinPE file format. The file format is described [here](https://www.symbolcrash.com/wp-content/uploads/2019/02/Authenticode_PE-1.pdf), showing that the actual signatures are stored in an **"Attribute Certificate Table"** as shown in Figure 1 of the standard.

   ![](https://i.postimg.cc/rmNXxRNb/image.png)

6. The signatures are stored in PKCS#7 (Public Key Cryptography Standard) format. PKCS#7 is part of a family of syntax standards for storing signed and/or encrypted data.

7. To extract the signature data from `shimx64.efi`, we can use [pyew](https://github.com/joxeankoret/pyew)

   - Pyew is written in Python2, we can clone and run it from source inside `python:2` docker container

     ```bash
     # Run Python2 Docker container
     docker run -it -d --name python2 python:2
     
     # Copy shim file from host to container for analysis
     sudo docker cp /boot/efi/EFI/ubuntu/shimx64.efi python2:/
     
     # Exec into the container with bash
     docker exec -it python2 bash
     
     # Clone pyew from github, install needed dependency, and run the tool
     $ git clone https://github.com/joxeankoret/pyew
     $ cd pyew
     $ pip install capstone
     $ python ./pyew.py ../shimx64.efi
     ```

   ![](https://i.postimg.cc/BbKgDwyN/image.png)

   ![](https://i.postimg.cc/8CJFF51L/image.png)

   - Signature data is located at the `DATA_DIRECTORY` section of the `OPTIONAL_HEADER` field in the binary as shown in Figure 1 of the standard. Here we use pyew to locate the exact virtual address and size of the entry.

     ![](https://i.postimg.cc/vZDTjBxC/image.png) 

   - We can then use `dd` to extract the entry with the following parameters:
     - Input file (SHIM EFI): `shimx64.bin`
     - Output file name: `signature.der`
     - Skip (where to start reading): 0xE9A78 = 957048 (in decimal) +8 = 957056
       - +8 to skip over Microsoft WIN certificate structure header.
     - Count (how much to capture) = 0x2590 = 9616 block, each of size 1 byte.

8. After extracting the signature, we can use `openssl pkcs7` to show the content in human readable text. The file contained two certificates, with the shown subjects and issuers respectively,

   ![](https://i.postimg.cc/YSRyqZKW/image.png)

   - Looks like "Marketplace Root" is verified by "UEFI CA", which is verified by "UEFI Driver Publisher". The trust chain can be represented with the following diagram:

     ![](https://i.postimg.cc/k441L9Pc/image.png)

Now we know that the system indeed boots the shim, and that this OS loader is indeed signed by Microsoft and where this signature is stored.
