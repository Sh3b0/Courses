# Lab 4 - Incident Response

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Preparation

### 1.1. Deploy Wazuh

**1- Deploy Wazuh central components (server, indexer, and dashboard) on your host machine or use an alternate method like Docker.** 

- Created an Ubuntu server VM

- Prepared the VM as the Docker host following the [docs](https://documentation.wazuh.com/current/deployment-options/docker/docker-installation.html)

  ```bash
  # Increase max_map_count
  sudo sysctl -w vm.max_map_count=262144
  
  # Install docker
  sudo apt update && sudo apt install -y docker.io
  sudo usermod -aG docker $USER
  
  # Diconnect and reconnect to SSH, verify docker works
  docker run hello-world
  
  # Install compose plugin
  DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
  mkdir -p $DOCKER_CONFIG/cli-plugins
  curl -SL https://github.com/docker/compose/releases/download/v2.35.0/docker-compose-linux-x86_64 -o $DOCKER_CONFIG/cli-plugins/docker-compose
  chmod +x $DOCKER_CONFIG/cli-plugins/docker-compose
  docker compose version
  
  # Download files and configs needed to run a single-node Wazuh deployment
  wget -O wazuh-docker.zip https://codeload.github.com/wazuh/wazuh-docker/zip/refs/tags/v4.11.0
  unzip wazuh-docker.zip
  cd wazuh-docker-4.11.0/single-node
  
  # Generate indexer certs
  docker compose -f generate-indexer-certs.yml run --rm generator
  ```

- Verify

  ![image-20250414172047430](https://i.imgur.com/EEY7yc6.png)

- Add generated `root-ca.pem` to the browser trust store (for local testing)

  ![image-20250224220135279](https://i.imgur.com/i1yZLpI.png)

- Run the services

  ![image-20250414173458865](https://i.imgur.com/F9fha9z.png)

- Add an entry in `/etc/hosts` for `192.168.122.192  wazuh.dashboard`

- Access the dashboard at https://wazuh.dashboard and login with credentials specified in compose file (follow [docs](https://documentation.wazuh.com/current/deployment-options/docker/wazuh-container.html#change-the-password-of-wazuh-users) to update the default credentials if needed)

- Verify server status: core components online, others not enabled by default

  ![image-20250414184535176](https://i.imgur.com/Fh8ksQO.png)

### 1.2. Prepare Agent Machine

**2- Set up a second machine with a Unix-like OS, install the Wazuh agent, and enroll it with**
**the Wazuh manager. Enable SSH and create a test user account.**

- Preparation

  ```bash
  # Obtain an Ubuntu Cloud guest
  wget https://cloud-images.ubuntu.com/releases/noble/release-20241004/ubuntu-24.04-server-cloudimg-amd64.img
  
  # Download cloud-init file from GNS3 website, preconfigured with
  #  credentials ubuntu:ubuntu and openssh-server installed
  wget https://github.com/GNS3/gns3-registry/raw/master/cloud-init/ubuntu-cloud/ubuntu-cloud-init-data.iso
  
  # Prepare and resize the image
  cp ubuntu-24.04-server-cloudimg-amd64.img /var/lib/libvirt/images/wazuh.img
  qemu-img resize /var/lib/libvirt/images/agent.img +5G
  
  # Create the vm in virt-manager
  virt-manager
  ```

- Configuration

  ```bash
  # Login to the machine
  ssh ubuntu@192.168.122.172
  
  # Install .deb amd64 Wazuh agent and connect it to manager
  sudo apt update
  wget https://packages.wazuh.com/4.x/apt/pool/main/w/wazuh-agent/wazuh-agent_4.11.0-1_amd64.deb && sudo WAZUH_MANAGER='192.168.122.192' WAZUH_AGENT_NAME='agent' dpkg -i ./wazuh-agent_4.11.0-1_amd64.deb
  
  # Start agent service, verify status
  sudo systemctl daemon-reload
  sudo systemctl enable wazuh-agent
  sudo systemctl start wazuh-agent
  sudo systemctl status wazuh-agent
  
  # Create test account
  sudo adduser test
  ```

- Verification

  ![image-20250414200628555](https://i.imgur.com/orQ0npD.png)

  ![image-20250414190750093](https://i.imgur.com/4F8OEyH.png)

### 1.3. Prepare Attacker Machine

**3- Prepare a third machine to act as the "attacker" endpoint to simulate cyber attacks.**

- Followed the same process but without installing an agent

- All machines are ready. Wrote the assigned IP addresses for convenience.

  ![image-20250414191306524](https://i.imgur.com/vzQRbHp.png)

## Task 2 - Configure Active Response

### 2.1, 2.2. SSH Password Brute-force Detection

1,2- Enable the Wazuh active response feature to disable a user account for 10 minutes when
a brute force attempt is detected. Block the attacker IP for 10 minutes as well [[ref.1](https://documentation.wazuh.com/current/user-manual/capabilities/active-response/ar-use-cases/disabling-user-account.html)] [[ref.2](https://documentation.wazuh.com/current/user-manual/capabilities/active-response/ar-use-cases/blocking-ssh-brute-force.html)]

- Verify predefined commands for  `disable-account` and `firewall-drop` are present in manager config `/var/ossec/etc/ossec.conf`

  - Command `disable-account` locks a user account for a period of time.
  - Command `firewall-drop` uses `iptables` for blocking all traffic from a certain host.

  ```xml
  <command>
    <name>disable-account</name>
    <executable>disable-account</executable>
    <timeout_allowed>yes</timeout_allowed>
  </command>
  <command>
    <name>firewall-drop</name>
    <executable>firewall-drop</executable>
    <timeout_allowed>yes</timeout_allowed>
  </command>
  ```

- Add a custom rules for account blocking at `/var/ossec/etc/rules/local_rules.xml`

  ```xml
  <group name="pam,syslog,">
    <rule id="120100" level="10" frequency="3" timeframe="120">
      <if_matched_sid>5503</if_matched_sid>
      <description>Possible password guess on $(dstuser): 3 failed logins in a short period of time</description>
      <mitre>
        <id>T1110</id>
      </mitre>
    </rule>
  </group>
  ```

- Add active-response blocks to use the commands.

  - Local Location means the blocking command will be executed on the monitored endpoint
  - Predefined rule 5763: `SSHD brute force trying to get access to the system`
  - Custom rule 120100: `Possible password guess ...`
  - Timeout 600 specifies how long the active response action will last. In this case, it blocks the IP address of the attacker for 10 minutes.

  ```xml
  <active-response>
      <command>firewall-drop</command>
      <location>local</location>
      <rules_id>5763</rules_id>
      <timeout>600</timeout>
  </active-response>
  <active-response>
      <command>disable-account</command>
      <location>local</location>
      <rules_id>120100</rules_id>
      <timeout>600</timeout>
  </active-response>
  ```

- Restart the manager (from UI or `sudo systemctl restart wazuh-manager`)

- Verify config loads without errors and the custom rule was added. Check task 3 for the demo.

  ![image-20250415015946751](https://i.imgur.com/FqViVZr.png)

### 2.3. Malware Detection

3- Create `/home/test/malwarefiles/`on the monitored endpoint. Integrate malware detection (e.g., VirusTotal or YARA), and monitor this directory for malware. Configure Wazuh to automatically delete detected malware files [[ref.](https://documentation.wazuh.com/current/proof-of-concept-guide/detect-malware-yara-integration.html)]

#### 2.3.1. Agent config

- Preparation

  ```bash
  # Create directory to be monitored for malware
  mkdir /home/test/malwarefiles/
  
  # Install YARA
  sudo apt update && sudo apt install -y yara
  
  # Download detection rules to `/tmp/yara/rules`
  sudo mkdir -p /tmp/yara/rules
  sudo curl 'https://valhalla.nextron-systems.com/api/v1/get' \
  -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' \
  -H 'Accept-Language: en-US,en;q=0.5' \
  --compressed \
  -H 'Referer: https://valhalla.nextron-systems.com/' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'DNT: 1' -H 'Connection: keep-alive' -H 'Upgrade-Insecure-Requests: 1' \
  --data 'demo=demo&apikey=1111111111111111111111111111111111111111111111111111111111111111&format=text' \
  -o /tmp/yara/rules/yara_rules.yar
  ```

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
          
          # Delete the detected malware file
          if [[ -f "$FILENAME" ]]; then
              rm -f "$FILENAME"
              if [[ $? -eq 0 ]]; then
                  echo "wazuh-yara: INFO - File Deleted: $FILENAME" >> ${LOG_FILE}
              else
                  echo "wazuh-yara: ERROR - Failed to delete file: $FILENAME" >> ${LOG_FILE}
              fi
          else
              echo "wazuh-yara: WARNING - File not found, could not delete: $FILENAME" >> ${LOG_FILE}
          fi
      done <<< "$yara_output"
  fi
  
  exit 0
  ```

- Change `yara.sh` file owner to `root:wazuh` and file permissions to `0750`

  ```bash
  chown root:wazuh /var/ossec/active-response/bin/yara.sh
  chmod 750 /var/ossec/active-response/bin/yara.sh
  ```

- Add the following within the `<syscheck>` block of the Wazuh agent `/var/ossec/etc/ossec.conf` configuration file to monitor the `/home/test/malwarefiles/` directory:

  ```bash
  <directories realtime="yes">/home/test/malwarefiles/</directories>
  ```

- Restart the Wazuh agent: `sudo systemctl restart wazuh-agent`

#### 2.3.2. Manager Config

- Add the rules to `/var/ossec/etc/rules/local_rules.xml`

  ```xml
  <group name="syscheck,">
    <rule id="100300" level="7">
      <if_sid>550</if_sid>
      <field name="file">/home/test/malwarefiles/</field>
      <description>File modified in /home/test/malwarefiles/ directory.</description>
    </rule>
    <rule id="100301" level="7">
      <if_sid>554</if_sid>
      <field name="file">/home/test/malwarefiles/</field>
      <description>File added to/home/test/malwarefiles/ directory.</description>
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

  ![image-20250415020238988](https://i.imgur.com/PlJtWRM.png)

- Decoders are available

  ![image-20250415020322988](https://i.imgur.com/nlAAn6k.png)

## Task 3 - Simulate Attacks

1- Launch an SSH brute force attack from the attacker endpoint against the test user.

- SSH to the `attacker` machine and simulate a brute-force attack against the monitored VM.

  ```bash
  sudo apt update && sudo apt install -y hydra
  wget https://weakpass.com/download/90/rockyou.txt.gz
  gzip -d rockyou.txt.gz
  hydra -t 4 -l test -P rockyou.txt 192.168.122.172 ssh
  ```

2- Show relevant alerts on the Wazuh dashboard.

![image-20250414220042051](https://i.imgur.com/MJTy2PR.png)

3- Provide evidence that the user account was disabled and the attacker IP blocked. Include
dashboard alerts and screenshots from the endpoint involved.

- Agent events show active response logs: both commands for `firewall-drop` and `account-disable` were triggered after the rule IDs specified in configs above were matched.

  ![image-20250414221844606](https://i.imgur.com/KAllKdR.png)

- User account for `test` was locked (`L` flag) and it was not possible to login. After 10 minutes, things went back to normal without intervention.

  ![image-20250414220326394](https://i.imgur.com/Tm0m4Hg.png)

- Attacker was able to ping agent before the attack attempt, but was blocked after the attempt.

  ![image-20250414215106118](https://i.imgur.com/dNHJUwx.png)

- Host unblocked and account unlocked after 10 minutes.

  ![image-20250414221723468](https://i.imgur.com/HV49uct.png)

4- Download a malware sample into the `/home/test/malwarefiles/` directory. Confirm detection and show that it was automatically removed.

- Downloading `webshell` malware binary to the monitored directory

  ```bash
  cd /home/test/malwarefiles
  wget https://wazuh-demo.s3-us-west-1.amazonaws.com/webshell
  ```

- Check events: malware was detected and removed

  ![image-20250414233538263](https://i.imgur.com/3QqicMG.png)

- Verify file was deleted

  ![image-20250414233726862](https://i.imgur.com/9CScxol.png)

## Task 4 - Log Management

**1- Define what a log retention policy is.**

- Log retention policy defines how long log data (alerts, events, archives) is stored before being automatically deleted or archived. It balances storage constraints with compliance/forensic needs.

**2- Explain how logs are rotated on Linux and how disk space is managed in relation to logs.**

- Linux has `logrotate`, a utility that automates log file management by:

  - Rotating (renaming/archiving) logs when they reach a size limit or time threshold.
  - Compressing old logs (e.g., `.gz`) to save space.
  - Deleting very old logs based on retention rules.
  - Restarting services if needed to ensure new logs are written properly.

- Rotation strategy can be

  - Time-based: Delete logs older than `X` days
  - Size-based: Rotate when logs exceed a certain size

- Example: default `apache2` log rotation config at `/etc/logrotate/apache2`

  ```bash
  /var/log/apache2/*.log {
      daily               # Rotate logs every day
      missingok           # Ignore if logs are missing
      rotate 14           # Keep 14 days of logs
      compress            # Compress old logs (saves space)
      delaycompress       # Wait until next rotation to compress
      notifempty          # Skip rotation if log is empty
      create 640 root adm # Set permissions on new log files
      sharedscripts       # Run post-rotation script once
      postrotate
          systemctl reload apache2 > /dev/null
      endscript
  }
  ```

**3- Create a configuration to automatically delete Wazuh alert log files older than 90 days.** [[ref.](https://serverfault.com/questions/1153188/how-can-i-configure-all-in-one-wazuh-for-log-retention)]

> This seems like a trick question, bear with me as I explain my research into this...

- By default, alert logs are written to `/var/ossec/logs/alerts/alerts.log` (and `.json`) before being forwarded to the indexer by Filebeat.

  - This is controlled by `alerts_log` and `jsonout_output` under `global` config
  - Both are set to `yes` by default [[ref.](https://documentation.wazuh.com/current/user-manual/reference/ossec-conf/global.html#alerts-log)]

- Wazuh utilizes and rotates these internal logs by creating files in the format `/var/ossec/logs/alerts/<year>/<month>/ossec-alerts-<day>.log` (and `.json`) [[ref.](https://groups.google.com/g/wazuh/c/mPYcQmegwTw)]

  - As shown, the current `alerts.log` file is hardlinked to the rotated one.

    ![image-20250415005512893](https://i.imgur.com/4Hd5wJi.png)

  - Upon rotation tomorrow, a fresh `alerts.log` will be linked to `2025/Apr/ossec-alerts-15.log`  and so on.

- By default, these internal files logs are retained for 31 days [[ref.](https://documentation.wazuh.com/current/user-manual/reference/internal-options.html#monitord)] as specified by `monitord.keep_log_days`.

- If we wish to override this, we can modify `/var/ossec/etc/local_internal_options.conf` and add `monitord.keep_log_days: 90`, but I don't really see a reason to play with that as it has no effect on the alerts displayed in the UI (since these ones are handled by the indexer).

  ![image-20250415011141099](https://i.imgur.com/dxmsPSe.png)

- Side note: Wazuh may also compress and store all event logs at `/var/ossec/logs/archives` (even the ones that don't generate alerts), this is controlled by `logall` and `logall_json` parameters, which are set to `no` by default.

## Bonus

**1- What are indices, and how do they differ from log files? [[ref.](https://documentation.wazuh.com/current/user-manual/wazuh-indexer/wazuh-indexer-indices.html)]**

- Log files shown above are plain-text/JSON files storing individual log lines and are directly accessible/readable by human.
- An index is more structured and optimized for low-storage and fast retrieval (like database indices on fields like `agent.id` and `rule.description`). Data is accessible through an API or a query language (e.g., from Kibana dashboards)
- The Wazuh Indexer (Elasticsearch) ingests logs, parses them, and stores them in indices.

**2- Create an index retention policy to delete Wazuh alert indices after 90 days.** [[ref.](https://documentation.wazuh.com/current/user-manual/wazuh-indexer-cluster/index-lifecycle-management.html)]

- Index Management -> State Management Policies -> Create Policy

- Add ISM template for `wazuh-alerts-*`

- Create an `initial` state (no action) and a `delete_alerts` state (action = `Delete`).

- Configure the transition from `initial` to `delete_alerts` when the condition `Minimum index age` is `90d`. Should look like this at the end.

  ![image-20250415015341418](https://i.imgur.com/PDvb3tm.png)

- Apply and check resulting JSON config

  ```json
  {
      "id": "wazuh-alert-retention-policy",
      "seqNo": 0,
      "primaryTerm": 1,
      "policy": {
          "policy_id": "wazuh-alert-retention-policy",
          "description": "Log retention policy",
          "last_updated_time": 1744670987601,
          "schema_version": 21,
          "error_notification": null,
          "default_state": "initial",
          "states": [
              {
                  "name": "initial",
                  "actions": [],
                  "transitions": [
                      {
                          "state_name": "delete_alerts",
                          "conditions": {
                              "min_index_age": "90d"
                          }
                      }
                  ]
              },
              {
                  "name": "delete_alerts",
                  "actions": [
                      {
                          "retry": {
                              "count": 3,
                              "backoff": "exponential",
                              "delay": "1m"
                          },
                          "delete": {}
                      }
                  ],
                  "transitions": []
              }
          ],
          "ism_template": [
              {
                  "index_patterns": [
                      "wazuh-alerts-*"
                  ],
                  "priority": 1,
                  "last_updated_time": 1744670987601
              }
          ]
      }
  }
  ```

  
