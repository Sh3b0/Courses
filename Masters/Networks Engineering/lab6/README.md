# Lab 6 - MPLS

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 0 - What is MPLS

> Hold on before we start. What is MPLS? Here are my personal notes on the topic

- **Multi-Protocol Label Switching (MPLS)** is a routing technique with additional link layer features

- **Purpose:** initial goal of MPLS was to support fast IP packet forwarding using fixed length labels instead of complex IP calculations (i.e., shortest prefix matching). Now MPLS is being used for general packet forwarding of various networking protocols
  - IP forwarding decisions are based on destination IP only, while MPLS forwarding decisions can be based on both source and destination label.
- **How it works:**
  - An MPLS-capable router (label-switched router) forwards packets based on the information in MPLS forwarding table (label, outgoing link) without inspecting IP address.
  - MPLS forwarding decisions are more efficient and flexible than IP forwarding decisions, they can use traffic engineering methods to precompute optimal paths and backup paths and switch between them (fast reroute) quickly if link fails (useful for VoIP for example).
  - The dynamic information used by MPLS for forwarding decisions (e.g., link bandwidth) are propagated using an existing link-state routing protocol (e.g., OSPF or IS-IS) that floods this information into the network.
  - Two MPLS capable routers use the Label Distribution Protocol (LDP) to exchange label mapping information used to maintain Label-Switched Path (LSP) Databases.

- **Implementation:** an extra header that prefixes packets (added between link layer frame and network layer packet). Header contents include:

  - **Label (20 bit):** node identifier to be used for forwarding.

  - **EXP (3 bits, now called Traffic Class):** used for Quality of Service.

  - **S (1 bit):** bottom of stack flag, signifies that the current label is the last in the stack (MPLS supports stacked labels).

  - **TTL (8 bit):** Time-To-Live.

## Task 1 - Preparation

