# Lab 5 - Mandatory Access Control

> Ahmed Nouralla - a.shaaban@innopolis.universty

[TOC]

## Task 1 - AppArmor

### 1.1. CIS benchmarks checks on an endpoint

- Deploy a Debian 12 Cloud VM in the same network with Wazuh Manager

  ![image-20250307152658216](https://i.imgur.com/hcg9zbS.png)

- Refer to the previous lab for Wazuh Manager installation.

  - Manager IP: `192.168.122.72` 

- Install Wazuh Agent on the machine

  ```bash
  wget https://packages.wazuh.com/4.x/apt/pool/main/w/wazuh-agent/wazuh-agent_4.11.0-1_amd64.deb && sudo WAZUH_MANAGER='192.168.122.72' dpkg -i ./wazuh-agent_4.11.0-1_amd64.deb
  sudo systemctl daemon-reload
  sudo systemctl enable wazuh-agent
  sudo systemctl start wazuh-agent
  ```

  ![image-20250308164249473](https://i.imgur.com/anWvrKv.png)

- CIS Benchmark are automatically checked and reported in Wazuh Dashboard.

  ![image-20250308191149099](https://i.imgur.com/QvVG5LE.png)  

  ![image-20250308191125265](https://i.imgur.com/AlNrxZq.png)

- SCA policy checks could be enabled and configured from manager config [[ref.](https://documentation.wazuh.com/current/user-manual/capabilities/sec-config-assessment/available-sca-policies.html)]

  ![image-20250308191518116](https://i.imgur.com/ZjJTBJx.png)

###  1.2. Fulfilling MAC section from CIS benchmarks

- Used CIS benchmark PDF for Debian 12 (section 1.3)

  ![image-20250307145451092](https://i.imgur.com/1HMNlJE.png)

- Ensure AppArmor is installed

  ![image-20250307151440306](https://i.imgur.com/VUWlHij.png)

- Ensure AppArmor is enabled in the bootloader configuration

  ![image-20250307151630991](https://i.imgur.com/NE9ft3m.png)

- Ensure all AppArmor Profiles are enforcing

  ![image-20250307152311572](https://i.imgur.com/6SSYpgQ.png)

### 1.3. WebApp Directory Confinement with AppArmor

1. Prepare demo

   ```bash
   # Install nginx webserver
   sudo apt update && sudo apt install nginx
   
   # Create two directories for static files
   sudo mkdir -p /var/www/dir1
   sudo mkdir -p /var/www/dir2
   
   # Add sample files to each directory
   echo "This is dir1/file1.txt" | sudo tee /var/www/dir1/file1.txt
   echo "This is dir2/file2.txt" | sudo tee /var/www/dir2/file2.txt
   
   # Add config to serve files from both directories
   sudo nano /etc/nginx/sites-available/default
   ```

2. Server config

   ```nginx
   server {
       listen 80 default_server;
       listen [::]:80 default_server;
   
       root /var/www;
   
       location /dir1/ {
           alias /var/www/dir1/;
       }
   
       location /dir2/ {
           alias /var/www/dir2/;
       }
   }
   ```

3. Restart `nginx`

   ```bash
   sudo systemctl restart nginx
   ```

4. Testing file access

   ![image-20250307154042975](https://i.imgur.com/EJsFEnR.png)

   ![image-20250307154053937](https://i.imgur.com/iPS3eWr.png)

5. Create, apply, and test AppArmor config

   - Generate a default profile for nginx

     ```bash
     sudo aa-genprof nginx # Press F (to pay respect)
     ```

   - Edit the generated file at `/etc/apparmor.d/usr.sbin.nginx` [[ref.](https://github.com/jelly/apparmor-profiles/blob/master/usr.bin.nginx)]

     ```htaccess
     abi <abi/3.0>,
     
     include <tunables/global>
     
     /usr/sbin/nginx {
       include <abstractions/base>
     
       capability dac_override,
       capability dac_read_search,
       capability net_bind_service,
       capability setgid,
       capability setuid,
       
       # tcp/ip sockets
       network inet tcp,
       network inet6 tcp,
     
       # binary, pid
       /usr/sbin/nginx mr,
       /run/nginx.pid rw,
     
       # system files for baseauth and more.
       /etc/group r,
       /etc/nsswitch.conf r, 
       /etc/passwd r,
     
       # configuration, logs, and ssl conf
       /etc/nginx/** r,
       /var/log/nginx/* w,
       /etc/ssl/openssl.cnf r,
       
       # Allow access to files in this directory (task)
       /var/www/dir1/* r,
     }
     ```

   - Reload AppArmor profiles and restart nginx server

     ```bash
     sudo systemctl reload apparmor
     sudo systemctl restart nginx
     ```

   - Test the rules, we can access files in `dir1` but not `dir2`

     ![image-20250308162408337](https://i.imgur.com/imEkuql.png)

   - AppArmor log

     ![image-20250308162552186](https://i.imgur.com/NY25YSb.png)

   - Changed profile to complain mode, access was restored.

     ![image-20250308163033821](https://i.imgur.com/akD0qZ8.png)

### 1.4. Profiles & Troubleshooting

4. **Briefly explain how AppArmor uses default profiles to secure your services**
   - Profiles restrict what files, directories, and capabilities a program (binary) can access, effectively reducing attack surface.
   - Profiles are placed in `/etc/apparmor.d` and CLI tools for `aa-*` are used to generate and manipulate profiles.
   - The package `apparmor-profiles` includes default profiles to secure popular packages and services (e.g., `firefox`, `tcpdump`, `libvirt`, etc.)
4. **In a situation where your WebApp fails to start or misbehaving after the AppArmor profile has been enforced (i.e AppArmor confinement). How would you rectify this? What steps would you take to troubleshoot this?**
  - Use `aa-complain` to configure the profile in complain mode.
    - AppArmor will log violations but not enforce them.
    - Inspect such logs to understand what AppArmor restrictions caused the WebApp to fail/misbehave.
    - Modify the profile, reload apparmor with `systemctl`, and re-inspect logs.
    - Once done, use `aa-enforce` to configure AppArmor to actively block violations.
  - Package `apparmor_urils` includes utilities to help troubleshoot and inspect AppArmor.
    - `apparmor_parser` loads AppArmor profiles into the kernel (e.g., helps with debugging and syntax checks)
    - `apparmor_status` displays various information about the current AppArmor policy (i.e., loaded profiles and their modes)

## Task 2 - SELinux

### 2.1. Overview

Security-Enhanced Linux is a kernel security module (set of kernel modifications and user-space tools) for supporting Mandatory Access Control

- It's used to enforce strict security policies that define how processes and users can interact with files, directories, and other resources.

### 2.2. Use case

- Deploy a Fedora 41 Cloud VM. It comes with SELinux pre-installed

  ![image-20250308183920558](https://i.imgur.com/Zog6XCo.png)

- Run a sample Python/Flask app with podman

  ![image-20250308184706856](https://i.imgur.com/15b7ffs.png)

- Install ApacheBench (for stress-testing)

  ```bash
  sudo yum install httpd-tools
  ```

- Disable SELinux, then run the bench

  ![image-20250308185715348](https://i.imgur.com/OpDFesa.png)

- Re-enable SELinux, create a custom policy for the container

  ``` bash
  # Enable SELinux
  sudo nano /etc/selinux/config # configure SELINUX=enforcing
  sudo reboot # To apply changes
  
  # Install needed tools
  sudo dnf install udica
  ```

- Create container policy with udica [[ref.](https://docs.redhat.com/en/documentation/red_hat_enterprise_linux/8/html/using_selinux/creating-selinux-policies-for-containers_using-selinux)]

  > Based on the results, `udica` detects which Linux capabilities are required by the container and creates an SELinux rule allowing all these capabilities. If the container binds to a specific port, `udica` uses SELinux user-space libraries to get the correct SELinux label of a port that is used by the inspected container.

  ```bash
  podman inspect app | sudo udica my_container
  ```

  ![image-20250308220146534](https://i.imgur.com/XHqw6YV.png)

- Re-run the bench and compare results.

  ![image-20250308220508500](https://i.imgur.com/WsuHCRM.png)

- We can observe a very slight performance degradation (less requests per second and more time per request) when using SELinux vs. when not using it.
  - That's a small tradeoff to consider for additional security.

