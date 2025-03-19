# Lab 3 - CI/CD Infrastructure

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Prepare Infrastructure

### 1.1. Deploy three VMs

- Three VMs created using Terraform (as shown in AWS).

  ![image-20250207204208318](https://i.imgur.com/EqdeWUW.png)

- As shown, all instances are in the same VPC and subnet, and thus, should be reachable from each other. This will also be verified in the upcoming steps.

- All source code used is attached to the report.

### 1.2. Set up Gitlab Server (VM1)

- Installed Docker with compose plugin on the VM

  ```bash
  sudo apt install docker.io
  
  # Post-installation steps
  sudo groupadd docker
  sudo usermod -aG docker $USER
  newgrp docker
  docker run hello-world # verify
  
  # Install compose plugin
  DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
  mkdir -p $DOCKER_CONFIG/cli-plugins
  curl -SL https://github.com/docker/compose/releases/download/v2.32.4/docker-compose-linux-x86_64 -o $DOCKER_CONFIG/cli-plugins/docker-compose
  chmod +x $DOCKER_CONFIG/cli-plugins/docker-compose
  docker compose version # verify
  ```
  
  ![image-20250207225725879](https://i.imgur.com/i62eb1m.png)
  
- Created compose file for the server `nano docker-compose.yaml` (explanations below).

  ```yaml
  name: gitlab
  
  services:
    gitlab:
      image: gitlab/gitlab-ce:latest
      hostname: 'st14.duckdns.org'
      container_name: st14-gitlab
      ports:
        - "80:80" # Web
        - "2222:22" # SSH
      volumes:
        - ./gitlab.rb:/etc/gitlab/gitlab.rb
        - gitlab_data:/var/opt/gitlab
        - gitlab_logs:/var/log/gitlab
      shm_size: '256m'
  
  volumes:
    gitlab_data:
    gitlab_logs:
  ```

- Custom `gitlab.rb` used to set external URL and optimize memory usage of the container by disabling unused services [[reference](https://docs.gitlab.com/omnibus/settings/memory_constrained_envs.html)]

  ```bash
  external_url 'st14.duckdns.org'
  registry['enable'] = false
  mattermost['enable'] = false
  gitlab_pages['enable'] = false
  gitlab_kas['enable'] = false
  postgresql['shared_buffers'] = "256MB"
  prometheus['enable'] = false
  puma['worker_processes'] = 0
  sidekiq['concurrency'] = 10
  ```

- Answers & Explanations
  
  1. Compose file pulls `gitlab-ce` image
  
  2. Running container is named `st14-gitlab`
  
  3. Ports 80 (http) and 22 (ssh) are mapped to host
  
  4. Instead of `st14.sne.com`, I used `st14.duckdns.org` (free service that dynamically points that address to my EC2 temporary IP).
  
     ![image-20250208055649704](https://i.imgur.com/x94Ie1y.png)
  
  5. Docker-managed volumes are created for server data and logs. Config file (`gitlab.rb`) shown above is bind-mount from the instance to the container.
  
  6. Running the compose file and making sure the configs are working.
  
     ![image-20250207205247066](https://i.imgur.com/1QUvQIx.png)
  
  7. Access the Gitlab server and log in, create a project and name it as `st14-repo`
  
     - GitLab UI accessible
  
       ![image-20250208000313215](https://i.imgur.com/Qg9cVij.png)
  
     - Changed root password
  
       ![image-20250207210831144](https://i.imgur.com/TOR5fnb.png)
  
     - Logged-in as root, disabled new sign-ups, then created a less privileged account for me.
  
       ![image-20250208000455000](https://i.imgur.com/UF3dydw.png)
  
     - Logged in as `ahmed`, created the required repo
  
       ![image-20250208000602709](https://i.imgur.com/COXEt1f.png)

### 1.3. Set up GitLab runner (VM2)

- Logged in back GitLab as `root`, created an instance runner from Admin Area > Runners with the required tag `st14-runner`. It then shows the commands needed to register the runner (executed below).

  ![image-20250207214902948](https://i.imgur.com/bDu3V1b.png)

- Followd the [docs](https://docs.gitlab.com/runner/install//linux-repository.html#install-gitlab-runner) to install `gitlab-runner` CLI on the Runner VM. Command used:

  ```bash
  curl -L "https://packages.gitlab.com/install/repositories/runner/gitlab-runner/script.deb.sh" | sudo bash
  sudo apt install gitlab-runner
  ```

- Registered the runner with a `shell` executor

  - The executor determines in which environment the jobs from `.gitlab-ci.yml` will be run

  - For example, `docker` executor launches a container for the CI scripts to run inside, while the `shell` executor runs them directly within the runner shell environment (under the `gitlab-runner` user account)

    ![](https://i.imgur.com/8RsXfy6.png)

- Verified the runner is online

  ![image-20250208005236021](/home/ahmed/.config/Typora/typora-user-images/image-20250208005236021.png)

### 1.4. Set up deployment server (VM3)

- Install docker on the deployment server.

  ```bash
  sudo apt install docker.io
  sudo usermod -aG docker $USER
  newgrp docker
  ```

- To setup passwordless SSH authentication between the runner and the deployment VMs

  - Generate SSH key pair on the runner machine (left)
  - Copy the public key to the deployment machine (right) to the `~/.ssh/authorized_keys` file.

  ![image-20250208023409393](https://i.imgur.com/11k7767.png)

## Task 2 - Create CI/CD Pipeline

1. Clone `st14-repo` created in the previous task.

   ```bash
   git clone http://st14.duckdns.org/ahmed/st14-repo
   cd st14-repo
   ```

2. Add sample app written in Python/Flask that shows current time

   - Application tested locally with Docker

     ![image-20250208024216329](https://i.imgur.com/2b0H4l2.png)

   - Push project to GitLab

     ```bash
     git add -A
     git commit -m "Initial commit"
     git push --set-upstream origin main
     ```

     ![image-20250208002445502](https://i.imgur.com/c9IXbo4.png)

3. Added `.gitlab-ci.yml` to run tests, build image, and push it to dockerhub.

   - For the CI to work with the shell executor, we need to have CLI utilities for Docker and Python be available and accessible by the `gitlab-runner` system user.

     ```bash
     sudo apt install docker.io python3-pip python3-venv
     sudo usermod -aG docker gitlab-runner
     newgrp docker
     ```

   - Pipeline file runs

     - CI: `pytest` -> `docker build` -> `docker push`
       - Variable `DOCKER_TOKEN` was added from the UI.
     - CD: `docker pull` -> `docker run` (over SSH @ deployment server)

     ```yaml
     stages:
       - test
       - build-and-push
       - deploy
     
     test:
       stage: test
       script:
         - |
           python3 -m venv venv \
           && source venv/bin/activate \
           && pip install -r requirements.txt \
           && pytest
       tags:
         - st14-runner
     
     build-and-push:
       stage: build-and-push
       needs: ["test"]
       script:
         - |
           docker build -t sh3b0/app . \
           && echo "$DOCKER_TOKEN" | docker login -u sh3b0 --password-stdin \
           && docker push sh3b0/app --all-tags
       tags:
         - st14-runner
     
     deploy:
       stage: deploy
       needs: ["build-and-push"]
       script:
         - |
           echo "docker pull sh3b0/app && docker run --name app -d -p80:8080 sh3b0/app" | ssh ubuntu@timeapp.duckdns.org /bin/bash 
       tags:
         - st14-runner
     ```

3. Validate that the deployment is successful by accessing the web app via the browser on deployment server side.

   ![image-20250208025647822](https://i.imgur.com/blsdKkx.png)

   ![image-20250208030311730](https://i.imgur.com/EcqNAFO.png)

## Task 3 - Polish the Pipeline

1. Update the CD stages to be able to deploy the web application using Ansible.

   - Need first to install Ansible on the runner

     ```bash
     sudo apt install -y pipx
     pipx install --include-deps ansible
     ```

   - Update the deploy stage from `.gitlab-ci.yml` to be as follows:

     ```yaml
     deploy:
       stage: deploy
       needs: ["build-and-push"]
       script:
         - ansible-playbook -i hosts site.yaml
       tags:
         - st14-runner
     ```

   - Inventory file

     ```ini
     [app-server]
     timeapp.duckdns.org
     ```

   - Content of `site.yaml` (simple playbook to pull and run app image)

     ```yaml
     ---
     - name: Deploy App
       hosts: app-server
       remote_user: ubuntu
       become: true
       tasks:
         - name: Pull the latest app image
           community.docker.docker_image:
             name: sh3b0/app
             tag: latest
             source: pull
     
         - name: Deploy the app container
           community.docker.docker_container:
             name: app
             image: sh3b0/app
             state: started
             ports:
               - "80:8080"
     ```

   - Stage run

     ![image-20250208043538564](https://i.imgur.com/6FcOqmq.png)

2. Update the pipeline to support multi-branch (e.g. master and develop) and jobs should be triggered based on the specific target branch.

   - We can add a rule such that the deploy step is only run for commits to the `main` branch.

     ```yaml
     deploy:
       stage: deploy
       needs: ["build-and-push"]
       script:
         - ansible-playbook site.yaml
       tags:
         - st14-runner
       rules:
         - if: '$CI_COMMIT_REF_NAME == "main"'
     ```

2. Use keywords such as `cache`, `artifact`, `needs`, and `dependencies` to have more control of pipeline execution.

   - We may use `cache` to save Python dependencies between jobs for faster CI runs [[docs](https://docs.gitlab.com/ee/ci/caching/index.html#cache-python-dependencies)]

   - An `artifact` can be extracted from the pipeline (e.g., `pytest` report)

   - Job ordering is already enforced with `needs` so they run sequentially.

   - By default, all artifacts are passed between jobs. There is no need to pass test reports to further stages so we can define `dependencies: []` for the subsequent two stages.

   - Pipeline after updates

     ```yaml
     stages:
       - test
       - build-and-push
       - deploy
     
     test:
       stage: test
       cache:
         - paths:
             - .cache/pip
       artifacts:
         paths:
           - report.xml
       variables:
         PIP_CACHE_DIR: "$CI_PROJECT_DIR/.cache/pip"
       script:
         - |
           python3 -m venv venv \
           && source venv/bin/activate \
           && pip install -r requirements.txt \
           && pytest --junit-xml=report.xml
       tags:
         - st14-runner
     
     build-and-push:
       stage: build-and-push
       needs: ["test"]
       script:
         - |
           docker build -t sh3b0/app . \
           && echo "$DOCKER_TOKEN" | docker login -u sh3b0 --password-stdin \
           && docker push sh3b0/app --all-tags
       tags:
         - st14-runner
       dependencies: []
     
     deploy:
       stage: deploy
       needs: ["build-and-push"]
       script:
         - ansible-playbook -i hosts site.yaml
       tags:
         - st14-runner
       dependencies: []
     ```

   - Pipeline run shows pip cache was created and artifact with `pytest` report is uploaded.
   
     ![image-20250208044201806](https://i.imgur.com/kXTRchQ.png) 

## Task 4 - Disaster Recovery

### 4.1. Backup GitLab

- Running `gitlab-backup create` to make an archive of server data.

  ![image-20250208044929402](https://i.imgur.com/pYYiitM.png)

- The backup is stored at `/var/opt/gitlab/backups`, taking it outside the container.

  ```bash
  docker cp st14-gitlab:/var/opt/gitlab/backups/1738979284_2025_02_08_17.8.1_gitlab_backup.tar .
  ```

- The warning asks to backup configs `gitlab.rb` and `gitlab-secrets.json`  manually as they may contain secrets.

  - The first file is mounted from host so that should not be a problem

  - The second one can be copied with the command

    ```bash
    docker cp st14-gitlab:/etc/gitlab/gitlab-secrets.json .
    ```

- Results

  ![image-20250208050646673](https://i.imgur.com/ZKIWQpV.png)

### 4.2. Destroy GitLab data, then restore it from backup

- Angry sysadmin destroying stuff

  ![image-20250208051230246](https://i.imgur.com/b7o1ZWC.png)

- Re-ran the server. All data is lost

  ![image-20250208053356673](https://i.imgur.com/n0lfp0M.png)

- Restore commands [[ref.](https://docs.gitlab.com/ee/administration/backup_restore/restore_gitlab.html)]

  ```bash
  # Rerun GitLab
  docker compose up
  
  # Copy secrets and backup files back to the new container
  docker cp backup/gitlab-secrets.json st14-gitlab:/etc/gitlab/gitlab-secrets.json
  docker cp backup/1738979284_2025_02_08_17.8.1_gitlab_backup.tar st14-gitlab:/var/opt/gitlab/backups/
  
  # Exec into the container
  docker exec -it st14-gitlab bash
  
  # Stop services using the DB
  gitlab-ctl stop puma
  gitlab-ctl stop sidekiq
  gitlab-ctl status
  
  # Give correct permissions and restore from backup file
  cd /var/opt/gitlab/backups/
  chown git:git 1738979284_2025_02_08_17.8.1_gitlab_backup.tar
  gitlab-backup restore BACKUP=1738979284_2025_02_08_17.8.1
  
  # Restart the server
  gitlab-ctl restart
  ```

- Calm sysadmin repairing stuff

  ![image-20250208052508541](https://i.imgur.com/1vENtga.png)

  ![image-20250208054517571](https://i.imgur.com/pmCS3Rq.png)

  ![image-20250208053723302](https://i.imgur.com/B76FdDv.png)

  ![image-20250208053751139](https://i.imgur.com/pRZyUY3.png)

### 4.3. Verify successful restore

- Logged in as `root`, data seems to be there.

  ![image-20250208054320722](https://i.imgur.com/86KcS3n.png)

- Logged in as `ahmed`, server state is restored.

  ![image-20250208054435333](https://i.imgur.com/Mb54mBL.png)

