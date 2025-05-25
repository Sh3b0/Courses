# Lab 3 - Malware Analysis

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Preparation

1. [Cuckoo](https://github.com/cuckoosandbox/cuckoo) project is unmaintained. I will be using [Sandboxie](https://github.com/sandboxie-plus/Sandboxie) as an alternative

2. Create a Windows 10 VM for using as the sandboxing environment.

   ![image-20250406185232242](https://i.imgur.com/gIYgWKv.png)

2. If needed, one could make a host-only network for the guest (no internet access) using the following commands:

   - In the host

     ```bash
     sudo brctl addbr br0 # create a bridge interface
     sudo ip a add 172.16.0.1/16 dev br0 # add static IP
     sudo ip link set br0 up # bring up the bridge
     ```

   - In the guest, set manual IP and gateway from control panel.

     ![image-20250406190324420](https://i.imgur.com/iU5cNoS.png)

   - Verify connectivity

     ![image-20250406191102279](https://i.imgur.com/vd5x3sn.png)

2. However, we don't need connectivity at all for this experiment. So it's safer to disable it altogether.

   ![image-20250406151140741](https://i.imgur.com/QQ7kyfI.png)

## Task 2 - Obtaining Malware

- Downloaded binary samples from [TheZoo](https://github.com/ytisf/theZoo).
  - [VBS.LoveLetter](https://malpedia.caad.fkie.fraunhofer.de/details/vbs.iloveyou) (Email Worm): `85cff1dc0007ea90f164c78b9baae8a4`
  - [ZeroLocker](https://malpedia.caad.fkie.fraunhofer.de/details/win.zerolocker) (Ransomware): `9eaecc8bd2d980fb2f3f38a249021a97`

## Task 3 - Sandbox Analysis

### 3.1. VBS.LoveLetter Worm

1. **Malware traces, artifact, and connections**

   - Sandboxie has configurable settings to tweak the sandboxing environment

     - It creates an isolated file system under `C:\Sandbox\<USER>\<BOX>`
     - It also creates an isolated `RegistryHive` under `HKEY_USERS`

   - I updated the shown setting: Make apps think they are running elevated.

     ![image-20250406151742525](https://i.imgur.com/i1d7H12.png)

   - Then ran sandboxed malware

     ![image-20250406193044826](https://i.imgur.com/Q9Joxvp.png)

     ![image-20250406225545968](https://i.imgur.com/07g7KZ8.png)

   - The script makes file system and registry modifications as explained below, it does not make any direct network communications.

1. **Malware behavior**

     - The script starts by copying itself to Windows and System32 directories.

       ![image-20250406152146678](https://i.imgur.com/ToSHEv0.png)

       ![image-20250406152217034](https://i.imgur.com/RGyOzY9.png)


     - The WebPage (if launched in Internet Explorer), tries to run the script as well.

       ![image-20250406204229147](https://i.imgur.com/DTk8l1X.png)


     - The script modifies registry keys for auto-running on startup

       ![image-20250406202706542](https://i.imgur.com/TPhQiSc.png)


     - Internet Explorer Homepage is also updated to point to a link that downloads a  trojan (`WIN-BUGFIX.exe`). The website is not live though.

       ![image-20250406203233747](https://i.imgur.com/fv7k3hu.png)


     - To make more destruction, the virus creates copies of itself named the same as user files but with `.vbs` extension

       - By default, extensions for known file types are hidden so unsuspecting users are more likely to invoke them.


     - Showing an example from 7-Zip installation directory.

       ![image-20250406204708675](https://i.imgur.com/5KhLhRR.png)



3. **Sandbox detection:**

   - The VBScript virus does not seem to have any sandbox detection, since it executed its logic as it would have done on a normal machine. This is confirmed later during the static analysis.

4. **Memory Dump:**

   - Used DumpIt to create a Microsoft memory crash dump (renamed the file to `mem.dmp`)

     ![image-20250409021735542](https://i.imgur.com/6UjP8B8.png)

   - Downloaded volatility and obtained metadata about the dump

     ```bash
     git clone https://github.com/volatilityfoundation/volatility3
     cd volatility3
     python3 -m venv venv
     source venv/bin/activate
     pip install -e ".[dev]"
     ./vol.py -f mem.dmp windows.info
     ```

     ![image-20250409025351101](https://i.imgur.com/8OWuVQR.png)

   - Used `pstree` and `cmdline` built-in plugins to show the commands that were executed

     - It shows that the `.vbs` script was launched by `wscript.exe` parent process

     ![image-20250409025949962](https://i.imgur.com/pzx5cW1.png)

   - Listing DLLs used by `wscript.exe` process, this could give more hints about the process nature and what it does

     ```bash
     ./vol.py -f mem.dmp windows.dlllist | grep <PID> | cut -f5 | tr '\n' ' '
     ```

     - Presence of `SbieDll.dll` because process is running it in Sandboxie. 
     - Presence of `bcrypt.dll` and others may indicate crypto-related operations.

     ![image-20250409030646561](https://i.imgur.com/BR6oHpG.png)

   - We can also inspect registry hives and get more details from the dump

     ![image-20250409032008053](https://i.imgur.com/xOCtV1I.png)

5. **Hybrid-Analysis: [Public Report](https://www.hybrid-analysis.com/sample/d3f6956e01e2a4bcdbdce1b41d0f31e546a102dc384fc9e81b9f1d912e099a13/5d54238102883853829ef87a)** 

   - File was flagged as malicious by most vendors: [VirusTotal link](https://www.virustotal.com/gui/file/d3f6956e01e2a4bcdbdce1b41d0f31e546a102dc384fc9e81b9f1d912e099a13/detection).

     ![image-20250406221516963](https://i.imgur.com/6pWuuAd.png) 


      - Detected registry operations. Start page is not what we observed above, this will be clarified below during static analysis.

        ![image-20250406221409948](https://i.imgur.com/Wyt6xrL.png)


      - Detected file operations, this aligns with our findings.

        ![image-20250406221641528](https://i.imgur.com/VTMo9Zn.png)



      - Screenshot from analyzer desktop shows access request to Outlook contacts (for propagating the worm).

        ![image-20250406222415718](https://i.imgur.com/tBQ1Z2V.png)



### 3.2. ZeroLocker Ransomware

> Reference: https://stopmalvertising.com/malware-reports/introduction-to-the-zerolocker-ransomware.html

1. **Malware traces, artifact, and connections**

   - Run sandboxed malware

     ![image-20250407013203200](https://i.imgur.com/MENjRaB.png)


   - Notably, directories `assebmly` and `ZeroLocker` were created under `Windows`.

     ![image-20250407005749073](https://i.imgur.com/F8qniKH.png)


   - ZeroLocker directory contains `address.dat`. Looks like some Crypto wallet address.

     ![image-20250407005807483](https://i.imgur.com/GgTCN5o.png)


2. **Malware behavior**

   - Notable registry modification: runs an executable `ZeroRescue` on startup.

     ![image-20250407013725637](https://i.imgur.com/eziCMUv.png)


   - The executable is expected to be downloaded from the remote attacker server (as shown below), online sources show that it typically looks like this:

     ![image-20250407013750759](https://i.imgur.com/KyRWV3Z.png)


   - Meanwhile, the ransomware is having fun encrypting everything in the sandbox (including shortcuts).

     ![image-20250407010011938](https://i.imgur.com/7XaZUgT.png)


3. **Sandbox detection:** does not seem to be there (malware executes as usual)

4. **Memory dump analysis**

   - Obtained a memory dump with DumpIT (same as above), named the file `mem2.dmp`.

   - Get PID and command executed

     ![image-20250409035126274](https://i.imgur.com/cTuD0V0.png)

   - Check file handles

     ![image-20250409034609399](https://i.imgur.com/sYIqKkB.png)

   - Timeliner plugin combines output of other plugins to show a timeline of events in chronological order, helps reconstruct events: `./vol.py -f mem2.dmp timeliner`

     ![image-20250409040712275](https://i.imgur.com/wdAGS5D.png)

4. [Any.Run](https://any.run/report/7831250dea8236282df6b998789db093a9188a26a800c938e3f29fedd3ce9155/ba475786-dec4-41a6-a77d-8889e444146b#Static%20information) and [Hybrid-Analysis](https://www.hybrid-analysis.com/sample/d4c62215df74753371db33a19a69fccdc4b375c893a4b7f8b30172710fbd4cfa/6513bd75ee7c8f74f805ecb9) public reports shows additional interesting details

   - One of the `strings` in the file.

     ```txt
     Your payment has not yet been either received or processed.\r\nPlease make the payment and try again. If you've already\r\nmade a payment, note that it may take up to 24 hours\r\nto process your payment and activate your key.
     ```


   - Network activity shows some HTTP GET requests to a server in Lithuania.

     ![image-20250407014837961](https://i.imgur.com/vB3QQPF.png)

   - Network activities explained [[ref.](https://securelist.com/zerolocker-wont-come-to-your-rescue/66135/)]

     - Requests for `/patriote/sansviolence` are responsible for downloading `ZeroRescue.exe` (the executable with ransom text).
     - Requests for `/zconfig` are for obtaining an updated wallet address and other configs, default address shown above is used in case of failure.
     - Last GET request includes a CRC32 of the MAC address of the infected computer, encryption key used, and wallet address. It's unclear whether the server actually stores this information or that it's lost forever.

## Task 4 - Static Analysis

### 4.1. VBS.LoveLetter Worm

- This one is a VBScript, the source can be directly viewed/modified without the need of a special tool (though one may use visual studio for debugging).

- If needed, one may first need to deobfuscate the script, this one was more or less readable however.

- Commands to copy the script are visible.

  ![image-20250407000258433](https://i.imgur.com/kXSLpAx.png)

- Commands to modify the registry (IE homepage override) were identified as well. The malicious URL is not always set, it's a random process.

  ![image-20250407000823618](https://i.imgur.com/foK4ZvQ.png)

- Further down the file, there is the logic for infecting files explained above, it's supposed to delete the original files afterwards. Probably Sandboxie had this deletion prevented during analysis.

  ![image-20250407001329234](https://i.imgur.com/iPUcTiq.png) 

### 4.2. ZeroLocker Ransomware

- **Goal:** static analysis should give us hints about how ZeroLocker does encryption, decryption, and key generation.

- Imported the executable into Ghidra

  ![image-20250407022030766](https://i.imgur.com/s4Bb9YQ.png)

- Identified interesting functions: `ComputeChecksum`, `EncryptFiles`, `DecryptFiles`, and `UseBitcoinAddress`

  ![image-20250407023132509](https://i.imgur.com/3SdF7v3.png)

- Since it's a .NET application, I thought JetBrains dotPeek might do a better job decompiling the project into C#![image-20250407040316981](https://i.imgur.com/3O6R6ev.png)

- Observations:

  - `getPassword` function (likely the one generating encryption keys) uses the `Random` class from `mscorelib`.
  - Function names look obfuscated (not sure if that's on purpose).

- Tried a different tool: [ILSpy](https://github.com/icsharpcode/ILSpy)

  - It's open source, more lightweight than dotPeek, and displays those unicode-named functions.

  - Seems like the encryption logic is internally calling that `\u4\u2` function, which in turn utilizes **`RijandaelManaged`** from `mscorelib`

    ![image-20250407035546184](https://i.imgur.com/LwkpFpN.png)

    ![image-20250407035626981](https://i.imgur.com/pntpyIw.png)

- Further analysis should give more understanding of the process and perhaps help efforts of constructing a decryptor if possible.

### 4.3. Sandboxing vs. Static Analysis

Summary of used techniques:

|                 | Advantages                                                   | Disadvantages                                                |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Sandboxing      | Easier to deploy and observe virus behavior as it executes.  | - May be detected by malware.<br />- Not all logic  paths can be discovered. |
| Static Analysis | Reveals hidden logic that may not get triggered in sandbox analysis | More difficult to skim through and understand, especially when obfuscated |

It's a good idea to utilize a mix of both approaches: running the sandbox first to get an overall idea about the malware behavior, then diving into code to reveal hidden logic paths like what was done above.



