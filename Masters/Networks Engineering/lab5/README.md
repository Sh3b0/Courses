# Lab 5 - QoS

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Preparation

- Created the following infrastructure in GNS3 with [Cisco 7200](https://gns3.com/marketplace/appliances/cisco-7200) Router (R1).

  ![image-20241122074540905](https://i.postimg.cc/QMJT7R04/image.png)

- Router configurations

  ```ruby
  R1> enable
  R1⋕ conf t
  
  # Enable DHCP on the cloud-connected interface
  R1 (config)⋕ interface g0/0
  R1 (config-if)⋕ ip address dhcp
  R1 (config-if)⋕ no shut
  
  # Configure static IPs on other router interfaces
  R1 (config-if)⋕ interface g1/0
  R1 (config-if)⋕ ip address 10.0.1.1 255.255.255.0
  R1 (config-if)⋕ no shut
  R1 (config-if)⋕ interface g2/0
  R1 (config-if)⋕ ip address 10.0.2.1 255.255.255.0
  R1 (config-if)⋕ no shut
  R1 (config-if)⋕ interface g3/0
  R1 (config-if)⋕ ip address 10.0.3.1 255.255.255.0
  R1 (config-if)⋕ no shut
  R1 (config-if)⋕ interface g4/0
  R1 (config-if)⋕ ip address 10.0.4.1 255.255.255.0
  R1 (config-if)⋕ no shut
  
  # Configure NAT for internet connectivity to hosts
  R1 (config)⋕ access-list 1 permit 10.0.0.0 0.0.255.255
  R1 (config)⋕ ip nat inside source list 1 interface GigabitEthernet0/0 overload
  R1 (config)⋕ interface g0/0 
  R1 (config-if)⋕ ip nat outside
  R1 (config-if)⋕ interface range g1/0 , g2/0 , g3/0 , g4/0
  R1 (config-if-range)⋕ ip nat inside
  ```

- Results

  ![image-20241122075146384](https://i.postimg.cc/SsZYTc0c/image.png)

- Host configurations

  ```bash
  # Replace X with 1,2,3,4 to configure the four hosts
  sudo hostname PCX
  sudo ip a add 10.0.X.2/24 dev ens3
  sudo ip route add default via 10.0.X.1 dev ens3
  ```

- PC1/PC2 and internet reachability

  ![image-20241122170948521](https://i.postimg.cc/Y0hGJjkZ/image.png)

- Update `/etc/resolv.conf` with `nameserver: 8.8.8.8` for DNS to work.

## Task 2 - QoS learning & configuring

**1- Briefly answer the questions or give one-line description what is it:**

- **Class of Service (CoS)**: A method to categorize and prioritize traffic at the data link layer (e.g., Ethernet).
- **Type of Service (ToS)**: An 8-bit field in the IPv4 header used for traffic prioritization and quality of service (QoS) decisions.
- **Differentiated Services Code Point (DSCP)**: A 6-bit field in the IP header specifying the QoS level for a packet.
- **Serialization**: The process of converting data into a sequence of bits for transmission over a network.
- **Packet Marking**: Assigning specific values (e.g., DSCP or CoS) to packets for QoS purposes.
- **Tail Drop**: A queue management policy that drops packets when the queue is full, starting with the last packets.
- **Head Drop**: A queue management policy that drops the earliest packets in a queue when it becomes full.
- **The Leaky Bucket Algorithm**: A traffic shaping mechanism that ensures a steady data flow by releasing packets at a constant rate.
- **The Token Bucket Algorithm**: A traffic policing and shaping algorithm allowing bursty data transmission up to a predefined token limit.
- **Traffic Shaping**: Controlling the rate and flow of outbound traffic to meet QoS policies.
- **Traffic Policing**: Monitoring and enforcing compliance with traffic rate limits, often dropping or remarking excess traffic.

**2- Configure your network as you decided above. After your network is configured, try to set a speed limitation (traffic shaping) between the two hosts.**

- Router configuration

  ```ruby
  # Create a class map to identify the traffic to shape
  R1(config)⋕ class-map match-any c1
  R1(config-cmap)⋕ match access-group 101
  R1(config-cmap)⋕ exit
  
  # Use an access control list (ACL) to define the traffic
  R1(config)⋕ access-list 101 permit ip any any
  
  # Use a policy map to apply the shaping (1Mbps limit)
  R1(config)⋕ policy-map p1
  R1(config-pmap)⋕ class c1
  R1(config-pmap-c)⋕ shape average 1000000
  R1(config-pmap-c)⋕ exit
  R1(config-pmap)⋕ exit
      
  # Attach the policy to an outbound interface
  Router(config)⋕ interface range g1/0 , g2/0 , g3/0 , g4/0
  Router(config-if-range)⋕ service-policy output p1
  Router(config-if-range)⋕ exit
  ```

- Show policy-map

  ![](https://i.postimg.cc/50LSDYty/image.png)



**3- Run a bandwidth testing tool, see what is the max speed you can get and verify your speed limitation. Compare the speed between the different hosts.**

- Bandwidth testing between PC1 and PC2 before traffic shaping

  ![](https://i.postimg.cc/t4KPbvPC/image.png)

- After traffic shaping: we can see bitrate is being capped.

  ![image-20241125165405287](https://i.postimg.cc/7PWTS1nk/image.png)

**4- While your bandwidth test is still running, try to download a file from one host to the other host and see what is the max speed you can get. If you have more than two hosts on the network, play around with different speed values and show it.**

- While bandwidth test between PC1 and PC2 is running. I download a file from PC3 to PC4. The speed is even more capped as shown by `iftop` (PC3) and `scp` (PC4).

  - On PC3, rate of 991Kbps is shown below 
  - On PC4, download rate is shown. Note that 128KBps = 1024Kbps = 1Mbps

  ![image-20241125173124549](https://i.postimg.cc/DwqJV9kv/image.png)

**5- Deploy and verify your QoS rules to prioritize the downloading of a file (or any other scenario) over the bandwidth test.**

- Router configuration to prioritize download (SCP) over bandwidth test (iperf3)

  ```ruby
  # Create class maps to distinguish scp/iperf traffic
  R1(config)⋕ class-map match-any scp
  R1(config-cmap)⋕ match access-group 102
  R1(config-cmap)⋕ class-map match-any iperf
  R1(config-cmap)⋕ match access-group 103
      
  # Use an access control list (ACL) to define the traffic
  # SCP port 22 and iperf port 5201
  R1(config)⋕ access-list 102 permit tcp any any eq 22
  R1(config)⋕ access-list 103 permit tcp any any eq 5201
      
  # Update policy map with the new config
  # (scp -> iperf -> fair-queue for other traffic)
  R1(config)⋕ policy-map p1
  R1(config-pmap)⋕ no class c1    
  R1(config-pmap)⋕ class scp
  R1(config-pmap-c)⋕ priority level 1
  R1(config-pmap)⋕ class iperf
  R1(config-pmap-c)⋕ priority level 2
  R1(config-pmap-c)⋕ class class-default
  R1(config-pmap)⋕ fair-queue
  ```

- Show policy-map

  ![](https://i.postimg.cc/rw7hbL9h/image.png)

- Running the same test from previous question: while bandwidth test between PC1 and PC2 is running, download a file from PC3 to PC4. 

- Test shows a gradual decrease in bandwidth as the download is prioritized by the router.

  ![image-20241125181428880](https://i.postimg.cc/wjVNyCWW/image.png) 

  

**7- What is the difference between the QoS rules to traffic allocation and priority-based QoS? Try to set up each of them and show then them. In which tasks of this lab do you use one or the other?**

- To implement QoS, we may explicitly shape traffic using `shape average` command to cap the bit rate on certain interfaces. This was implemented in <u>Task 2</u>.
  - It's useful to prevent a traffic-intensive process from consuming most of the bandwidth from others.
- Alternatively, we can only specify that certain classes (types) of traffic should be prioritized over others. This was implemented in <u>Task 5</u>.
  - It's useful to deliver more important traffic first (e.g., VoIP goes before Email) in case of congestion.

**8- Packet drops can occur even in an unloaded network where there is no queue overflow. In what cases and why does this happen?**

- A router may drop packets for several reasons other than a full buffer. These reasons include

  - Strict QoS mechanisms in place like traffic policing and rate limiting: we can specify `exceed-action drop` over a policy.
  - Congestion control mechanisms (e.g., TTL expiry): the packet may have been roaming the network for multiple hops. The router may decide to drop it to avoid congestion.

  - Firewalls / Packet filters: a router may be explicitly configured to block certain types of traffic.
  - Malformed packets: packets that the router was not able to process and understand. 
  - Issue with the router: the router itself may be buggy or misconfigured.

## Task 3 - QoS verification & packets analysis

**1- How can you check if your QoS rules are applied correctly? List and describe the various methods.**

- Inspect router config (as shown above) with commands: `show policy-map` and `show policy-map interfaces`
- Inspect a certain interface. E.g.,`show interface g0/0`.
- Inspect traffic `show ip traffic` or with Wireshark.
- Practically verify from hosts  (e.g., with `iperf3`, `iftop`, `ping`, `scp`, etc.) as shown above.

**2- Try to use Wireshark to see the QoS packets. How does this depend on the number of routers in the network topology?**

- The IPv4 header contained an 8-bit field using for QoS

  - [Historically](https://en.wikipedia.org/wiki/Type_of_service#Allocation), it contained "IP precedence" value and related fields.
  - Currently, it contains two fields: Differentiated Services Code Point (DSCP) and Explicit Congestion Notification (ECN).

- Configure the router following the same steps shown above to setup a traffic class and configure a DSCP value for it according to the type of traffic [[Wiki ref.](https://en.wikipedia.org/wiki/Differentiated_services#Class_Selector), [Cisco ref.](https://www.cisco.com/web/sbtg/gui_mockups/RV110W/dscp.asp.htm)]

  ![image-20241125220734026](https://i.postimg.cc/CLpnvhXP/image.png)

-  For the example shown (with SCP/SSH), we can set:

  ```ruby
  R1(config)⋕ policy-map p1 
  R1(config-pmap)⋕ class scp
  R1(config-pmap-c)⋕ set dscp cs2
  ```

-  The DSCP value can be seen in Wireshark. If DSCP was specified on different types of traffic, it should help the router implement priorities for QoS.

  ![image-20241125220947423](https://i.postimg.cc/15v7GjYz/image.png)

- As more routers are added to the topology, it gets more complicated to configure QoS as different routers may downgrade or overwrite priority values based on their QoS configuration.
  - However, DSCP values should be preserved across hops.
