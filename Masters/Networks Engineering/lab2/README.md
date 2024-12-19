# Lab 2 - IPv4 & IPv6

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Ports and Protocols

Given the slightly modified network topology from the previous lab

![](https://i.postimg.cc/Rhyh2WS9/image.png)



1. From the admin or web machine, we can use `lsof` or `netstat` to verify the status of listening socket and open ports as shown

   ![](https://i.postimg.cc/7ZN3WJt0/image.png)

2. Scanning the router from outside using `nmap` shows many open ports

   ![](https://i.postimg.cc/9MsZ7dz5/image.png)

3. Let's configure NAT, disable unused services and try scanning again.

   ```bash
   # Forwarding HTTP traffic from outside to Web machine
   ip firewall nat add chain=dstnat protocol=tcp dst-address=0.0.0.0/0 dst-port=80 action=dst-nat to-addresses=10.0.0.2 to-ports=80 comment="HTTP Port Forwarding"
   
   # Forwarding SSH traffic from Web/Admin to Worker
   ip firewall nat add chain=dstnat protocol=tcp dst-address=10.0.0.1/28  dst-port=22 action=dst-nat to-addresses=172.16.0.2 to-ports=22 comment="Worker SSH Port Forwarding"
   
   # Forwarding SSH traffic from outside to Admin machine
   ip firewall nat add chain=dstnat protocol=tcp dst-address=192.168.122.0/24  dst-port=22 action=dst-nat to-addresses=10.0.0.3 to-ports=22 comment="SSH Port Forwarding"
   
   # Disable unused services (ports 21, 2000, 8291, and 23 in order)
   ip service disable ftp
   tool/bandwidth-server/set enabled=no
   ip service disable winbox
   ip service disable telnet
   ```

   ![](https://i.postimg.cc/htV1kYYN/untitled.png)

4. Scanning the worker from admin shows open SSH port as expected

   ![](https://i.postimg.cc/g24hDFrK/image.png)

   - Let's block ICMP traffic on worker and change SSH port to 55555

     ```bash
     # Block ping requests
     sudo iptables -A INPUT -p icmp --icmp-type echo-request -j DROP
     
     # Change SSH port
     sudo nano /etc/ssh/sshd_config # change port to 55555
     sudo systemctl restart sshd
     ```

   - Rescan with default settings may not detect the SSH on non-standard port. We can force full port scanning with `nmap -p 1-65535 <target>`. However, it would be slow.

     ![](https://i.postimg.cc/90W2QTbC/image.png)

   - We can also do service detection with nmap. The following scan shows the versions for nginx and OpenSSH servers being deployed on the web machine, as well as other service-specific information like HTTP page title and SSH host key.

     ![](https://i.postimg.cc/ZRmtw31p/image.png) 

## Task 2 - Traffic Capture

### Task 2.1 - HTTP Capture

- Using Wireshark to capture GNS traffic, we can filter by HTTP, then access the Nginx server deployed on the Web machine from outside to see the requests being made.

- We can see the complete HTTP request and response being exchanged with all the headers and metadata.

  - An HTTP request is made from source IP (192.168.122.1) to destination IP (192.168.122.234)
  - The request was made by "curl" user agent
  - Curl had no restrictions on the types accepted responses
  - Nginx server responded with HTTP code 200 OK and headers for date, content type, and length. The connection is persistent (keep alive).
  - The full HTML content for the nginx welcom page follows.

  ![](https://i.postimg.cc/v8jXMMZS/image.png)

  ![](https://i.postimg.cc/NMF7qPqc/image.png)

### Task 2.2 - SSH Capture

- Let's do the same to capture SSH traffic to the admin machine from outside.

  - Unlike HTTP, we cannot get much info from here since SSH is a secure protocol.
  - Information exchanged between client and server is encrypted with the use of cryptographic keys.
  - Diffie-Hellman Algorithm is used here to exchange secret keys between client and server.
  - Both client and server present their supported cipher suite (potential algorithms for hashing and encryption as seen in `*_key_algorithms` and `*_encryption_algorithms`)
  - It can be seen in subsequent requests that parties did agree on communicating using [ChaCha20-Poly1305](https://en.wikipedia.org/wiki/ChaCha20-Poly1305) encryption algorithm.

  ![](https://i.postimg.cc/MK9Bxm5Z/image.png)

### Task 2.3 - Burp Suite

The HTTP request done in task 2.1 can be intercepted and modified in burp suite.

- The image shows that changing the target path from `/` to `/404` resulted in a different webpage (in this case, error page) returned by server.
- It was possible to see and modify request content because plain HTTP (without SSL/TLS) does not encrypt data on the wire, unlike SSH inspected above.

![](https://i.postimg.cc/76BSVTLJ/image.png)

## Task 3 - IPv6

1. Enable IPv6 on the router and add static routes for simplicity

   ```bash
   ipv6 settings set disable-ipv6=no
   ipv6 settings set accept-redirects=yes
   ipv6 route add dst-address=fe80::e50:b7ff:fe35:0 gateway=ether1
   ipv6 route add dst-address=fe80::eff:fcff:feba:0 gateway=ether1
   ipv6 route add dst-address=fe80::e11:78ff:feb4:0 gateway=ether2
   ```

   ![](https://i.postimg.cc/kgjc5tN9/image.png)

2. IPv6 Packet Capture

   - On the "Web" machine, use `tcpdump` to capture HTTP traffic.

   - On the "Admin" machine, use `curl` to access the web app using its IPv6 address

     ![](https://i.postimg.cc/k5zB1nCW/image.png)

   - Capture file (.pcap) for the above interaction is attached to the report. Shown below in wireshark.

     - The application data (HTTP messages) are not different from the case with IPv4, except for the Host header, which shows the IPv6 address (eight 16-bit fields, each represented by a 4-digit case-insensitive hexadecimal number, separated by a colon)
     - IPv6 packets uses 128-bit addressing, header size is larger (with flow label and traffic class information), and may have performance benefits in some cases.

     ![image-20241101004621562](https://i.postimg.cc/W4gwwfLp/image.png)

3. IPv6 Addresses Compressing and Decompressing rules

   - Leading zeros can be omitted.
   - Successive fields of zeros are represented by `::` but only once to avoid ambiguity
   - Convention is to use `::` only once with the rightmost set of consecutive zeros.
   - Special addresses `::1` for `localhost` decompresses to all zeros followed by a single 1

   IPv6 addresses used in the demonstration

   | Machine | Compressed Form         | Full Form                       |
   | ------- | ----------------------- | ------------------------------- |
   | Admin   | `fe80::e50:b7ff:fe35:0` | `fe80:0000:0e50:b7ff:fe35:0000` |
   | Web     | `fe80::eff:fcff:feba:0` | `fe80:0000:0eff:fcff:feba:0000` |
   | Worker  | `fe80::e11:78ff:feb4:0` | `fe80:0000:0e11:78ff:feb4:0000` |

   