- Prepared a topology of three Cisco7200 routers and two Ubuntu hosts

  ![image-20241127211425038](https://i.postimg.cc/RhwXPcPL/image.png)

- Configured addresses and dynamic routing with OSPF.

  ```ruby
  # H1 config
  sudo ip a add 10.0.0.1/30 dev ens3
  sudo ip route add default via 10.0.0.2 dev ens3
  
  # H2 config
  sudo ip a add 10.0.0.14/30 dev ens3
  sudo ip route add default via 10.0.0.13 dev ens3
  
  # R1 config
  int g0/0
  ip address 10.0.0.2 255.255.255.252
  no shut
  
  int g1/0
  ip address 10.0.0.5 255.255.255.252
  no shut
  
  router ospf 1
  router-id 1.1.1.1
  network 10.0.0.0 0.0.0.3 area 0
  network 10.0.0.4 0.0.0.3 area 0
  
  # R2 config
  int g0/0
  ip address 10.0.0.6 255.255.255.252
  no shut
  
  int g1/0
  ip address 10.0.0.9 255.255.255.252
  no shut
  
  router ospf 1
  router-id 2.2.2.2
  network 10.0.0.4 0.0.0.3 area 0
  network 10.0.0.8 0.0.0.3 area 0
  
  # R3 config
  int g0/0
  ip address 10.0.0.10 255.255.255.252
  no shut
  
  int g1/0
  ip address 10.0.0.13 255.255.255.252
  no shut
  
  router ospf 1
  router-id 3.3.3.3
  network 10.0.0.8 0.0.0.3 area 0
  network 10.0.0.12 0.0.0.3 area 0
  
  # Route inspection command
  show ip route
  ```

- Showing routing tables on all routers and tracing the path between H1/H2.

  ![image-20241127215742393](https://i.postimg.cc/X702rzJH/image.png)

## Task 2

> At this point I got sick of colorless terminal and decided to pipe my telnet sessions to [`lolcat`](https://github.com/busyloop/lolcat)...

### 2.1. MPLS Learning & Configuring

1- Briefly answer the questions or give one-line description what is it:

- **LSP (Label Switched Path)**: A path through an MPLS network, established for forwarding packets based on labels.
- **VPLS (Virtual Private LAN Service)**: A Layer 2 VPN technology providing Ethernet-based point-to-multipoint connectivity over MPLS.
- **PHP (Penultimate Hop Popping)**: An MPLS process where the second-to-last router removes the label before forwarding the packet to the last hop.
- **LDP (Label Distribution Protocol)**: A protocol used in MPLS networks to distribute labels for LSP creation.
- **MPLS L2VPN (Layer 2 VPN)**: A service using MPLS to provide Layer 2 connectivity (e.g., Ethernet or Frame Relay) between remote sites.
- **CE-router (Customer Edge router)**: A router at the customer premises that connects to the service provider's network.
- **PE-router (Provider Edge router)**: A router in the service provider's network that interfaces with customer CE routers and manages VPNs.

2- Configure MPLS domain on your OSPF network, first without authentication.

- Configure MPLS globally and on each interface by running these commands on every router

  ```bash
  conf t
  mpls ip
  int range g0/0 , g1/0
  mpls ip
  ```

3- Enable authentication (what kind of authentication did you use)? Make sure that you can ping and trace all your network.

- To enable password authentication with MD5 verification, run the following commands on each router

  ```bash
  mpls ldp neighbor <neighbor-router-id> password mpls-pass
  mpls ldp password required 
  ```

- Note that the highest address on each router is used as an ID.

  - So R1 = 10.0.0.5, R2 = 10.0.0.9,  and R3 = 10.0.0.13

- Upon configuring password authentication on routers, neighbors went down due to MD5 mismatch as shown until all of them had knowledge of the password. 

  ![image-20241128021538428](https://i.postimg.cc/jdpRnKNV/image.png)

### 2.2. Verification

- Show your LDP neighbors: `show mpls ldp neighbors`

  ![image-20241127231010996](https://i.postimg.cc/7YMK0nJx/image.png)

- Show your local LDP bindings and remote LDP peer labels: `show mpls ldp bindings`:

  

  ![image-20241127231550709](https://i.postimg.cc/Dwx5YfcH/image.png)

- Show your MPLS labels and forwarding table

  ![image-20241127232452464](https://i.postimg.cc/s28msnqJ/image.png)

- Show your network path from one customer edge to the other customer edge.

  ![image-20241127233032231](https://i.postimg.cc/hvR07XSz/image.png)

## Task 3 - MPLS Packet Analysis

- We can see periodical LDP KeepAlive messages used by MPLS.

  ![image-20241128000921987](https://i.postimg.cc/rsGNGqmh/image.png)

- Ping H1/H2 capture in Wireshark shows MPLS header and labels

  - Ethernet header indicates MPLS content and src/dst MAC addresses.
  - MPLS header shows label 17 from the forwarding table above.
  - Packet payload contains the ICMP ping request.

  ![image-20241128000435375](https://i.postimg.cc/fWYCQfKx/image.png)

## Task 4 - VPLS

1- Configure VPLS (L2) between the 2 hosts edges 

- Configuring VPLS on the Customer Edge (CE) routers: R1 and R3. These commands should do it.

  ```ruby
  R1(config)⋕ l2 vfi VPLS1 manual
  R1(config-vfi)⋕ vpn id 10
  R1(config-vfi)⋕ neighbor 10.0.0.13 encapsulation mpls
  R1(config-vfi)⋕ exit
  
  R1(config)⋕ interface g0/0
  R1(config-if)⋕ service instance 10 ethernet
  R1(config-if-srv)⋕ encapsulation default
  R1(config-if-srv)⋕ bridge-domain 10
  R1(config-if-srv)⋕ exit
  
  R3(config)⋕ l2 vfi VPLS1 manual
  R3(config-vfi)⋕ vpn id 10
  R3(config-vfi)⋕ neighbor 10.0.0.5 encapsulation mpls
  R3(config-vfi)⋕ exit
  
  R3(config)⋕ interface g1/0
  R3(config-if)⋕ service instance 10 ethernet
  R3(config-if-srv)⋕ encapsulation default
  R3(config-if-srv)⋕ bridge-domain 10
  R3(config-if-srv)⋕ exit
  ```

- The feature is not supported on Cisco's virtual IOS [[Link1](https://learningnetwork.cisco.com/s/question/0D53i00000KssLaCAJ/all-l2-services-psuedowire-and-vpls-not-working-on-iosxr-in-gns3-), [Link2](https://community.cisco.com/t5/routing/vpls-emulation/td-p/2781681)]. I couldn't test it.

2- Show your LDP neighbors again, what has been changed?

- We should see additional LDP bindings for the VPLS pseudowire (emulated point-to-point connection).

3- Find a way to prove that the two customers can communicate at OSI layer 2.

- We can add both of them to the same VLAN and ping.

4- Is it required to disable PHP? Explain your answer.

- No, PHP removes the MPLS label on the second-to-last hop, without affecting the payload.
- In our L2VPN setup, the overlay network between H1 and H2 is just one hop. So, there should be no need to disable PHP.

