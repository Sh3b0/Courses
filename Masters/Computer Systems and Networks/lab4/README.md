# Lab 4 - Networking Reconnaissance and Analysis
> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Wireshark

> Reference: https://www.wireshark.org/docs/wsug_html_chunked/

### 1.1. Capture The Flag

- Downloaded the `.pcapng` file and opened it in Wireshark.

- I can see plaintext FTP communication over TCP between `10.142.0.3` and `104.155.183.43`, followed by TLS 4-way handshake and further encrypted communication.

  - The server is sending a file `key.zip` to the client. The highlighted packet contains the file being transferred.

  - We can use Wireshark (File -> Export Options -> FTP Data) to dump the file content as shown

    ![](https://i.postimg.cc/5ysShnb7/image.png)

- The file contained a private RSA key as shown

  ![](https://i.postimg.cc/cCLQBwwQ/image.png)

- Let's try using the obtained private key to decrypt the TLS communication in Wireshark

  - Edit -> Preferences -> RSA Keys -> Add new keyfile -> Choose the downloaded `server_key.pem`

    ![image-20241110051056331](https://i.postimg.cc/vBRY9yPD/image.png)

  - We can see decrypted communication. It shows HTML file referencing an image file `flag.jpg` as shown

    ![image-20241110051329166](https://i.postimg.cc/wjGTqLLX/image.png)

  - File -> Export Options -> HTTP Data

    ![image-20241110051644027](https://i.postimg.cc/YC9CBbG2/image.png)

    ![flag](./flag.jpg)

### 1.2. Filter Ping Traffic

- Open Wireshark and start capturing packets on all interfaces. 

- Open a terminal and run `ping 8.8.8.8`

- From Wireshark, filter traffic using the string `ip.addr == 8.8.8.8` 

  ![](https://i.postimg.cc/P5T8WmHN/image.png)

## Task 2 - Nmap

> Reference: https://nmap.org/book/man.html

- The screenshot shows three scans

  1. Scanning my machine (`localhost`) with Nmap (default parameters). I've got a couple of services running.

  2. A full-port scan, shows more non-standard ports used.
  3. A service Version scan for port 22 shows I use OpenSSH server version 9.6p1.

  ![image-20241110052859115](https://i.postimg.cc/ncRt2xb7/image.png)

- When scanning a Windows system Nmap may stop the scan and report that the host is down, what can you do to solve this issue?

  - If we suspect the host may be blocking our ping requests, we can use `-Pn` flag that skips host discovery and proceeds to scan the host even when it does not respond to ping.
  - If we suspect the host may just be slow to respond, we can increase the scan timeout with `--host-timeout`
  - We may as well try different scan types (e.g., `-sT` for TCP scan or `-sS` for stealth scan)

- Scanning the small network in my room shows results for the gateway (access point), my PC, and smart phone.

  ![image-20241110053734177](https://i.postimg.cc/RZMn67W2/image.png)

## Task 3 - Reconnaissance

**There are two types of reconnaissance, active and passive. What are the differences between them? Which one would you use?** 

- Active recon involves the attacker actively interacting with the target system to scan it, it may alert the target, but it also provides useful information.
- Passive recon avoids direct communication and gathers publicly available information about the target. It's more stealthy, but may not yield useful information.
- One may start with passive recon and proceeds to sneaky/polite/normal active recon to gather more information as needed.

**Can you do a passive scan of the local subnet that is connected to your PC?**

If an attacker is interested in a target that is physically nearby, they can use passive methods to capture nearby traffic.

- In case of a wireless network, they may use an adapter that supports **monitoring** **mode**, which allows them to capture traffic not intended for them and learn more about the activity of nearby devices.
- In case the attacker connected to the same network, they may enable **promiscuous** **mode**, which achieves a similar idea by allowing the NIC to process frames not intended for it. 

