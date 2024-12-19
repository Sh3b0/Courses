# Lab 1 - Networking Basics

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Tools

1. Followed the [documentation steps](https://docs.gns3.com/docs/getting-started/installation/linux/) to install GNS3 GUI, server, and other dependencies on my Ubuntu machine.

   ![](https://i.imgur.com/DQLIqMI.png)

2. Downloaded "Ubuntu Cloud Guest" from [the marketplace](https://gns3.com/marketplace/appliances/ubuntu-cloud-guest) and imported the appliance file (`.gns3a`) in GNS3 (had to download initialization file `.iso` and machine image `.img`).

3. Created a blank project in GNS3 and tested the machine.

   ![](https://i.imgur.com/gvSq7EJ.png)

4. To enable Internet access for the machine, there are multiple options [[ref.](https://docs.gns3.com/docs/using-gns3/advanced/connect-gns3-internet)]

   - **Fastest option:** place a NAT node and connect the machine to it. This will create a virtual interface `virbr0` on my host and the machine would reach the Internet through it.

     ![](https://i.imgur.com/vwj45Dd.png)

   - **An alternative option** is to use a "cloud" node then configure a router to connect to it.

     - The cloud resource can be configured to use the Internet interface on the host, in this case, the connected devices would gain Internet access through that interface.

     - The router interface connected to the cloud can then be configured with an IP address in the same network as shown below
   
     - In this demo, I used [Cisco IOSv appliance](https://gns3.com/marketplace/appliances/cisco-iosv) and referred to the [docs](https://docs.gns3.com/docs/using-gns3/advanced/connect-gns3-internet) to get it to work.
   
       ![image-20241027015241097](https://i.postimg.cc/9XJsZC7w/image-20241027015241097.png)
   
     - If needed, one may then connect a PC to the router and configure IP addressing and default gateway in the same manner to provide Internet connectivity to the host.

## Task 2 - Switch

- Created the required architecture in GNS3 and installed/enabled the needed services

  - Ubuntu cloud guest machine comes with `openssh-server` pre-installed but disabled.
  - Installed Nginx using the command `apt install nginx`.

  ![image-20241027021614554](https://i.postimg.cc/SR3rdT43/image-20241027021614554.png)

- `/28` means that the network portion takes the first 28-bit of the address and the host portion takes the remaining `32 - 28 = 4` bits, allowing for a total of `2 ** 4 - 2 = 14` host.

  - The first address is reserved for the network and the last one for broadcast.

  - For the network `10.0.0.0/28`, the usable addresses will be `10.0.0.1` to `10.0.0.14`

- Assign static IPs to admin and web machine and test connectivity with `ping` and `traceroute`

  ![image-20241027023354861](https://i.postimg.cc/wjd5BsjX/image.png)

- Test web server accessibility from admin machine

  ![image-20241027023522314](https://i.postimg.cc/YSr16Kgj/image.png)

## Task 3 - Routing

- Installed the [appliance](https://gns3.com/marketplace/appliances/mikrotik-cloud-hosted-router) for MikroTik CHR
- Created the required setup with the shown address configuration.![image-20241027125902025](https://i.postimg.cc/L51f9HdQ/image.png)

- Here are the commands used to make everything work

  ```bash
  # Admin
  sudo ip a add 10.0.0.2/28 dev ens3
  sudo route add default gw 10.0.0.1 dev ens3
  ip -4 -brief a
  
  # Web
  sudo ip a add 10.0.0.3/28 dev ens3
  sudo route add default gw 10.0.0.1 dev ens3
  ip -4 -brief a
  
  # Worker
  ip 172.16.0.2/24 172.16.0.1
  show ip
  
  # Router (login with admin and no password, then create password)
  ip address add address=10.0.0.1/28 interface=ether1
  ip address add address=172.16.0.2/28 interface=ether2
  ip dhcp-client add interface=ether3 disabled=no
  ip address/print
  ```

- Connectivity verifications

  ![image-20241027131901579](https://i.postimg.cc/ryKJtXXQ/image.png)

- Configure SSH and HTTP port forwarding on the router

  ```bash
  ip firewall nat add chain=dstnat protocol=tcp dst-port=80 action=dst-nat to-addresses=10.0.0.3 to-ports=80 comment="HTTP Port Forwarding"
  
  ip firewall nat add chain=dstnat protocol=tcp dst-port=22 action=dst-nat to-addresses=10.0.0.2 to-ports=22 comment="SSH Port Forwarding"
  
  ip firewall/nat/print
  ```

- Testing SSH and HTTP connection from a worker machine

  ![image-20241027134817784](https://i.postimg.cc/ncmG04Lm/image.png)

## Bonus

- Replaced the router with an Ubuntu host

- From the configuration menu (network tab), change the number of adapters to 2 (Intel Gigabit Ethernet).

- Login to the machine and run the commands

  ```bash
  # Configure IPs on both gatway interfaces
  sudo ip a add 10.0.0.1/28 dev ens3
  sudo ip a add 172.16.0.1/24 dev ens4
  
  # Bring up interfaces
  sudo ip link set dev ens3 up
  sudo ip link set dev ens4 up
  
  # Enable IPv4 forwarding between the interfaces
  echo 1 | sudo tee /proc/sys/net/ipv4/ip_forward
  
  # Add static routes to both networks via the interfaces
  sudo ip route add 172.16.0.0/24 via 10.0.0.1 dev ens3
  sudo ip route add 10.0.0.0/28 via 172.16.0.1 dev ens4
  ```

- Now the Test client (or worker) can SSH into admin machine or access the Nginx web server through HTTP

  > Used an Ubuntu test client because the worker machine does not have a built-in SSH or HTTP clients.

  ![image-20241027152348558](https://i.postimg.cc/cLNMjvKb/image.png)
  
  
