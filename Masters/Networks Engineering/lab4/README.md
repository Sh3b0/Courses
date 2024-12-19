# Lab 4 - OSPF

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Preparation

- Installed IOS for [Cisco 7200](https://gns3.com/marketplace/appliances/cisco-7200) from GNS3 marketplace

- Created the network from variant B

  - Only used different router names and square-looking topology
  - The light bulb is there just because it look nice :)

  

  ![image-20241112201141211](https://i.postimg.cc/PqrsZXrB/image.png)

- Router Configurations (only assigning static IPs but not static routes)

  ```ruby
  R1⋕ conf t
  R1 (config)⋕ interface g0/0
  R1 (config-if)⋕ ip address 10.0.0.1 255.255.255.252
  R1 (config-if)⋕ no shut
  R1 (config-if)⋕ interface g1/0
  R1 (config-if)⋕ ip address 10.0.3.2 255.255.255.252
  R1 (config-if)⋕ no shut
  
  R2⋕ conf t
  R2 (config)⋕ interface g0/0
  R2 (config-if)⋕ ip address 10.0.0.2 255.255.255.252
  R2 (config-if)⋕ no shut
  R2 (config-if)⋕ interface g1/0
  R2 (config-if)⋕ ip address 10.0.1.1 255.255.255.252
  R2 (config-if)⋕ no shut
  
  R3⋕ conf t
  R3 (config)⋕ interface g0/0
  R3 (config-if)⋕ ip address 10.0.1.2 255.255.255.252
  R3 (config-if)⋕ no shut
  R3 (config-if)⋕ interface g1/0
  R3 (config-if)⋕ ip address 10.0.2.1 255.255.255.252
  R3 (config-if)⋕ no shut
  
  R4⋕ conf t
  R4 (config)⋕ interface g0/0
  R4 (config-if)⋕ ip address 10.0.3.1 255.255.255.252
  R4 (config-if)⋕ no shut
  R4 (config-if)⋕ interface g1/0
  R4 (config-if)⋕ ip address 10.0.2.2 255.255.255.252
  R4 (config-if)⋕ no shut 
  ```

- Only **L**ocal (loopback) and **C**onnected (directly adjacent) routes are learned. Showing routes on all routers

  ![image-20241112215921604](https://i.postimg.cc/9f83Y6Dz/image.png)

- Now for example, **R1 can ping R2 or R4** (on their g0/0), but **R1 cannot ping R3** (on any remote interface), until we configure OSPF.

  ![image-20241112203006048](https://i.postimg.cc/NMSWWR7c/image.png)



## Task 2 - OSPF Learning & Configuring

**1- Deploy OSPF in your chosen network topology.**

- Router configurations to enable OSPF, set router IDs, and advertise the networks it can reach so other routers can update their tables.

  ```ruby
  R1⋕ conf t
  R1(config)⋕ router ospf 1
  R1(config-router)⋕ router-id 1.1.1.1
  R1(config-router)⋕ network 10.0.0.0 0.0.0.3 area 0
  R1(config-router)⋕ network 10.0.3.0 0.0.0.3 area 0
  
  R2⋕ conf t
  R2(config)⋕ router ospf 1
  R2(config-router)⋕ router-id 2.2.2.2
  R2(config-router)⋕ network 10.0.0.0 0.0.0.3 area 0
  R2(config-router)⋕ network 10.0.1.0 0.0.0.3 area 0
  
  R3⋕ conf t
  R3(config)⋕ router ospf 1
  R3(config-router)⋕ router-id 3.3.3.3
  R3(config-router)⋕ network 10.0.1.0 0.0.0.3 area 0
  R3(config-router)⋕ network 10.0.2.0 0.0.0.3 area 0
  
  R4⋕ conf t
  R4(config)⋕ router ospf 1
  R4(config-router)⋕ router-id 4.4.4.4
  R4(config-router)⋕ network 10.0.2.0 0.0.0.3 area 0
  R4(config-router)⋕ network 10.0.3.0 0.0.0.3 area 0
  
  # Inspection commands
  Router⋕ show ip route
  Router⋕ show ip ospf neighbor
  ```

- Showing learned OSPF routes and neighbor status on all routers.

![image-20241112221317462](https://i.postimg.cc/Dz00zZ7j/image.png)

- Now R1 can reach R3 on any of its interfaces.

  ![image-20241112221828162](https://i.postimg.cc/sfZF50sD/image.png)

**2- Which interface you will select as the OSPF router ID and why?**

- OSPF (unless configured) will automatically set the router ID for each router to be the highest IP on a loopback interface (e.g., R1 in my case should have 10.0.3.2 as router ID)
- To avoid confusing and potentially changing IDs, I choose to set the following to make sense: (R1: 1.1.1.1, R2: 2.2.2.2, R3: 3.3.3.3, R: 4.4.4.4). There should be no restrictions on a router ID other than it being a valid and unique address within the domain.

**3- What is the difference between advertising all the networks VS manual advertising (per** **interface or per subnet)? Which one is better?**

- One could have just used a single `network` command on all routers to advertise the complete 10.0.0.0/16 network to others as: `network 10.0.0.0 0.0.255.255 area 0`
  - This is generally more popular with less chance for human error and easier configuration
  - However, one may accidentally over-advertise to networks that are not reachable 
- Manual approach (used above), specifies exactly which subnets to advertise by each router.
  - It has more configuration overhead with possibility of mistakes
  - However, it is more precise and avoids over-advertising.
- No choice is ultimately better (depends on the network)
  - For smaller networks, one may afford to do manual advertising for correctness.
  - For larger networks, it may be easier to advertise all to avoid configuration overhead.

**4- If you have a static route in a router, how can you let your OSPF neighbors know about it? Approve and show it on practice.**

![image-20241113000448328](https://i.postimg.cc/28psGFSd/image.png)

- Assuming R1 has a static route to `192.168.1.0/30` (R5/R6 network), we can configure the route and let others know about that as follows

  ```ruby
  R1⋕ conf t
  
  # Configure static IP on R1 g2/0
  R1(config)⋕ interface g2/0
  R1(config-if)⋕ ip address 10.0.4.1 255.255.255.252
  R1(config-if)⋕ no shut
  R1(config-if)⋕ exit
  
  # Configure static route on R1 to R6 network
  R1(config)⋕ ip route 192.168.1.0 255.255.255.252 g2/0
  
  # Configure static route on R6 to R1 network
  R6(config)⋕ ip route 10.0.4.0 255.255.255.252 g0/0
  ```

- Rest of configuration for R5 and R6 is trivial, just adding static IPs in the same way as above.

- Testing reachability between R1 and R6 using static routes.

  ![image-20241112232405765](https://i.postimg.cc/W1TQLNrh/image.png)

- To further distribute the static routes from R1 to its OSPF neighbors, do the following:

  ```bash
  # Let R1 neighbors know about route to 10.0.4.0/30 (R1-R5 network)
  R1(config)⋕ network 10.0.4.0 0.0.0.3 area 0
  
  # Redistribute static routes [KEY COMMAND]
  R1(config)⋕ router ospf 1
  R1(config-router)⋕ redistribute static subnets
  ```

- Verify propagated static routes on R2, R4, and R3

  ![](https://i.postimg.cc/FzxVj4KB/image.png)

  

- Now routers 2,3, and 4 can ping R6 without having to configure OSPF on either R5 or R6.

  - Note that R6 needs a static route to every pinger for replies to be routed correctly.

  - Alternatively, we can verify reachability by checking ICMP requests in Wireshark while capturing the traffic on the R5-R6 interface as shown:

    ![image-20241113000220021](https://i.postimg.cc/65Yhqtg7/image.png)

**5- Enable OSPF with authentication between the neighbors and verify it.**

- OSPF messages are exchanged between routers without sender authentication, an attacker may try to inject false routes into the network. To prevent that, we can setup password with MD5 hash authentication (recommended) between trusted OSPF neighbors.

- Router configurations (identical commands on all routers) to use `ospf-pass` as the password.

  ```ruby
  R1(config)⋕ interface range g0/0 , g1/0
  R1(config-if-range)⋕ ip ospf authentication message-digest
  R1(config-if-range)⋕ ip ospf message-digest-key 1 md5 ospf-pass
  R1(config-if-range)⋕ ip ospf 1 area 0
  ```

- Showing OSPF packets between R1 and R2 after configuring authentication. We can see MD5 hashes (Auth Crypt Data) being used for verification.

  ![image-20241113015021927](https://i.postimg.cc/W1zxb9Wv/image.png)



## Task 3 - OSPF Verification

1- How can you check if you have a full adjacency with your router neighbor?

- Using command: `show ip ospf neighbor`

  ![](https://i.postimg.cc/s26073b0/image.png)

2- How can you check in the routing table which networks did you receive from your neighbors?

- Using command: `show ip route ospf`

  ![image-20241113021637374](https://i.postimg.cc/HWKYgysj/image.png)

- Showing OSPF databases as well with `show ip ospf database`

  ![image-20241113022159135](https://i.postimg.cc/kXbnXgBs/image.png)

3- Use traceroute to verify that you have a full OSPF network.

- Traceroute from R3 to R5 shows two paths (one that goes through R2 then R1, and another that goes through R4 then R1), proving that a full OSFP routes have been learned.

  ![image-20241113035842358](https://i.postimg.cc/0y1961Ss/image.png)

4- Which router is selected as DR and which one is BDR?

- DR elections only happen on broadcast networks (e.g., multiple routers connected in a star topology to a central switch). **There is no DR/BDR election on Point-to-Point and Point-to-Multipoint networks.** [[ref.](https://community.cisco.com/t5/other-network-architecture-subjects/dr-and-bdr-in-ospf-process/td-p/520844)]
- All networks in my topology are point-to-point. That's why each router will form a full neighbor relationship with both of it's neighbors and each of them will provide relevant link state updates.

- Refer to my OSPF summary in the appendix to check my understanding of the process.

5- Check what is the cost for each network that has been received by OSPF in the routing table.

- As shown in the screenshot for point 2, route entries learned by OSPF contain a string in the format `[priority/metric]` where protocol priority is `110` by default for OSPF and `metric` is the path cost.
  - Cost is 2 for regular routes.
  - Cost is 20 for external (E2) routes. 

## Appendix: OSPF Summary

My understanding of OSPF, after watching [this](https://www.youtube.com/watch?v=kfvJ8QVJscc) and reading some of [this](https://en.wikipedia.org/wiki/Open_Shortest_Path_First).

- Open Shortest Path First is a widely used and supported **dynamic**, **global**, **interior gateway (inter-AS)**, **link-state** and **load-insensitive** routing protocol.

  - **Dynamic:** routing information are learned from the network and not configured statically.
  - **Global:** all router know the same information about all links in the autonomous system.
  - **Interior gateway:** used to exchange routing and reachability information between routers in the same **Autonomous System (AS)**.
  - **Link-state:** each node send information to all other nodes regarding the state of it's neighboring links.relay
  - **Load-insensitive:** the best path chosen doesn't depend on the current traffic load in the network, a congested path can be chosen as the best one if it has the minimum cost.

- **How it works:**

  ![OSPF Neighbor States – OSPF Neighbor Forming Process](https://www.firewall.cx/images/stories/ospf-adjacency-neighbor-states-forming-process-1.png)

  1. OSPF routers agree to form a neighbor relationship.
     - **(Down state)** Each router chooses a RID: static -> highest loopback interface IP -> highest non-loopback interface IP.
     - **(Attempt/Init state)** Each router sends a HELLO message to its neighbors, containing it's RID and known neighbors.
       - If receiver is already neighbor with sender, it just resets dead interval.
         - If dead interval passed with no received hello from neighbor, it's assumed to be dead and set to down.
       - Else, it starts building neighbor relationship with sender.
         1. Check that attributes match (e.g., area ID)
         2. Add sender RID to its list of known neighbors.
         3. Enters **(2-way state)**.
     - **(DR election)** on broadcast networks, routers will only continue form full-neighbor relationship with Designated Router (DR).
       - DR works as the central point of trusted Link-State Advertisements (LSA) updates, it receives link state updates and share them with the rest of the network.
       - DR is selected at this stage (manually assigned priority -> highest RID) and announced to everyone.
       - A Backup DR (BDR) is also elected to serve in case the DR is down.
     - **(ExStart):** master router (higher RID) is selected, it generates an initial sequence number to be used for exchanging Link-State Database (LSDB) data with slave routers.
     - **(Exchange):** LSDBs are exchanged between routers.
       - Each router broadcasts LSA packets containing information about each subnet it's part of.
       - LSAs are re-broadcasted whenever there is a change, or just every specified interval (to ensure paths are updated).
       - Received information is stored in each router's LSDB
     - **(Loading):** now each router knows LSDBs of other routers, it compares it with its own LSDB
       - If a neighbor **B** knows about an interface which **A** doesn't know about, **A** will ask **B** for this information (LS Request), **B** will reply with data (LS Update), **A** will then send LSAcknowledge for updates.
       - Process continues until every router knows about every subnet in the AS and LSDBs are synchronized. 
     - **(Full)** routers now have formed full neighbor relationship with DR and BDR and can load upcoming updates.
  2. Each router chooses the best routes based on information exchanged from LSDBs (runs Dijkstra single-source shortest path algorithm locally)
     - Each link is assigned a cost based on its bandwidth, costs are used as weights while applying Dijkstra's algorithm.
     - Serial interface: 64, Ethernet: 10, FastEthernet: 1, anything faster: 1.

