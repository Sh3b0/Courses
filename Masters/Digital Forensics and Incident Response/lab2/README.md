# Lab 2 - Windows Forensics

> Ahmed Nouralla - a.shaaban@innopolis.university - **st14**

[TOC]

## Task 1 - Download Evidence File

- Downloaded [Case 2](https://drive.google.com/file/d/1LMXj8auzx8Kvtx8bFxcpLFM67jdZmIkE/view?usp=sharing) evidence file

- Output from `file` shows information about the case file.

  ![image-20250331195747877](https://i.imgur.com/1frYkxk.png)

- Output from `fdisk` should information about the dumped disk image.

  ![image-20250402223056740](https://i.imgur.com/QULGaTF.png)

## Task 2 - Forensics Analysis

### 2.1. System Information

Figure out the platform, system and file system type

- Boot Partition: DOS/MBR

- File System: NTFS

- System: Windows Vista Enterprise (found in Registry and extracted by Autopsy).

  ![image-20250402231446854](https://i.imgur.com/q9GBjFI.png)

  ![image-20250402201930455](https://i.imgur.com/XzFEyA7.png)

### 2.2. Malware Search

- Mount the image in read-only mode

  ![image-20250331203030668](https://i.imgur.com/S1U0gIy.png)

- Install `clamav` with `apt`

- Run `freshclam` to perform a database update

- Run a scan with `clamscan`

  ![image-20250331215812245](https://i.imgur.com/ShFbu75.png)

- Findings include

  - [Adware:Win32/Babylon](https://www.microsoft.com/en-us/wdsi/threats/malware-encyclopedia-description?Name=Adware:Win32/Babylon): browser-hijacking adware.
  - [Win32.Expiro](https://www.microsoft.com/en-us/wdsi/threats/malware-encyclopedia-description?Name=Win32/Expiro): credential stealing virus.
  - [Win.Vobfus](https://malpedia.caad.fkie.fraunhofer.de/details/win.vobfus): Visual-Basic worm that propagates to external systems, may change windows settings and/or download other malware.


### 2.3. Timeline Analysis

- Create a CSV timeline with [NBTempoX](https://github.com/esperti/nbtempox)

- Analyze the timeline in [timesketch](https://github.com/google/timesketch/).

  - Most activities happen between 2005-2012.

    ![image-20250402221116455](https://i.imgur.com/dyaJUqW.png)

  - Peak activity between 25th of Match and 1st of April 2012

    ![image-20250402222515552](https://i.imgur.com/XUS07fo.png)

  - Most of such activities relate to user account `jb` accessing files on their system and using browsers.

### 2.4. Artifacts in Windows Components

#### 2.4.1. Windows Registry

Registry Hives stored at `%WINDIR%\System32\config\`. Interesting hives include:

- **Security Account Manager (SAM):** includes information about user accounts on the machine. Autopsy is able to extract interesting info about each account

  - Administrator and Guest accounts were disabled.

  - JB: main account used by "Jason Bourne"

    ![image-20250402225259593](https://i.imgur.com/1P2Fbwn.png)

  - MK: briefly used by "Marie Kreutzer"
    - Created on 29-3-2012, 14:22:27 PDT
    - Last used on 1-4-2012, 00:53:57 PDT
    - Login count: 7
    - Password hint: "Who am I"

- **SOFTWARE:** includes hints about software installed on the system.

  - TrueCrypt: disk encryption software

  - KeePassX: for maintaining an encrypted database.

  - eMule: P2P file sharing

  - Other regular software: Skype, Firefox, Thunderbird, Adobe Reader.

- **SYSTEM:** includes information about USB devices attached

  ![image-20250402232806914](https://i.imgur.com/oUHNTex.png)

#### 2.4.2. Event Logs

- `.evtx` files at `%WINDIR%\System32\winevt`contain system events.

- Relevant ones include: `Application`, `System`, `Setup`, and `Security`

  - Events include Logon/Logoff activities, application/driver behaviors, registry accesses, etc.

- Analyzed the files in Windows Event Viewer, no significant findings.

  ![image-20250403021324002](https://i.imgur.com/JRyKVeF.png)

#### 2.4.3. Personal data of users

- Files at `Desktop`, `Downloads`, `Documents`, and `AppData` are typically the most relevant and interesting.

- `Users/jb/Documents/wanted.png`: screenshot showing PC owners `jb` and `mk` as wanted criminals.

  ![image-20250403002622606](https://i.imgur.com/3HWXleb.png)

- `Users/jb/Documents/pass_uk_kane.png`: UK passport (contradicts above info showing US nationality).

  ![image-20250403002738008](https://i.imgur.com/jWSoJHS.png)

- Password-protected ZIP archive

  - Filename: `treadstone_files.zip`
  - Location: Thunderbird inbox storage in `AppData/Roaming`
  - Received as email attachment for the email titled "Your identity" (see below)
  - Content: a PDF file, probably the one shown above.

- Encrypted KeePass Database at `Users/jb/Documents/mypasswd.kdb`

- Executable file at `Users/mk/Desktop`

  - Filename: `UPS_INVOICE_TRNR_PLEASE_PRINT.DOC.exe`
  - Labeled as [Backdoor.Win32.Agobot](https://threats.kaspersky.com/en/threat/Backdoor.Win32.Agobot/)
  - Hybrid Analysis Scan: [link](https://www.hybrid-analysis.com/sample/8279aa5f5bb6ffd0ce65131a9cd699b5b43d8351008b82d7189b9c31bf87bbe5/67eda66e02d3918a200de1a0)

#### 2.4.4. Mail

- Found incriminating communications between `jb`, `mk`, and `pamela.landy` (see below report).

  ![image-20250403021608907](https://i.imgur.com/0T7bYBZ.png)

#### 2.4.5. Browser

- Browser form history, shows mobile number and query searches for hotels.

  ![image-20250403005354475](https://i.imgur.com/ZtedoDo.png)

- Web Bookmarks: shows lots of news articles.

  ![image-20250403022022433](https://i.imgur.com/4jsxRbF.png)

- Web search: shows a query for `naples italy`

  ![image-20250403022627043](https://i.imgur.com/NGSA6Pw.png)

## Task 3 - Create a forensic report

Overall report structure may look like this [[ref.](https://online.fliphtml5.com/rllbc/zdmn/#p=32)]

1. **Introduction**

   - Summary of Case and Tasking

     ```tex
     The case involves Jason Bourne, who is wanted by the FBI for multiple offences; including Esponiage, Fraud, Theft, Computer Crime, Counterfeiting, and Forgery. Upon obtaining a forensic copy from the hard disk of suspects, the forensic expert was engaged to reconstruct - to the best of their ability - the documentary evidence showing the suspect's activities as well as find corroborative evidence of the charges against him.
     ```

   - Statement of Compliance

     ```tex
     I understand my duty as an expert witness to provide independent assistance by way of objective unbiased opinion in relation to matters within my expertise. I will inform all parties in the event that my opinion changes on any material issues.
     ```

1. **Forensic Examination**

   - Tools used (should also mention versions)
     - Linux-native tools: `file`, `fls`, `fdisk`, `dd`
     - Windows-native tools: `Windows Event Viewer`
     - Malware Analyzer: `clamav`
     - Timeline generating and analysis: `esperti/nptempox`, `google/timesketch`
     - Forensic platform: `autopsy 4.22.0` and its sub-systems.
     - Registry Explorer: open-source registry inspection tool by Eric Zimmerman: 
   - Chain of Custody: a table with the following fields
     - Date and time of release
     - Released By Whom
     - Received By Whom
     - Comments/Location
   - Evidence Classes:
     - All information shown above, presenting only facts without opinions/guesses.
     - Example:
       - Artifact Filename: `pass_uk_kane.pdf`
       - File Path: `\Users\jb\Documents\pass_uk_kane.pdf`
       - MD5 Hash:  `dc9c534aefafb745636ef914d86caa5c`
       - Created: `2012-03-30 08:52:55 PDT`
       - Modified: `2012-03-30 08:52:55 PDT`
       - Accessed: `2012-03-30 08:52:55 PDT`
       - Comment: UK passport with the image of the suspect under a different name.

3. **Summary of conclusions**

   - Expert opinion regarding the found results, may look like this:

     ```tex
     Analysis of the forensic evidence show that the suspect (upon receiving an email from his accomplice informing him that they were wanted), made several web searches and skype calls, then obtained forged documents to facilitate fleeing to Naples, Italy. Further analysis and decryption of the encrypted files obtained from the suspect's PC may provide additional supporting evidence for court proceedings.
     ```

     

