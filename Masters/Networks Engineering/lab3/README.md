# Lab 3 - VLAN, Fault Tolerance, and STP

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Preparation

- Installed [Cisco IOSv (Router)](https://gns3.com/marketplace/appliances/cisco-iosv) and [CiscoIOSvL2 (Switch)](https://gns3.com/marketplace/appliances/cisco-iosvl2) appliances in GNS3 and created the following infrastructure

  ![](https://i.postimg.cc/PqCdK5X8/image.png)

## Task 2 - VLANs

**a. Change the topology of your network, and make the necessary configurations.**

- The topology is shown above

- Router (gateway) configuration:

  ```ruby
  Router> enable
  Router⋕ conf t
  Router(config) interface g0/0
  Router(config-if) ip address dhcp
  Router(config-if) no shut
  Router(config-if) interface g0/1
  Router(config-if) ip address 10.0.0.1/24 255.255.255.0
  Router(config-if) no shut
  Router(config-if) interface g0/2
  Router(config-if) ip address 192.168.1.1 255.255.255.0
  Router(config-if) exit
  Router(config) exit
  
  # Inspection command
  Router⋕ show ip interface brief
  ```

  ![](https://i.postimg.cc/hvJjcz2g/image.png)

- Hosts configuration

  ```bash
  # Web
  sudo ip a add 192.168.1.2/24 dev ens3
  sudo ip route add default via 192.168.1.1 dev ens3
  
  # Admin
  sudo ip a add 192.168.1.3/24 dev ens3
  sudo ip route add default via 192.168.1.1 dev ens3
  
  # HR
  sudo ip a add 10.0.0.2/24 dev ens3
  sudo ip route add default via 10.0.0.1 dev ens3
  
  # Management
  sudo ip a add 10.0.0.3/24 dev ens3
  sudo ip route add default via 10.0.0.1 dev ens3
  
  # ITManager
  sudo ip a add 10.0.0.4/24 dev ens3
  sudo ip route add default via 10.0.0.1 dev ens3
  ```

**b. Configure the switches and make sure you have connectivity between the hosts.**

- The switch self-learning behavior would automatically enable connectivity between hosts after learning host MAC addresses whenever they communicate with it.

- Connectivity verifications

  ![](https://i.postimg.cc/mDBgTCsx/image.png)

**c. How do VLANs work at a packet level? What are the two main protocols used for this?**

- VLAN (Virtual Local Access Network) is the technology that logically groups switch ports into separate broadcast domains, for logical isolation of hosts sharing a switch (e.g., different departments).

  - The following diagram from [Kurose, Ross](https://gaia.cs.umass.edu/kurose_ross/eighth.php) shows an informative use case with 2 VLANs configured on 2 switches with a trunk port for delivering cross-switch VLAN traffic.

  ![](https://i.postimg.cc/j5hsGbtK/image.png) 

- In an Ethernet frame, an additional header for [802.1Q](https://en.wikipedia.org/wiki/IEEE_802.1Q) is injected when the frame is forwarded out a trunk port to indicate that a frame is tagged, it includes VLAN ID and other tagging information as shown.

  ![](https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Ethernet_802.1Q_Insert.svg/1328px-Ethernet_802.1Q_Insert.svg.png)

- This [wiki section](https://en.wikipedia.org/wiki/VLAN#Protocols_and_design) describes the main protocols used to work with VLAN

  - It indicates 802.1Q being the most popular standard (with its extensions [802.1ad](https://en.wikipedia.org/wiki/IEEE_802.1ad) and [802.1aq](https://en.wikipedia.org/wiki/IEEE_802.1aq) supporting more VLANs).
  - Also, Cisco's [VLAN Trunking Protocol (VTP)](https://en.wikipedia.org/wiki/VLAN_Trunking_Protocol) that reduces administration overhead by synchronizing VLAN configurations between switches using a client/server architecture.

**d. What is the Native VLAN?**

- Normally, traffic going through a trunk port has to have 802.1Q header for tagging as shown above.

- If (for some reason), a packet sent over a trunk port is untagged, it will be assigned to the native VLAN (ID=1 by default on most devices).
  - Untagged frame may originate from a legacy device that does not understand VLAN or an end device directly connected to the trunk (e.g., transmitting voice traffic).

**e. Configure the VLANs on the switches to isolate the two virtual networks as follows**

- Infrastructure after doing some IP changes to isolate VLANs

  ![](https://i.postimg.cc/yYxpDgv9/image.png)

- Host reconfigurations

  ```bash
  # HR
  sudo ip a del 10.0.0.2/24 dev ens3
  sudo ip a add 10.0.10.2/24 dev ens3
  sudo ip route add default via 10.0.10.1 dev ens3
  
  # Management
  sudo ip a del 10.0.0.3/24 dev ens3
  sudo ip a add 10.0.20.2/24 dev ens3
  sudo ip route add default via 10.0.20.1 dev ens3
  
  # ITManager
  sudo ip a del 10.0.0.4/24 dev ens3
  sudo ip a add 10.0.20.3/24 dev ens3
  sudo ip route add default via 10.0.20.1 dev ens3
  ```
  
- Switch configurations to configure VLAN (access ports and trunk ports)

  ```ruby
  # Administration Switch
  Switch> enable
  Switch⋕ conf t
  Switch(config) interface g0/0
  Switch(config-if) switchport mode access
  Switch(config-if) switchport access vlan 10
  Switch(config-if) interface g0/1
  Switch(config-if) switchport mode access
  Switch(config-if) switchport access vlan 20
  Switch(config-if) interface g0/2
  Switch(config-if) switchport trunk encapsulation dot1q
  Switch(config-if) switchport mode trunk
  
  # ITDepartment Switch
  Switch> enable
  Switch⋕ conf t
  Switch(config) interface g0/0
  Switch(config-if) switchport mode access
  Switch(config-if) switchport access vlan 20
  Switch(config-if) interface g0/1
  Switch(config-if) switchport trunk encapsulation dot1q
  Switch(config-if) switchport mode trunk
  Switch(config-if) exit
  Switch(config) exit
  
  # Inspection commands
  Switch⋕ show vlan brief
  Switch⋕ show interfaces trunk
  ```

- Showing VLAN and trunk information on both switches

  ![](https://i.postimg.cc/Y0f25Dmc/image.png)

f. Ping between ITManager and HR, do you have replies? Ping between ITManager and Management, do you have replies? Can you see the VLAN ID in Wireshark?

- Ping between ITManager (VLAN 20) and HR (VLAN 10) **does not work** as they belong to different subnets and VLANs.

  ![](https://i.postimg.cc/JnqQXnV9/image.png)

- Ping between ITManager (VLAN 20) and Management (VLAN 20) **works** as they belong to the same subnet and VLAN.

  ![](https://i.postimg.cc/QdXgyVyC/image.png)

- Inspecting ping traffic over Wireshark shows the 802.1Q header with VLAN ID 20

  ![](https://i.postimg.cc/nhgzQPBx/image.png)

g. Configure Inter-VLAN Routing between Management VLAN and HR VLAN and Show that you can now ping between them.

- We can enable inter-VLAN routing by adding sub-interfaces on the VLAN-facing interface on the router as follows

  ```ruby
  Router> enable
  Router⋕ conf t
  Router(config) interface g0/1
  Router(config-if) ip address 10.0.1.1 255.255.255.0
  Router(config-if) no shut
  Router(config-if) interface g0/1.10
  Router(config-subif) encapsulation dot1Q 10
  Router(config-subif) ip address 10.0.10.1 255.255.255.0
  Router(config-subif) no shut
  Router(config-subif) interface g0/1.20
  Router(config-subif) encapsulation dot1Q 20
  Router(config-subif) ip address 10.0.20.1 255.255.255.0
  Router(config-subif) no shut
  Router(config-subif) exit
  Router(config) exit
  
  # Inspection command
  Router⋕ show ip interface brief
  ```
  
  ![](https://i.postimg.cc/V6Rm4Grd/image.png)

- Ping between ITManager (VLAN 20) and HR (VLAN 10) **now works** as we configured inter-VLAN routing on the router using subinterfaces.

  ![](https://i.postimg.cc/fb774LLJ/image.png)

## Task 3 - Fault Tolerance
a. What is Link Aggregation? How does it work (briefly)? What are the possible configuration modes?

1. Link aggregation is the idea of combining multiple physical links into one logical link for:

   - **Fault tolerance:** if one cable goes down, others stay functional

   - **Load balancing:** distributing the traffic over multiple cables to achieve an overall increased bandwidth

2. It works by connecting multiple cables between the same two points (on different interfaces of course), then configuring [802.1AX](https://en.wikipedia.org/wiki/Link_aggregation#802.1AX) (i.e., Link Aggregation Control Protocol (LACP)).

3. In Cisco switches, this is typically supported via [EtherChannels](https://en.wikipedia.org/wiki/EtherChannel), which supports 3 modes: static, PAgP ([Port Aggregation Protocol](https://en.wikipedia.org/wiki/Port_Aggregation_Protocol)), or LACP.

   - A PAgP or LACP port can be in on of two modes: active or passive.

   - The active mode allows the port to initiate auto-config negotiations with the other active/passive port

   - For EtherChannel to work, connected ports has to have the same duplex, speed, type (access/trunk), STP interface settings, and they can't be both passive
     - If using access ports (connected to hosts), they have to be in the same VLAN.
     - If using trunk ports (connected to another switch), they have to have the same native VLAN and same allowed VLANs

b. Use link aggregation between the Web and the Gateway to have Load Balancing and Fault Tolerance as follows

- Infrastructure changes:

  - Replaced IOSv Router with Cisco's 7200 as the latter includes support for EtherChannel needed for this task. However, the same commands used above still apply.

  - Added additional network interface on the host and duplicate connections as shown

    ![](https://i.postimg.cc/dQX9yDhT/image.png)

- Host (Web) configuration

  - Create a file: `sudo nano /etc/netplan/00-installer-config.yaml`

  - Add the following netplan configuration: It creates a bond interface (grouping `ens3` and `ens4`)

    ```yaml
    network:
      version: 2
      renderer: networkd
      bonds:
        bond0:
          interfaces:
            - ens3
            - ens4
          parameters:
            mode: 802.3ad # LACP mode
          addresses:
            - 192.168.1.2/24
          routes:
            - to: default
              via: 192.168.1.1
      ethernets:
        ens3: {}
        ens4: {}
    ```

  - Apply configuration: `sudo netplan apply`

  - Verify interface configuration: `ip a`

    ![](https://i.postimg.cc/Xv0HyqDg/image.png)

- Router configuration

  ```ruby
  Cisco7200> enable
  Cisco7200⋕ conf t
  Cisco7200 (config) interface port-channel 2
  Cisco7200 (config-if) ip address 192.168.1.1 255.255.255.0
  Cisco7200 (config-if) no shut
  Cisco7200 (config-if) interface range g2/0 , g3/0
  Cisco7200 (config-if-range) no ip address
  Cisco7200 (config-if-range) channel-group 2
  Cisco7200 (config-if-range) no shut
  Cisco7200 (config-if-range) exit
  Cisco7200 (config) exit
  
  # Inspection command
  Cisco7200⋕ show interface port-channel 2
  ```

  ![](https://i.postimg.cc/YSY1Yxm4/image.png)

- Switch configuration

  - Used LACP mode with host-connected interfaces
  - Used static mode with router-connected interfaces as it does not support LACP.

  ```ruby
  Switch> enable
  Switch⋕ conf t
  Switch(config) interface range g0/0 - 1
  Switch(config-if-range) channel-group 1 mode active
  Switch(config-if-range) no shut
  Switch(config-if-range) interface range g0/2 - 3
  Switch(config-if-range) channel-group 2 mode on
  Switch(config-if-range) no shut
  Switch(config-if-range) exit
  Switch(config) exit
  
  # Inspection command, shows active aggregation status
  Switch⋕ show etherchannel summary
  ```

  ![](https://i.postimg.cc/YCV319nP/image.png)

c. Test the Fault Tolerance by stopping one of the cables and see if you have any downtime.

- Suspended two cables midway during a time-consuming ping operation, the result it shown

  ![](https://i.postimg.cc/DypDM7pr/image.png)



## Task 4 - STP

My personal notes on STP after watching [this](https://www.youtube.com/watch?v=japdEY1UKe4)

- Spanning Tree Protocol builds a loop-free logical topology of Ethernet networks.
- Connections between Ethernet switches typically contain loops for redundancy, STP disables (blocks) some ports to prevent problems caused by switching loops and re- enables them upon need.
- **Problems caused by switching loops:**
  - Broadcast storm: broadcast messages being retransmitted over and over along the loop, eventually congesting and taking down the network).
  - Unstable MAC addresses: ARP tables keep updating because information is being received from different ports.
  - Duplicate frames can reach the same host from different paths.
- **Which port(s) to disable?**
  - This is determined by the spanning tree algorithm.
  - Overall the spanning tree created by disabling ports is the one that have the minimum path cost (ties are broken by a well-defined criteria).
- **Port states:** a port in an STP-enabled switch can be in one of the following states:
  - **Disabled:** shutdown.
  - **Blocking:** receiving traffic but ignoring them.
  - **Listening:** not forwarding traffic, not learning MAC addresses (typically stays there for 15 seconds before moving to Learning state)
  - **Learning:** not forwarding traffic, but learning MAC addresses (typically stays there for 15 seconds before moving to Forwarding state state)
  - **Forwarding:** forwarding traffic and learning MAC addresses as normal.

---

**a. Change the topology as follows and Disable STP on the Internal network switches.**

- Command to disable STP globally: `no spanning-tree`

**b. Send a broadcast ping request to the PCs connected to the Internal Network.**

- From router interface `g0/1` (address `10.0.1.1/24`), we can ping `10.0.1.255`

**c. What can you notice? Why did this happen? What are the implications of this on the network?**

- Ping messages go over and over in circles between the switches.
  - My host CPU utilization approaches 100%. Couldn't even take screenshots :)
- This happens because all interfaces are in a forwarding state and none of them were in a blocking state since we disabled STP.
- This will congest and eventually take down the network.

**d. Enable back STP on the Switches and do the experiment again.**

- Things go back to normal since STP construct a loop-free topology by setting some ports to a blocking state.

**e. Can you see STP traffic? Explain it briefly.**

- We can inspect STP traffic in Wireshark by capturing at any link between two STP-enabled switches.

  ![](https://i.postimg.cc/HL9nFByD/image.png)

- It shows switches exchanging port identifiers and bridge ID information (priority and MAC address) through Bridge Protocol Data Units (BPDUs), to elect a Root switch (one that will have all interfaces in forwarding state).
- Then, each non-root switch selects one root port. Then, each remaining link choose a designated port.
- Then, all non-root-ports, non designated-ports are switched to a blocking state.

**f. Configure the switches to have the *Internal* as the Root switch.**

- To ensure the internal switch becomes the Root Bridge, we can lower its priority.

- On the internal switch, showing it's priority and the ID of the root switch (not *Internal*)

  ![](https://i.postimg.cc/yWCWw5vK/image.png)

- By default the priority is `32769`, we can lower it by issuing a command like:

  ```ruby
  Switch (config)⋕ spanning-tree vlan 1 priority 24576
  ```

- Rechecking. It shows root ID Address to be the same as Bridge ID (Switch) Address.

  ![image-20241112183704847](/home/ahmed/.config/Typora/typora-user-images/image-20241112183704847.png)

**g. Would we need STP between routers?**

- Nope, routers operate on layer 3, they do not forward broadcasts like switches.
- However, on routers connected in a loop topology, we would a routing protocol like OSPF for configuring routes. But let's leave that to the upcoming lab.

