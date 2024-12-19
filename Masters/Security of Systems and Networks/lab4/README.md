# Lab 4 - OpenVPN

> Ahmed Nouralla - a.shaaban@Innopolis.university

[TOC]

## Task 1 - Introduction

a. What is PKI, and what is the purpose of PKI with OpenVPN?

- Public Key Infrastructure is the cryptographic system that works with encryption/decryption using public and private keys (for establishing confidentiality) as well as digital certificates (for proving the authenticity and validity of public keys being used).
- OpenVPN uses that system for establishing trust (authentication and key exchange) between a VPN server and its clients using SSl/TLS certificates.

b. Distinguish between a master certificate authority (CA) and a separate certificate.

- A primary (master) CA is used to sign the certificates for the server and client (proving their authenticity).
- A separate certificate (i.e., public key) and private key for the server and each client, to establish secure communication between them.

## Task 2 - Setup PKI

### 2.1. Setup Certification Authority

a) Install, build, and initialize a Public Key Infrastructure for an installed OpenVPN.

0. Following the documentation at https://ubuntu.com/server/docs/how-to-install-and-use-openvpn

1. Install needed tools

   ```bash
   sudo apt install openvpn easy-rsa
   ```

2. Set up certificate authority

   ```bash
   sudo -i
   make-cadir /etc/openvpn/easy-rsa
   cd /etc/openvpn/easy-rsa
   ./easyrsa init-pki
   ./easyrsa build-ca
   ```

### 2.2. Create Server Keys and Certificates 

b) What did you notice about the prompt when creating a CA? What is the reason behind creating and using a passphrase?

- When creating a CA, it asks for a common name and a passphrase, the passphrase is used every time we need to encrypt a command output, to avoid printing sensitive data in terminal.

  > The server machine I used is a cloud hosted VM, trying to deploy a remote-access VPN service.

  ![](https://i.postimg.cc/5yZpTCf5/image.png)
  
  ![](https://i.postimg.cc/9F7LTJBL/image.png)

c) Generate Diffie-Hellman parameters and key pair for the server. Show the location (path) of the key pair generated.

- Generate a key-pair for the server: `./easyrsa gen-req myservername nopass`
  ![](https://i.postimg.cc/fLPvS9S4/image.png)

- Generate Diffie-Hellman parameters: `/easyrsa gen-dh`

  ![](https://i.postimg.cc/xCX4ZYK2/image.png)

d) What is the size of the Diffie-Hellman parameters and their locations? Shown here:

![](https://i.postimg.cc/KzCHSVNg/image.png)

e) Create a certificate for the server. What is the commonName value, and how many days are the certificates signed on the server?

- Create a certificate for the server: `./easyrsa sign-req server myservername`

- `commonName` is shown to be `openvpn-server`

- The certificates are signed on the server for 825 days as shown

  ![](https://i.postimg.cc/RZm2Zkz7/image.png)

- Copy files created to OpenVPN root for convenience

  ```bash
  cp pki/dh.pem pki/ca.crt pki/issued/openvpn-server.crt pki/private/openvpn-server.key /etc/openvpn/
  ```

### 2.3. Create Client Certificates

f) Create a key for the client and also a client certificate. What is the expiration date of the certificate?

- Create key pair for the client: `./easyrsa gen-req myclient1 nopass`

![](https://i.postimg.cc/KvPsw0gR/image.png)

- Create certificate for the client: ``./easyrsa sign-req client myclient1`.

![](https://i.postimg.cc/zfBtL52g/image.png)

- The certificate is shown to expire on **Feb 5 2027**.

### 2.4. Server Configuration

- Start from a sample server config

```bash
cp /usr/share/doc/openvpn/examples/sample-config-files/server.conf /etc/openvpn/myserver.conf
```

- Update the config to use the files we created earlier.

- For extra security, we can use a TLS Authentication (TA) key.

a. What is the TLS Authentication (TA) Key, and why is it essential in OpenVPN?

> Reference: https://openvpn.net/community-resources/hardening-openvpn-security/

- TLS authentication is a mechanism used by OpenVPN for extra security. The idea is to have a shared `ta.key` shared between client(s) and server.
- The file is used to add additional HMAC (Hash-based Message Authentication Code) signature to all SSL/TLS handshake packets for integrity verification.
- This is used to help block DoS attacks and UDP port flooding.

b. Generate TLS Authentication (server) and show the server key information

- Command to generate TA key on server: `openvpn --genkey secret ta.key`
- In server config we specify `tls-auth ta.key 0`

- Showing relevant entries in the config file:

  ![](https://i.postimg.cc/Gh7XNVw7/srv.png)

### 2.5. Client Configuration

- Install openvpn on the client (e.g., `apt install openvpn`)

- Copy the files to the client (e.g., using `scp`) and place them in `/etc/openvpn`

  ```bash
  ca.crt
  myclient1.crt
  myclient1.key
  ta.key
  /usr/share/doc/openvpn/examples/sample-config-files/client.conf
  ```

c. Generate a TLS Authentication key (client) and show the client key information

- In client config, specify `tls-auth ta.key 1`

- Showing relevant parts in client config

  ![](https://i.postimg.cc/B6BgCwBC/client.png)

## Task 3 - Verification

- Start the server

  ```bash
  nano /etc/sysctl.conf # uncomment net.ipv4.ip_forward=1
  sysctl -p /etc/sysctl.conf
  systemctl start openvpn@myserver
  ```

- Verify service status

  ![](https://i.postimg.cc/y6cP8nyB/image.png)

- Start the client with `systemctl start openvpn@myserver`

- Verify client status

  ![](https://i.postimg.cc/vTqV62T2/image.png)

a. Simulate a communication between the OpenVPN server and the and client.

- There will be automatic communications that will happen once the client is started, for establishing the secure VPN channel.

b. Using a traffic sniffer like Wireshark, inspect the interface of the VPN traffic. Show the information about this traffic

- We can inspect the traffic in Wireshark.
  - It shows the known TLS 4-way handshake
  - Followed by traffic specific to the OpenVPN protocol connection establishment
  - Then encrypted application data (VPN traffic) is being exchanged.

![](https://i.postimg.cc/cJ8kfmzD/image.png)

