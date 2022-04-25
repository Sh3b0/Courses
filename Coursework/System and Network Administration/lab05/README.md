# SNA Lab 5: Subnetting and Linux Networking

## Exercise 1 Questions

1. You have a range 172.16.200.0/22

   1. Identify subnet range

      > - A subnet of /22 means that we can use only the last 32-22=10 bits of the IP address for hosts, broadcast, and the network address.
      > - (172.16.200.0)<sub>10</sub> = (10101100.00010000.11001000.00000000)<sub>2</sub>
      >
      > - **First (Network) IP:** 10101100.00010000.110010**00.00000000** = 172.16.200.0
      > - **Last (Broadcasting) IP:** 10101100.00010000.110010**11.11111111** = 172.16.203.255

   2. How many usable IP addresses?

      > - 2<sup>32-22</sup> = 2<sup>10</sup> = 1024 available IP addresses.
      > - Two of which are reserved for network and broadcast addresses
      > - So 1022 usable IP addresses for hosts.

   3. Identify starting IP and ending IP

      > - Block size = 2<sup>24-22</sup> = 2<sup>2</sup> = 4 which lies in the 3rd octet.
      > - **First host IP:** 172.16.200.0 + 1 = 172.16.200.1
      > - **Last host IP:** 172.16.204.0 - 2 = 172.16.203.254

   

2. You have a range 10.16.200.12/17

   1. Identify subnet range

      > - A subnet of /17 means that we can use only the last 32-17 = 15 bits of the IP address for hosts, broadcast, and the network address.
      > - (10.16.200.12)<sub>10</sub>=(00001010.00010000.11001000.00001100)<sub>2</sub>
      >
      > - Starting IP: 00001010.00010000.1**0000000.00000000** = 10.16.128.0
      > - Ending IP: 00001010.00010000.1**1111111.11111111** = 10.16.255.255

   2. How many usable IP addresses?

      > - 2^<sup>32-17</sup> = 2<sup>15</sup> = 32768 available IP addresses.
      > - Two of which are reserved for network and broadcast addresses
      > - So 32766 usable IP addresses for hosts.

   3. Identify starting IP and ending IP

      > - Block size = 2<sup>24-17</sup> = 2<sup>7</sup> = 128 which lies in the 3rd octet.
      > - First network has to be a multiple of 128, so 200 goes back to 128
      > - **Starting IP:** 10.16.128.1 + 1 = 10.16.128.1
      > - **Ending IP:** 10.16.256.0 - 2 = 10.16.255.254

      

3. You have a range 192.168.0.0/24. Divide into small subnets such that there will be a subnet with 29 hosts, another with 120 hosts, and a third with 60 hosts.

      >   We can divide the range into two subnets:
      >
      >   - 192.168.0.0/25 with hosts from 192.168.0.1 to 192.168.0.126
      >
      >   - **192.168.0.128/25 with hosts from 192.168.0.129 to 192.168.0.254**
      >     - We can further divide this one into two subnets:
      >       - **192.168.0.128/26 with hosts from 192.168.0.129 to 192.168.0.190**
      >       - 192.168.0.192/26 with hosts from 192.168.0.193 to 192.168.0.254
      >         - We can further divide this one into two subnets:
      >           - **192.168.0.192/27 with hosts from 192.168.0.193 to 192.168.0.223**
      >           - 192.168.0.224/27 with hosts from 192.168.0.225 to 192.168.0.254
      >   - Only the **bolded** ones can be used to accommodate 120, 60, and 29 hosts, respectively, the rest can be reserved for future use.



## Exercise 2 Questions

1. Add several IP addresses to interface using by `netplan` and ping them.

   > We can have the following YAML configuration file for an interface with multiple addresses
   >
   > ```yaml
   > network:
   >   version: 2
   >   renderer: networkd
   >   ethernets:
   >     ens3:
   >       dhcp4: no
   >       addresses: [192.168.31.1/24, 192.168.31.2/24]
   >       gateway4: 192.168.31.0
   >       nameservers:
   >           addresses: [8.8.8.8, 1.1.1.1]
   > ```
   >
   > Then do `netplan try` to test for syntax errors
   >
   > When all is OK, do `netplan apply` to apply the changes (in some cases you might need to reboot)
   >
   > We can then `ping 192.168.31.1` or `ping 192.168.31.2` and receive a response 
   >
   > ```bash
   > PING 192.168.31.1 (192.168.31.1) 56(84) bytes of data.
   > 64 bytes from 192.168.31.1: icmp_seq=1 ttl=64 time=0.958 ms
   > 64 bytes from 192.168.31.1: icmp_seq=2 ttl=64 time=15.1 ms
   > 64 bytes from 192.168.31.1: icmp_seq=3 ttl=64 time=69.2 ms
   > 64 bytes from 192.168.31.1: icmp_seq=4 ttl=64 time=1.47 ms
   > ...
   > ```
