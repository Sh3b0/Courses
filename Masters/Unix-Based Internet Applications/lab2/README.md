# Lab 2 - DNS

> Ahmed Nouralla - a.shaaban@innopolis.university
>
> ID for the task = 14 (BIND). Domain = [st14.sne24.ru]() 

[TOC]

## Task 1 - Download and install a caching name server

### 1.1. Validating the download

**Why is it wise to verify your download?**

- To verify we downloaded the file that was indeed distributed by ISC, and not a modified version (perhaps a malicious one). 

**Download the BIND tarball and check its validity using one of the signatures.**

```bash
cd ~/Downloads

# Download BIND9 tarball and its signature from ISC website
wget https://downloads.isc.org/isc/bind9/9.18.31/bind-9.18.31.tar.xz
wget https://downloads.isc.org/isc/bind9/9.18.31/bind-9.18.31.tar.xz.asc

# Import ISC's PGP key from https://www.isc.org/pgpkey/ to my GPG keyring
gpg --keyserver hkp://keyserver.ubuntu.com --recv-keys 706B6C28620E76F91D11F7DF510A642A06C52CEC

# Verify tarball using the signature file
gpg --verify bind-9.18.31.tar.xz.asc bind-9.18.31.tar.xz
```

![](https://i.postimg.cc/3xY8M5Tt/image.png)

> Output shows a good signature. However, the warning indicates that the key is not verified, this is because I imported the key but didn't (and won't) explicitly confirm (by signing it with my private key) that I trust that the imported key indeed belongs to the owner (Michał Kępień is that case). More information about the process is available [here](https://security.stackexchange.com/questions/147447/gpg-why-is-my-trusted-key-not-certified-with-a-trusted-signature).

**Which mechanism is the best one to use (signatures or hashes)? Why?**

- Verification with **signatures** is more secure than hashes alone.
- Hashes verify integrity (i.e., the file at the server is the same one we downloaded).
- Signatures verify integrity and sender authentication (i.e., ISC published the file and signed it with their private key that only they have access to).

### 1.2. Documentation & Compiling

> References:
>
> - https://github.com/isc-projects/bind9/blob/main/doc/arm/build.inc.rst
>
> - https://kb.isc.org/docs/aa-00768

**Configure, Compile, and Install the servers in `/usr/local`**

```bash
# Verify we don't have a bind already
named -v # or we can run `locate bind` and inspect the results

# Installing common build dependencies as requested from configure script
sudo apt install build-essential perl libssl-dev libuv1-dev pkg-config libexpat1-dev bison flex libnghttp2-dev libcap-dev libjemalloc-dev

# Installing from source
tar -xf ./bind-9.18.31.tar.xz
cd bind-9.18.31
./configure
make
sudo make install
```

**Make sure each server will look for its configuration files in `/usr/local/etc/bind`**

- The output from `./configure` shows the paths for configuration

![image-20241108171822983](https://i.postimg.cc/BZ81fvbM/image.png)

- The output from make install shows installed libraries locations

  ![image-20241108172036305](https://i.postimg.cc/D0bhjH2P/image.png)

**What is the difference between `/etc`, `/usr/etc`, `/usr/local/etc`**

- `/etc` contains system-wide configuration files (e.g., `/etc/passwd`, `/etc/shadow`, `/etc/fstab`, ...)
- `/usr/etc` historically contained configuration for services installed in `/usr`
- `/usr/local/etc` contains configuration for manually installed services (e.g., installation from source by system administrator, as above)

## Task 2 - Configuring a caching name server

> Reference: https://gist.github.com/Nilpo/1a70ebca988ad0743ea533d747445148

**Why are caching-only name servers still useful?**

A caching-only name server is not responsible for providing authoritative answers for any specific zone, it just caches answers obtained from other servers to provide

- Faster responses to frequent DNS queries
- Reduced load on root and authoritative servers.
- Fault tolerance in case authoritative servers had downtime.

Configure `/usr/local/etc/bind/named.conf`

```python
# Configure global options
options {
    directory "/var/named";  # Directory for zone files, default location
    listen-on port 53 { 127.0.0.1; any; };  # Listen on localhost and all interfaces (only for testing, not recommended)
    allow-query { any; };  # Allow queries from all clients (only for testing, not recommended)
    recursion yes;  # Enable recursion for caching
    forwarders { 8.8.8.8; 8.8.4.4; };  # Optional: Use Google's public DNS as forwarders
    listen-on-v6 { none; }; # Disable IPv6
};

# Configure verbose (debug) logging to a file
logging {
    channel default_file {
        file "/var/log/named.log" versions 3 size 5m;
        severity debug;
        print-time yes;
        print-severity yes;
        print-category yes;
    };
    category default { default_file; };
};

# This zone (root) tells "named" where to start looking for any name on the Internet
zone "." IN {
    type hint; # a hint type means that we've got to look elsewhere for authoritative information
    file "named.root"; # file to be downloaded below
};

# Where the localhost hostname is defined
zone "localhost" IN {
  # a master type means that this server needn't look
  # anywhere else for information; the localhost buck stops here.
  type master;
  file "named.local";
  # don't allow dynamic DNS clients to update info about the localhost zone
  allow-update { none; };
};
```

Download the root hint file to `/var/named/named.root`

```bash
sudo mkdir /var/named
cd /var/named
sudo wget ftp://ftp.rs.internic.net/domain/named.root
```

![image-20241108182346286](https://i.postimg.cc/vB28S55b/image.png)

Create the referenced file at `/var/named/named.local` , for defining localhost

```bash
;
; loopback/localhost zone file
;
$TTL 1D
$ORIGIN localhost.
@              IN  SOA   @  root (
                         1   ; Serial
                         8H  ; Refresh
                         15M ; Retry
                         1W  ; Expire
                         1D) ; Minimum TTL
               IN   NS   @
               IN   A    127.0.0.1
```

**Check config syntax for validity**

![image-20241108182540736](https://i.postimg.cc/yxNgDVyY/image.png)

**Why is access control important for recursive server?**

> Reference: https://bind9.readthedocs.io/en/v9.18.1/security.html#access-control-lists

Allowing everyone to use our DNS server is a bad security practice as attackers may abuse the service to implement Denial of Service attacks by sending a large number of small requests that require large answers, effectively congesting the network.

Therefore, it's better to restrict access only to devices that need the service. In `named.conf`, this can be achieved using options such as`listen-on`, `allow-query`, `allow-recursion`, and `allow-query-cache`

**Why do the programs return a result value?**

A return code helps to indicate the status of a finished program, where 0 would normally mean success and other codes indicate a failure with a certain error code defined by the programmer.

## Task 3 - Running caching name server

**Run `named` with debug level 2**

![image-20241108191302778](https://i.postimg.cc/zXdY9CLb/image.png)

**Check logs**

![](https://i.postimg.cc/W30yKvB8/image.png)

**Show the changes you made to your configuration to allow remote control**

> Reference: https://bind9.readthedocs.io/en/latest/manpages.html#cmdoption-rndc-confgen-a

To configure the Remote Name Daemon Control (`rndc`) for `named` server running on the localhost, run

```bash
sudo rndc-confgen -a
```

This will automatically generate the required key at `/usr/local/etc/rndc.key` to be read by `rndc` and `named` for local communication without additional setup.

- If run without the `-a` flag, it will print the necessary changes in `rndc.conf` (on client) and `named.conf` (on server) which can be used for communication with a remote server if needed.

  ![](https://i.postimg.cc/K8L80K9g/image.png)

- Verify `named` server status with `rndc`. In case it was not running, run it with `sudo named &`

  ![](https://i.postimg.cc/j2HrzNqs/image.png)

**What other commands/functions does `rndc` provide?**

> Reference: https://bind9.readthedocs.io/en/latest/manpages.html#man-rndc

- `rndc` is a control utility that communicates over a secure TCP channel with a remote bind server, it can do tasks such as:
  - Initiate server start/stop/reload
  - Query server logs, status and stats
  - Add, delete, reload, refresh, or freeze zone info
  - Flush/sync DNS cache

**What is the difference between stop -> start and reload?**

- Stop then start completely terminates and reinitializes the server, this means that any cached data is lost and any ongoing requests are lost. Use it when doing major changes in server configuration, a server update, or to recover from failures.
- Reload functionality does not stop the server process, it just reloads configuration and zone information from respective files while maintaining cache and ongoing connections.

**What do you need to put in resolv.conf (and/or other files) to use your own name server?**

- I have BIND installed in an Ubuntu server VM with NAT connection to my Ubuntu host.

- On the host, I can reference the server address in my `/etc/resolv.conf`  as

  ```
  nameserver 192.168.122.27
  ```

- However, the file is managed by NetworkManager, so my changes can be overwritten. It's better to modify the changes manually from the GUI.

- My system also uses `systemd-resolved`, so I can do the change there as well

  ```bash
  sudo nano /etc/systemd/resolved.conf # Update the line as "DNS=192.168.122.27"
  sudo systemctl restart systemd-resolved
  ```

  ![image-20241112212015101](/home/ahmed/.config/Typora/typora-user-images/image-20241112212015101.png)

- Verify that our DNS server (at 192.168.122.27) is being used to resolve queries. 

  ```bash
  dig example.com
  ```

​		![](https://i.postimg.cc/htqVRdHn/image.png)

**Show that your queries are successfully resolved and cached by also inspecting the server’s log file**

Showing sample DNS queries for some websites against our server using `dig` and `nslookup`. 

- With dig we can specify the address of DNS server to query
- Here we use our server, but it can also ask Google's `8.8.8.8` or Cloudflare's `1.1.1.1`

![image-20241109005458317](https://i.postimg.cc/R01b9nQt/image.png)

![image-20241109005856357](https://i.postimg.cc/TwRNPYC4/image.png)

## Task 4 - Authoritative Name Server

**What is a private DNS zone? Is st14.sne24.ru private?**

- A private DNS zone a DNS zone (portion of DNS namespace) that is only available for internal name resolution, unlike public zones that are accessible over the public Internet.
- For example, `dev.company.local` that resolves to the company's internal website only when accessed from inside the company's network.
- `st14.sne24.ru` is considered to be private in that context, as querying it (e.g., with `nslookup`) returns `NXDOMAIN`.

**What information was needed by TAs so they can implement the delegation?**

- The owner of the parent domain `sne24.ru` should have delegated control of the subdomain at `st14.sne24.ru` so that any requests to it are resolved by the nameserver I control at `ns.st14.sne24.ru`
- For that to work, they need to specify `NS` records that point to my nameserver, as well as `A` (or `AAAA`) records that point to its IP addresses.

**Create zone file at `/var/named/st14.sne24.ru.zone`** with the following content.

- The public IP used is the output of `curl ifconfig.me`

  ![](https://i.postimg.cc/V6kq0SM2/image.png)

**Add reference to the zone file in `named.conf`  as follows**

```json
zone "st14.sne24.ru" {
    type master;
    file "/var/named/st14.sne24.ru.zone";
};
```

The zone file for `sne24.ru.zone` could potentially look like this (with subdomains for other students) and be reachable from the university network (addresses in the 10.0.0.0/8 range for organization networks).

![image-20241112210741716](/home/ahmed/.config/Typora/typora-user-images/image-20241112210741716.png)


