# Lab 3 - MTA

> Ahmed Nouralla - a.shaaban@innopolis.university
>
> ID for the task: 14 (EXIM). Domain: st14.sne24.ru

[TOC]

## Task 1 - Installation

### 1. Install from source

**a) First make sure that your system does not contain a pre-installed version of the MTA of your choice, if so, remove it before you continue.**

- There was no previously installed version on my server

  ![image-20241113185535809](https://i.postimg.cc/dVJV7z8N/image.png)



**b) Make sure the source code is retrieved from a secure location. Use the official website for the MTA of your choice**

- Downloading the software and signature from  https://www.exim.org/

  ```bash
  wget https://ftp.exim.org/pub/exim/exim4/exim-4.98.tar.bz2
  wget https://ftp.exim.org/pub/exim/exim4/exim-4.98.tar.bz2.asc
  ```

  ![](https://i.postimg.cc/R0FCdKpc/image.png)

**c) Because it is important that an MTA be correct and secure it is often signed using a digital PGP signature. If your MTA is signed then make sure you have downloaded the correct sources by checking the validity of the key and the signature.**

- Importing maintainers' signatures into my keyring and using it to verify the downloaded file

  ```bash
  wget https://downloads.exim.org/Exim-Maintainers-Keyring.asc
  gpg --import Exim-Maintainers-Keyring.asc
  gpg --verify ./exim-4.98.tar.bz2.asc ./exim-4.98.tar.bz2
  ```

    ![](https://i.postimg.cc/VsMfKYgH/image.png)

- Extract the files (may need to `apt install bzip2`)

  ```
  tar -xvjf ./exim-4.98.tar.bz2
  ```

  ![image-20241113193000901](https://i.postimg.cc/qMxkdj9s/image.png)

**d) There are a number of options that you will have to enter before compilation, so that he functionality can be compiled into the program. Make sure the basic install holds all the necessary functionality. Show the options you configured.**

> References:
>
> - https://wiki.sharewiz.net/doku.php?id=exim4:install_exim4_complete
> - https://www.exim.org/exim-html-current/doc/html/spec_html/ch-building_and_installing_exim.html

- Default configuration options are available in `src/EDITME`. We will start from there.

  ![image-20241113210654140](https://i.postimg.cc/1zfPwP4B/image.png)

  ```bash
  # Install dependecncies
  sudo apt install build-essential libssl-dev libpcre2-dev libdb-dev
  
  # Create user and group for exim
  sudo groupadd -r exim
  sudo useradd -r -g exim -s /sbin/nologin exim
  
  # Copy template Makefile
  cd exim-4.98/
  cp src/EDITME Local/Makefile
  
  # Configure build options
  cat >> Local/Makefile <<- EOF
  EXIM_USER=exim
  USE_OPENSSL=yes
  TLS_LIBS=-lssl -lcrypto
  EOF
  
  # Build and install
  make
  sudo make install
  ```

- Installation successful

  ![image-20241113205050174](https://i.postimg.cc/N0StxVHc/image.png)

- Verify installation

  ![image-20241113214951374](https://i.postimg.cc/7hyyF29t/image.png)

- Create a systemd service for convenience

  ```bash
  cat >> /etc/systemd/system/exim.service <<- EOF
  [Unit]
  Description=Exim MTA
  After=network.target
  
  [Service]
  ExecStart=/usr/exim/bin/exim -bd -q60m
  
  [Install]
  WantedBy=multi-user.target
  EOF
  
  sudo systemctl daemon-reload
  sudo systemctl start exim
  ```

  

### 2. Configure

**Most of the options for an MTA can be found in a configuration file that will be loaded when the MTA starts. It is recommended to start with an example configuration that looks a lot like what you need for now. Show how you adapt it to your needs.**

- Upon installation, exim places auto-generated config files in the default config dir `/usr/exim/configure`.

- Let's leave defaults for now until we know what is required.

- **[Update]** after figuring out that we need to send local email, the following config is used.

  - I had to uncomment the shown two lines for `group` and `mode` so exim can write to `/var/mail/<user>`

  ![image-20241113222229105](https://i.postimg.cc/7hMywZQH/image.png)

### 3. Verify

**a) Add a local account on your experimental machine and make sure that the MTA can deliver mail to it. Show the required configuration.**

- Adding a local user: `sudo adduser exim-test`

  ![image-20241113215928636](https://i.postimg.cc/Qxh3ScHH/image.png)

- Checking that exim can reach the user via the local transport and delivery: `exim -bt exim-test`

  ![image-20241113220015622](https://i.postimg.cc/VNxytCxs/image.png)

**b) Add to your log an email received by this account. Do not forget the full headers!**

- Sending a test email: `echo -e "Subject: Test\n\nHello" | exim -v exim-test@ubuntu-server`

  ![](https://i.postimg.cc/HxPZQrkL/image.png)

- Verifying it has arrived at `/var/mail/exim-user`

  ![](https://i.postimg.cc/pVmx388q/image.png)

**c) Also make sure that any email intended for postmaster@st14.sne24.ru is delivered to this account. Show the full email as delivered to the new account and the required configuration**

- Modify `/etc/aliases` and add the following so emails to `exim-user` becomes the postmaster (email administrator account of the machine).

  ```
  postmaster: exim-test
  ```

- Add a local delivery configuration in `/usr/exim/configure` as follows

  ```yaml
  # add the following after "begin routers" section
  st14_sne24_ru_local:
    driver = accept
    domains = st14.sne24.ru : sne24.ru
    transport = local_delivery
  
  # modify this entry after "begin transports" section to be as follows
  local_delivery:
    driver = appendfile
    file = /var/mail/$local_part_data
    delivery_date_add
    envelope_to_add
    return_path_add
    group = mail
    mode = 0660
    user = $local_part_data
  ```

- This will redirect any local mail to postmaster@st14.sne24.ru

- However, this **does not** utilize DNS or SMTP. It's like a `/etc/hosts` and direct writing to files.

- We will fix that shortly in the next task.

  ![image-20241116000031330](https://i.postimg.cc/2yxggXV3/image.png)

  


## Task 2 - Mail Backup

|           | Name           | Email                          | Task ID   | Host IP         |
| --------- | -------------- | ------------------------------ | --------- | --------------- |
| Me:       | Ahmed Nouralla | a.shaaban@innopolis.university | 14 (EXIM) | 192.168.122.190 |
| Teammate: | Mohamad Bahja  | m.bahja@innopolis.university   | 18 (EXIM) | 192.168.122.119 |

### Setup DNS and Connectivity [4a, 5a]

> **4a) Adapt the DNS information for your domain, so that the backup MTA on your partner’s server can be found**
>
> **5a) Make your MTA act as a backup for your partner’s domain.**

- Let's setup connectivity between my host and my teammate's

- To isolate DNS and MTA logic for easier understanding, we configured a common BIND (`named`) server that is:

  - Authoritative (master) for both zones: `st14.sne24.ru` and `st18.sne24.ru` 
  - Reachable by both our hosts to resolve the IP of the other host and it's mail server.
  - Responsible for the following resource records (essential ones are shown from zone files).

  ![image-20241116012030431](https://i.postimg.cc/J7cwytPm/image.png)

- Ordering of MX records means that when anyone is emailing a host, the DNS server will obviously prefer to redirect them to the mail server of that host (priority 10).

  - However, when the machine is down, the DNS server would **fall back to priority 20** and use the partner's machine instead.

  - Verifying DNS config and reachability on both our machines (showing connection over SSH)

    ![](https://i.postimg.cc/KzdKsptB/image.png)

  - Verifying with `host` command and DNS logs on the server

    ![image-20241116015307853](https://i.postimg.cc/vHQJ5MNn/image.png)


### Testing with SMTP and DNS

- Modify the `/etc/aliases` to make `ahmed` the postmaster for my machine
  - Also, delete (or comment out) the local route used previously so the local route is no longer used.

- Sending an email from `mohamed@st18.sne24.ru` to `ahmed@st14.sne24.ru` (Yay!)

  - **On the left:** the received timestamped message and exim logs on receiver (my machine)
  - **On the right:** the sender command with complete timestamped SMTP session at sender.

  ![image-20241116024346113](https://i.postimg.cc/sDwtc8kN/image.png)

  

### Testing Backup Functionality [4b, 4c, 5b, 5c]

**4b) Validate by shutting your service down and sending a message to your domain (...your partner sees its logs and where the message is temporarily stored...)**

- Shutting down my listener

  ![image-20241116031429908](https://i.postimg.cc/RZY2rtV1/image.png)

- Sending a message from mohamed. It's stuck in queue as shown.

  ![image-20241116031400018](https://i.postimg.cc/VNbpzNdx/image.png)

**4c) Bring your service back up and wait**

- Brought it back up and nothing happens, the message it still stuck.

- Probably some retry interval config option should have been specified.

**5b) Show the logs while doing your mate’s acceptance test and show where the message is temporarily stored.**

- The message is temporarily stored in the outgoing queue as shown

  ![image-20241116034717072](https://i.postimg.cc/V6QTvctd/image.png)

- Sender logs

  ![image-20241116035129947](https://i.postimg.cc/KjQs0kHM/image.png)

**5c) Once your partner’s MTA is back online, eventually force an immediate delivery and show your mail logs.**

- We can force retry delivering as shown.

  ![image-20241116032056225](https://i.postimg.cc/W1df746d/image.png)

- Queue is emptied and message has arrived

  ![image-20241116032130318](https://i.postimg.cc/g21ttPNN/image.png)

- Timestamped sender logs show that message was held for about 10 minutes (downtime) before being finally delivered.

  ![](https://i.postimg.cc/s2ChxnTT/image.png)



## Task 3 - Transport Encryption

### 6. Questions
a) Which one is better, SSL/TLS or STARTTLS, why?

- Comparison:
  - SMTPS (SMTP over SSL/TLS) means that SMTP communications (port 456) are encrypted upon connection using TLS.
  - STARTTLS begins with plaintext SMTP (port 25) connection then upgrades to a TLS connection (port 587) for data.


- Conclusion
  - SMTPS is older and not compatible with listeners at port 25. Yet it's more secure as every thing is encrypted.
  - STARTTLS is newer and more popular, but is susceptible to downgrade attacks if improperly configured.  

b) Which one is actually in use for SMTP?

- STARTTLS is used in our case as shown above in the SMTP session.

  ![image-20241116050829198](https://i.postimg.cc/vm9qpSKM/image.png)

  

### 7. Task

a) Add transport encryption to your MTA.

- The communication already uses STARTTLS for transport encryption as shown above.
- By default is uses self-signed certificates but we can change that by specifying the options for `tls_certificate` and `tls_privatekey`.
- To use SMTPS (TLS on connection), we can uncomment the line `tls_on_connect_ports = 465` in exim config.

b) Eventually force the transport to be encrypted only (refuse non encrypted transport).

- To refuse unencrypted connections, disable port 25 for plain SMTP by updating the config key `daemon_smtp_ports = 456`

- Alternatively, configure an access control rule:

  ```bash
  # Add the rule in the "begin acl" section
  
  acl_check_connect:
    accept  condition = ${if eq{$tls_cipher}{} {no} {yes}}
    deny    message = "TLS encryption is required for this connection"
  ```

c) Proceed with validation (proof or acceptance testing), as usual.

- Sent another email from `mohamed@st18.sne24.ru` to `ahmed@st18.sne24.ru` while capturing the traffic with Wireshark.
- The communication is fully encrypted with TLS as shown.
- Wireshark (as brilliant as it is) somehow figured out that encrypted payload contains SMTP as highlighted down there.

![image-20241116050200213](https://i.postimg.cc/SshTqf09/image.png)



## Task 7 - SPF and DKIM

15- Write a small paragraph that highlights the advantages and disadvantages of SPF and DKIM. What would you choose at a first glance and why?

- SPF (Sender Policy Framework) and DKIM (DomainKeys Identified Mail) are email authentication methods used to reduce email spoofing (e.g., you know, to avoid getting random mails from trump@whitehouse.us)
  - SPF allows domain owners to specify which mail servers are authorized to send email on behalf of their domain, reducing the risk of unauthorized senders.
  - However, SPF can be circumvented if the email is forwarded, as the forwarding server might not match the SPF record.
- DKIM uses cryptographic signatures to verify that the email content hasn’t been tampered with, offering stronger protection against email content alteration.
  - However, DKIM requires more configuration and the management of private keys.

- At first glance, DKIM seems to provide stronger security, though both technologies look complementary and I don't see a reason why one may not utilize both if needed.

