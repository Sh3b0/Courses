# Research on DHCP

> CIA Midterm - Ahmed Nouralla - a.shaaban@innopolis.university
>
> Overleaf: https://www.overleaf.com/project/6744d9d5a0952b62aeca0299

[TOC]

## 1. Introduction: why this tool/service is important

- Dynamic Host Configuration Protocol (DHCP) provides a framework for hosts joining a network to automatically obtain configuration information essential for further communications.
- Such information includes
  - The host's identity (e.g., IP address, subnet mask, FQDN).
  - Addresses of other key nodes (e.g., network gateway, name server, time server, etc.). 

## 2. History and background (RFC, if any)

- **RFC 951 (1985)** defined the bootstrap protocol (BOOTP) for static IP assignment to clients based on their MAC address.
- **RFC 1531 (1993)** introduced DHCP as an extension to BOOTP for automatic IP address assignment. **RFC 1541 (1993)** replaced it with clearer specifications.

- **RFC 2131 (1997)**: standardized DHCPv4, defining the DHCP message format and operation procedures. <u>It remains the primary DHCPv4 standard.</u>

- Multiple RFCs were published later introducing extensions, enhancements, security patches, and support for IPv6.

## 3. Key components and terminology

Essential terminology as defined in RFC 2131

- **DHCP client:** host using DHCP to obtain configuration parameters
- **DHCP server:** host providing the configuration parameters to clients
- **Relay agent:** host/router forwarding DHCP messages between server and client
- **Binding:** the configuration parameters (i.e., at least the IP address to be bound to the client).

## 4. Functionality, purpose, use cases

The most common use-case of DHCP (also known as the DORA process) involves four message exchanges.

![](https://www.dcs.bbk.ac.uk/~ptw/teaching/IWT/network-layer/dhcp-interaction.gif)

## 5. Architecture and working mechanisms

- DHCP uses a client server architecture as illustrated.

- A DHCP server is always listening on port 67/UDP for client requests.

- Message format as defined in RFC2131

  ![image-20241126003531387](https://i.postimg.cc/7hBT7twC/image.png)

- Currently, 18 OP codes (1-18) are in use to specify different types of DHCP messages, with over 80 options and extensions for communicating different types of information.

  ![](https://i.postimg.cc/W1s1jwvC/image.png)

- Figure 5 of the standard illustrates the following state-transition diagram.

  ![](https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Dhcp-client-state-diagram.svg/1920px-Dhcp-client-state-diagram.svg.png)   

## 6. Configuration management

A DHCP server can be configured to allocate addresses using one of three allocation strategies:

- **Dynamic**: providing addresses from a pool with leases (expiration) time.
- **Automatic**: permanent address assignment, unless voluntarily released.
- **Manual**: static IP/MAC binding table maintained by the administrator.

## 7. Security issue: Rogue DHCP Server

- A Man-In-The-Middle attack where a malicious host on the same subnet as the DHCP client deceives them by acting as the DHCP server
- It does so by replying to DHCPDISCOVER, offering it's own IP address as the default gateway.
- Once carried out, it allows the attacker to intercept all requests sent by the victim.
- A common mitigation strategy (DHCP snooping) involves dropping DHCP messages that did not come from the trusted switchport (the one connected to the real DHCP server). 

## 8. Alternative commands and tools

- DHCP server functionality can be implemented in routers, or in Linux hosts. 

- Popular server implementations include dnsmasq and ISC DHCP server (superseded by Kea)
- A thin client (e.g., dhcpcd or dhclient) is needed for communication with the server.

## 9. Practical Demonstration

- A quick demo showing the minimal set of commands to configure a working DHCP server on Cisco IOS. The scenario is simulated in GNS3 and the results are inspected in Wireshark.

  ![](https://i.postimg.cc/kGXPzf8v/image.png)

## 10. References

- RFC 2131
- Kurose, Ross TextBook
- https://en.wikipedia.org/wiki/Comparison_of_DHCP_server_software
- https://www.cisco.com/en/US/docs/ios/12_4t/ip_addr/configuration/guide/htdhcpsv.html

