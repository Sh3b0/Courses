# Steps taken
1. Connected two laptops via Ethernet cable
2. Assigned static IPs 10.0.0.2 and 10.0.0.3 via the NetworkManager
3. Used `hping3 -1 -c 1 10.0.0.3 -e 'HIDDEN TEXT'` to hide data in ICMP packet.
