# Lab 4 - SIEM

> Ahmed Nouralla - a.shaaban@innopolis.universty

[TOC]

## Task 1 - Introduction

**a. Give a brief explanation of the architecture of your SIEM solution.**

- **Agent:** a lightweight service installed on endpoints (nodes being monitored), it collects and pushes data (e.g., logs) to the server.

- **Server:** main component to process and analyze data received from agents, it supports a cluster mode (master+workers) for larger deployments with large data volumes. It exposes a REST API for interaction.

- **Indexer:** to store data and organize for faster search and retrieval.

- **Dashboard:** for users to query and visualize collected data.

  ![](https://documentation.wazuh.com/current/_images/deployment-architecture1.png)

**b. Provide 3 advantages of open source solutions and how do these vendors actually make money?**

- Advantages:
  - Being free to use: accessible to businesses of all size.
  - Flexibility to customize: if needed, users can fork and customize the tool for their own needs.
  - Community support: a large open community gives better support, faster detection and resolution of bugs.
- Vendors like Wazuh generate revenue in many ways:
  - Paid support and consultation
  - Enterprise editions and add-ons
  - Providing training and certifications
  - Donations and sponsorships

## Task 2 - Setup Infrastructure

### 2.1. Setting Up Wazuh Server (in Docker)

> Docs: https://documentation.wazuh.com/current/deployment-options/docker/docker-installation.html

1. Increase `max_map_count` on your Docker host:

   ```bash
   sysctl -w vm.max_map_count=262144
   ```

1. Download compose files and configs needed to run a `single-node` deployment for this lab

   ```bash
   wget -O wazuh-docker.zip https://codeload.github.com/wazuh/wazuh-docker/zip/refs/tags/v4.11.0
   unzip wazuh-docker.zip
   cd wazuh-docker-4.11.0/single-node
   ```

1. Generate indexer certs.

   ![image-20250224214906470](https://i.imgur.com/QUMc2Sc.png)

1. Add generated `root-ca.pem` to the browser trust store (for local testing)

   ![image-20250224220135279](https://i.imgur.com/i1yZLpI.png)

1. Run the services

   ![image-20250225200841446](https://i.imgur.com/ZM9oH0v.png)

1. Add an entry in `/etc/hosts` for `192.168.122.72  wazuh.dashboard`

1. Access the dashboard at https://wazuh.dashboard and login with credentials specified in compose file (follow [docs](https://documentation.wazuh.com/current/deployment-options/docker/wazuh-container.html#change-the-password-of-wazuh-users) to update the default credentials)

   ![image-20250224220517687](https://i.imgur.com/vmvA3CQ.png)

### 2.2. Configuring Agents on Monitoring Targets

#### 2.2.1. Preparation

1. Obtain images

   ```bash
   # Ubuntu Cloud Guest (server image)
   wget https://github.com/GNS3/gns3-registry/raw/master/cloud-init/ubuntu-cloud/ubuntu-cloud-init-data.iso
   wget https://cloud-images.ubuntu.com/releases/noble/release-20241004/ubuntu-24.04-server-cloudimg-amd64.img
   
   # Windows 7 image
   wget https://dl.bobpony.com/windows/7/en_windows_7_with_sp1_x64.iso
   
   # MicroTik router image
   wget https://download.mikrotik.com/routeros/7.16/chr-7.16.img.zip
   unhzip chr-7.16.img.zip && rm chr-7.16.img.zip
   ```

2. Ran images in `virt-manager` with QEMU.

   - All VMs are placed in the same network along with Wazuh server.
   - Wrote the IPs here for reference

   ![image-20250226004352319](https://i.imgur.com/cfIbhdJ.png)


#### 2.2.2. Ubuntu Endpoint (`linux-host`)

- Commands used to install the agent

  ```bash
  # Install .deb amd64 agent
  wget https://packages.wazuh.com/4.x/apt/pool/main/w/wazuh-agent/wazuh-agent_4.11.0-1_amd64.deb && sudo WAZUH_MANAGER='192.168.122.72' WAZUH_AGENT_NAME='linux-host' dpkg -i ./wazuh-agent_4.11.0-1_amd64.deb
  
  # Start agent service
  sudo systemctl daemon-reload
  sudo systemctl enable wazuh-agent
  sudo systemctl start wazuh-agent
  ```

- Execution

  ![image-20250226004847334](https://i.imgur.com/gbz1l8z.png)

- Agent is active

  ![image-20250226005100499](https://i.imgur.com/kYEGQBx.png)


- The agent forwards information and logs from the endpoint to the manager. We can view stats in "Dashboard" tab.

  ![image-20250226005201690](https://i.imgur.com/HvFn9Ct.png)

- We can also inspect logs in "Events" tab. Currently, it shows authentication events (login success/failure) from SSH and PAM. It also shows events about agent status (start/stop).

  ![image-20250226010238506](https://i.imgur.com/ADyfKJM.png)

#### 2.2.3. Windows Endpoint (`windows-host`)

- Downloaded and installed Wazuh Windows agent from [here](https://packages.wazuh.com/4.x/windows/wazuh-agent-4.11.0-1.msi)

- Specified Manager IP to contact.

  ![image-20250225214853995](https://i.imgur.com/tKjDVw7.png)

- Started the service with `NET START Wazuh`

  ![image-20250225214726382](https://i.imgur.com/pfsK5Iy.png)

- Agent is active. Dashboard is also available.

  ![image-20250225214700968](https://i.imgur.com/4IpFl50.png) 

  ![image-20250226010727030](https://i.imgur.com/m2KaSts.png)

- Events from Windows are being collected and forwarded to Wazuh

  ![image-20250226010833483](https://i.imgur.com/nbccACe.png)

- Inspecting the event about login success

  ![image-20250226011222105](https://i.imgur.com/jJE9Os8.png)

#### 2.2.4. MikroTik Router

- **Goal:** setup agent-less monitoring by having the router send logs to Wazuh manager.

- Update `/var/ossec/etc/ossec.conf` to enable receiving remote syslogs on port 514. The `local_ip` is the IP address of the container running `wazuh-manager`.

  ```xml
  <remote>
      <connection>syslog</connection>
      <port>514</port>
      <protocol>udp</protocol>
      <allowed-ips>0.0.0.0/0</allowed-ips>
      <local_ip>172.18.0.3</local_ip>
  </remote>
  ```

- Add community-developed decoders and rules for MikroTik to manager

  ```bash
  # Download community-developed decoders and rules from GitHub
  wget https://raw.githubusercontent.com/angolo40/WazuhMikrotik/refs/heads/main/local_rules.xml
  wget https://raw.githubusercontent.com/angolo40/WazuhMikrotik/refs/heads/main/1001-mikrotik_decoders.xml
  
  # Copy them to the Wazuh manager container
  docker cp 1001-mikrotik_decoders.xml single-node-wazuh.manager-1:/var/ossec/etc/decoders/1001-mikrotik_decoders.xml
  docker cp local_rules.xml single-node-wazuh.manager-1:/var/ossec/etc/rules/local_rules.xml
  
  # Restart Wazuh manager
  docker restart single-node-wazuh.manager-1
  ```

  ![image-20250225223117529](https://i.imgur.com/jxImzts.png)

- Rules and decoders are visible in the UI

  ![image-20250226003004036](https://i.imgur.com/dCzDoSY.png)

- Configure remote logging in the router

  ```bash
  # Configure remote logging from MikroTik Router to the Wazuh server
  /system logging action add name=remote target=remote remote=192.168.122.72
  
  # Add a logging rule to send all logs to the remote server
  /system logging add action=remote topics=system
  /system logging add action=remote topics=info
  ```

- Verify config in router admin page

  ![image-20250226002538868](https://i.imgur.com/X5Scy5F.png)

- Logged out and back in to the router CLI. Event is visible in the discover page.

  ![image-20250226001641912](https://i.imgur.com/74sfNAl.png)

## Task 3 - Use cases

### 3.1. Blocking a known malicious actor

> Guide: https://documentation.wazuh.com/current/proof-of-concept-guide/block-malicious-actor-ip-reputation.html

- Install `apache2` server on the Linux host: `apt install apache2`

  ![image-20250227190014457](https://i.imgur.com/zj6YKgp.png)

- Add the following to `/var/ossec/etc/ossec.conf` file to configure the Wazuh agent and monitor the Apache access logs. Then restart the agent with `systemctl restart wazuh-agent`

  ```xml
  <localfile>
    <log_format>syslog</log_format>
    <location>/var/log/apache2/access.log</location>
  </localfile>
  ```

- Run the commands in Wazuh manager:

  ```bash
  docker exec -it --user 0 single-node-wazuh.manager-1 bash
  
  # Download the Alienvault IP reputation database:
  yum update && yum install wget
  wget https://raw.githubusercontent.com/firehol/blocklist-ipsets/master/alienvault_reputation.ipset -O /var/ossec/etc/lists/alienvault_reputation.ipset
  
  # Append attacker IP to the database (using router as example)
  echo "192.168.122.140" >> /var/ossec/etc/lists/alienvault_reputation.ipset
  
  # Convert ipset to cdb
  wget https://wazuh.com/resources/iplist-to-cdblist.py -O /tmp/iplist-to-cdblist.py
  /var/ossec/framework/python/bin/python3 /tmp/iplist-to-cdblist.py /var/ossec/etc/lists/alienvault_reputation.ipset /var/ossec/etc/lists/blacklist-alienvault
  rm -rf /var/ossec/etc/lists/alienvault_reputation.ipset
  rm -rf /tmp/iplist-to-cdblist.py
  
  # Assign the right ownership to the generated file
  chown wazuh:wazuh /var/ossec/etc/lists/blacklist-alienvault
  ```

- Configure Wazuh active response:

  1. Add a custom rule to `/var/ossec/etc/rules/local_rules.xml`

     ```xml
     <group name="attack,">
       <rule id="31108" level="10" overwrite="yes">
         <list field="srcip" lookup="address_match_key">etc/lists/blacklist-alienvault</list>
         <description>IP address found in AlienVault reputation database.</description>
       </rule>
     </group>
     ```

  1. Edit the Wazuh server `/var/ossec/etc/ossec.conf` configuration file and add the `etc/lists/blacklist-alienvault` list to the `<ruleset>` section:

     ```xml
     <ossec_config>
       <ruleset>
         <!-- Default ruleset -->
         <decoder_dir>ruleset/decoders</decoder_dir>
         <rule_dir>ruleset/rules</rule_dir>
         <rule_exclude>0215-policy_rules.xml</rule_exclude>
         <list>etc/lists/audit-keys</list>
         <list>etc/lists/amazon/aws-eventnames</list>
         <list>etc/lists/security-eventchannel</list>
         <list>etc/lists/blacklist-alienvault</list>
     
         <!-- User-defined ruleset -->
         <decoder_dir>etc/decoders</decoder_dir>
         <rule_dir>etc/rules</rule_dir>
       </ruleset>
     
     </ossec_config>
     ```

  1. Add the Active Response block to the Wazuh server `/var/ossec/etc/ossec.conf` file:

     ```xml
     <ossec_config>
       <active-response>
         <command>firewall-drop</command>
         <location>local</location>
         <rules_id>31108</rules_id>
         <timeout>60</timeout>
       </active-response>
     </ossec_config>
     ```

  1. Restart the manager

     ```bash
     docker restart single-node-wazuh.manager-1
     ```

- Verify rule from the UI

  ![image-20250227213601740](https://i.imgur.com/8kbdqMx.png)

- Testing if the rule works for the log format

  ![image-20250227213628045](https://i.imgur.com/PljrRnA.png)

- Verify active response is triggered in the threat hunting -> events dashboard.

  ![image-20250227215051245](https://i.imgur.com/LQbVtSn.png)

### 3.2. Detecting a Brute Force attack

> Guide: https://documentation.wazuh.com/current/proof-of-concept-guide/detect-brute-force-attack.html

- Install `hydra` on the `linux-host` (acting as an attacker)

  ```bash
  sudo apt install -y hydra
  ```

- Enable RDP on the `windows-host` (acting as the victim)

  ![image-20250228032733995](https://i.imgur.com/rlQmfgv.png)
  
- Simulate a brure-force attack against RDP service.

  ```bash
  wget https://weakpass.com/download/90/rockyou.txt.gz
  gzip -d rockyou.txt.gz
  hydra -l badguy -P rockyou.txt rdp://192.168.122.124
  ```

  ![image-20250228032157632](https://i.imgur.com/U8OyU9J.png)

- Check the threat hunting dashboard. Failed logins were detected

  ![image-20250228031802203](https://i.imgur.com/4Xyv6SY.png)

## Task 4 - SIEM Integration

> Guide: https://documentation.wazuh.com/current/proof-of-concept-guide/detect-malware-yara-integration.html

### 4.1. Agent Configuration

- Goal: detect malware (whispergate) using YARA integration.

- Download and install YARA

  ```bash
  sudo apt update
  sudo apt install yara
  ```

- Download detection rules to `/tmp/yara/rules`. Add more [rules](https://github.com/cado-security/DFIR_Resources_Whispergate/blob/main/YARA/WhisperGate.yara) for whispergate if needed.

  ![image-20250228042454801](https://i.imgur.com/1JJb5vi.png)

- Create a `yara.sh` script in the `/var/ossec/active-response/bin/` directory. This is necessary for the Wazuh-YARA Active Response scans

  ```bash
  #!/bin/bash
  # 1. Gather parameters
  
  # Extra arguments
  read INPUT_JSON
  YARA_PATH=$(echo $INPUT_JSON | jq -r .parameters.extra_args[1])
  YARA_RULES=$(echo $INPUT_JSON | jq -r .parameters.extra_args[3])
  FILENAME=$(echo $INPUT_JSON | jq -r .parameters.alert.syscheck.path)
  
  # Set LOG_FILE path
  LOG_FILE="logs/active-responses.log"
  
  size=0
  actual_size=$(stat -c %s ${FILENAME})
  while [ ${size} -ne ${actual_size} ]; do
      sleep 1
      size=${actual_size}
      actual_size=$(stat -c %s ${FILENAME})
  done
  
  # 2. Analyze parameters
  
  if [[ ! $YARA_PATH ]] || [[ ! $YARA_RULES ]]
  then
      echo "wazuh-yara: ERROR - Yara active response error. Yara path and rules parameters are mandatory." >> ${LOG_FILE}
      exit 1
  fi
  
  # 3. Main workflow
  # Execute Yara scan on the specified filename
  yara_output="$("${YARA_PATH}"/yara -w -r "$YARA_RULES" "$FILENAME")"
  
  if [[ $yara_output != "" ]]
  then
      # Iterate every detected rule and append it to the LOG_FILE
      while read -r line; do
          echo "wazuh-yara: INFO - Scan result: $line" >> ${LOG_FILE}
      done <<< "$yara_output"
  fi
  
  exit 0;
  ```

- Change `yara.sh` file owner to `root:wazuh` and file permissions to `0750`

  ```bash
  chown root:wazuh /var/ossec/active-response/bin/yara.sh
  chmod 750 /var/ossec/active-response/bin/yara.sh
  ```

- Add the following within the `<syscheck>` block of the Wazuh agent `/var/ossec/etc/ossec.conf` configuration file to monitor the `/tmp/yara/malware` directory:

  ```bash
  <directories realtime="yes">/tmp/yara/malware</directories>
  ```

- Restart the Wazuh agent: `sudo systemctl restart wazuh-agent`

### 4.2. Server Configuration

- Add the rules to `/var/ossec/etc/rules/local_rules.xml`

  ```xml
  <group name="syscheck,">
    <rule id="100300" level="7">
      <if_sid>550</if_sid>
      <field name="file">/tmp/yara/malware/</field>
      <description>File modified in /tmp/yara/malware/ directory.</description>
    </rule>
    <rule id="100301" level="7">
      <if_sid>554</if_sid>
      <field name="file">/tmp/yara/malware/</field>
      <description>File added to /tmp/yara/malware/ directory.</description>
    </rule>
  </group>
  
  <group name="yara,">
    <rule id="108000" level="0">
      <decoded_as>yara_decoder</decoded_as>
      <description>Yara grouping rule</description>
    </rule>
    <rule id="108001" level="12">
      <if_sid>108000</if_sid>
      <match>wazuh-yara: INFO - Scan result: </match>
      <description>File "$(yara_scanned_file)" is a positive match. Yara rule: $(yara_rule)</description>
    </rule>
  </group>
  ```

- Add decoder `/var/ossec/etc/decoders/local_decoder.xml` for extracting information from scan results

  ```xml
  <decoder name="yara_decoder">
    <prematch>wazuh-yara:</prematch>
  </decoder>
  
  <decoder name="yara_decoder1">
    <parent>yara_decoder</parent>
    <regex>wazuh-yara: (\S+) - Scan result: (\S+) (\S+)</regex>
    <order>log_type, yara_rule, yara_scanned_file</order>
  </decoder>
  ```

- Add config for active response in `/var/ossec/etc/ossec.conf`

  ```xml
  <ossec_config>
    <command>
      <name>yara_linux</name>
      <executable>yara.sh</executable>
      <extra_args>-yara_path /usr/bin -yara_rules /tmp/yara/rules/yara_rules.yar</extra_args>
      <timeout_allowed>no</timeout_allowed>
    </command>
  
    <active-response>
      <command>yara_linux</command>
      <location>local</location>
      <rules_id>100300,100301</rules_id>
    </active-response>
  </ossec_config>
  ```

- Restart the manager

  ```bash
  sudo systemctl restart wazuh-manager
  ```

- Rules were added

  ![image-20250228051319180](https://i.imgur.com/JeA8cGc.png)

- Decoder is available

  ![image-20250228051418411](https://i.imgur.com/mmujM4d.png)

### 4.3. Attack Emulation

- Download malware sample to `/tmp/yara/malware`

  ```bash
  git clone https://github.com/cado-security/DFIR_Resources_Whispergate
  cd DFIR_Resources_Whispergate/Samples
  unzip -P "infected" a196c6b8ffcb97ffb276d04f354696e2391311db3841ae16c8c9f56f36a38e92.zip
  mv a1* /tmp/yara/malware/sus
  ```

- Check the threat hunting page. Malware was identified.

  ![image-20250228060038363](https://i.imgur.com/6vEgWEK.png)

