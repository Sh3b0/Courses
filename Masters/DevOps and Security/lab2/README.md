# Lab 2 - IaC with Cloud

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Theory

### Ansible

**Overview:** a <u>playbook</u> (YAML file) defines a list of <u>plays</u> to be executed against one or more machines (from the <u>inventory</u>) through an SSH connection.

- `ansible.cfg` file [[ref.](https://docs.ansible.com/ansible/latest/reference_appendices/config.html)]
  - INI file for configuring Ansible behavior (setting CLI options, playbook keywords, variables, etc.)
  - Precedence order: 
    - `ANSIBLE_CONFIG` (environment variable if set)
    - `./ansible.cfg` (project configuration - in the current directory)
    - `~/.ansible.cfg` (user configuration - in the home directory)
    - `/etc/ansible/ansible.cfg` (global configuration)
- `inventory` directory [[ref.](https://docs.ansible.com/ansible/latest/inventory_guide/intro_inventory.html)]
  
  - Contains INI/YAML files or Python scripts for describing the list (or group of lists) of hosts (targets) being managed (automated) by Ansible and how to reach them (e.g., by hostname/IP address or using a dynamic inventory).
  - A single inventory file can be placed in `ansible` project root. However, if interacting with multiple inventories, it's recommended to place all their configs in one `inventory` directory.
  
- `roles` directory [[ref.](https://docs.ansible.com/ansible/latest/playbook_guide/playbooks_reuse_roles.html)]

  - Contains a subdirectory for each Ansible role (subdirectory name is the role name)
  - Roles are the standard way to group related Ansible artifacts (vars, files, tasks, etc.) to share them so they can be loaded and reused.

- Each role has a [defined](https://docs.ansible.com/ansible/latest/playbook_guide/playbooks_reuse_roles.html#role-directory-structure) directory structure:

  - `tasks`: a list of tasks that the role provides to the play (execution scenario).
  - `defaults/vars/group_vars`: for managing variables used by the role and their values.
    - `default`: lower precedence default values for role variables
    - `vars`: high precedence variables provided by the role to the play
    - `group_vars`: variables specific to groups of hosts (e.g., `webservers` group)
    - Precedence order is specified [in the docs](https://docs.ansible.com/ansible/latest/playbook_guide/playbooks_variables.html#understanding-variable-precedence). 
  - `handlers`: contains tasks run only when notified that Ansible made changes to the target machine
  - `templates`: contains any files to be processed by [Jinja2](https://jinja.palletsprojects.com/en/stable/) (templating engine) for usage in the roles (e.g., to create parametrized config files).
  - `meta`: metadata for the role, including role dependencies and optional Galaxy metadata such as platforms supported. This is required for uploading into galaxy as a standalone role, but not for using the role in your play.

- `playbooks` directory: this note from the [docs](https://docs.ansible.com/ansible/latest/tips_tricks/sample_setup.html) describe it perfectly

  ![image-20250130185914338](https://i.imgur.com/mOnkAEE.png)

### Terraform

A minimal module contains the following standard structure [[ref.](https://developer.hashicorp.com/terraform/language/modules/develop/structure)].

- `main.tf`: entrypoint with the main resource definitions.
  - For a simple module, this may be where all the resources are created.
  - For a more complex module, resource creation may be split into multiple files.
- `variables`: contains input variables (module parameters) and locals (shorthand for an expression)
- `outputs.tf`: like return values for the module. Prints results to the terminal after applying the module.

## Task 2 - Prepare Application

- Old Node.js project used: https://github.com/NearByrds/NearBirds
- New repo with the rest of the lab work: https://github.com/DEPI-DevOps/ansible-terraform-lab

## Task 3 - Dockerize Application

- A slightly modified version of the original Dockerfile to follow best practices

  ```bash
  # Use a minimal base image
  FROM node:20-alpine 
  
  # Set working directory
  WORKDIR /app
  
  # Copy package files first to leverage Docker caching
  COPY package.json package-lock.json ./
  
  # Install dependencies in a clean environment
  RUN npm ci --omit=dev
  
  # Copy application files separately (after installing dependencies)
  COPY . .
  
  # Use a non-root user for security
  RUN addgroup -S app && adduser -S app -G app && chown -R app:app .
  USER app
  
  # Expose the application port
  EXPOSE 3000
  
  # Define the startup command
  CMD ["npm", "run", "start"]
  ```

- Build and push the image to DockerHub. Find it [here](https://hub.docker.com/repository/docker/sh3b0/nearbirds).

  ```bash
  docker login -u sh3b0
  docker build -t sh3b0/nearbirds .
  docker push sh3b0/nearbirds --all-tags
  ```

- The application didn't utilize an external services.

  - However, one may deploy the app with a database (e.g., `mongo`) and a web server (e.g., `nginx`) using a minimal `docker-compose.yaml` like this:

  ```bash
  name: 'Sample app with Nginx and MongoDB'
  services:
    app:
      image: sh3b0/nearbirds
      container_name: app
      environment:
        - NODE_ENV=production
        - MONGO_URI=mongodb://mongodb:27017/db
      depends_on:
        - mongodb
  
    mongodb:
      image: mongo:5.0
      container_name: mongodb
      volumes:
        - mongodb_data:/data/db
      environment:
        - MONGO_INITDB_ROOT_USERNAME=root
        - MONGO_INITDB_ROOT_PASSWORD=example
  
    nginx:
      image: nginx:1.27-alpine
      container_name: nginx
      ports:
        - "80:80"
      volumes:
        - ./nginx.conf:/etc/nginx/nginx.conf
      depends_on:
        - app
  
  volumes:
    mongodb_data:
  ```

- Needless to say, this would require

  - Modifications for the app to actually connect to the db (e.g.,  using `mongoose` library in Node.js) and use it for some functionality.

  - Nginx config to forward traffic to the app as a reverse proxy

    ```bash
    events {}
    
    http {
      server {
        listen 80;
    
        location / {
          proxy_pass http://app:3000;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
        }
      }
    
    ```

- However, these tasks seem out of scope of the assignment goals.

## Task 4 - Deliver Application using Terraform & Ansible

> See [Appendix](#Appendix) for Tools installation

1. Will be using an AWS EC2 instance for a simple deployment of the application in `app` directory.

   ```bash
   nano ~/.aws/config  # Updated with AWS connection config
   ```

2. In `terraform` directory, added the required files to provision an EC2 instance (and its security group for HTTP and SSH access) to be used for hosting the app.

   ```bash
   cd terraform
   terraform init      # Prepare workspace and download providers
   terraform validate  # Check configuration for validity
   terraform fmt       # Format source file
   terraform plan      # Show execution plan
   terraform apply     # Execute the plan
   ```

   - Init, validate, fmt, and plan:

     ![image-20250130224717537](https://i.imgur.com/ZNTaOcJ.png)

   - Apply:

     ![image-20250130225058625](https://i.imgur.com/NuojSiS.png)

   - Result in AWS

     ![image-20250130225300451](https://i.imgur.com/43dy5y4.png)

3. Will be using Ansible for installing Docker on the target machine and deploy the app.

4. In `ansible` directory

   - Added a role `install_docker` and a playbook using it in `main.yaml`

   - Added a task to pull and run app image from DockerHub (image is public)

   - Created `aws_ec2.yaml` for dynamic inventory using  [aws_ec2_inventory](https://docs.ansible.com/ansible/latest/collections/amazon/aws/aws_ec2_inventory.html)  plugin

     ![image-20250130230704669](https://i.imgur.com/3kGojba.png)

   - Started an `ssh-agent` then used `ssh-add key.pem` to add the `.pem` key for connecting to the machine

   - Ran `ansible-lint` on the created files.

     ![image-20250130230755052](https://i.imgur.com/RFa9Pia.png)

   - Executed the playbook with `ansible-playbook main.yaml`

     ![image-20250131000425725](https://i.imgur.com/x8luGgR.png)

   - Verify docker is installed and container is running in the instance

     ![image-20250131001100813](https://i.imgur.com/hvJBqW0.png)  

   - Verify app is accessible over HTTP

     ![image-20250131000819481](https://i.imgur.com/n4oysIl.png)

5. Files used are available at https://github.com/DEPI-DevOps/ansible-terraform-lab.

## Task 5 - Experiment with Ansible

- Let's create a role that installs and configures `apache2` web server on my machine.

- Ansible features used:

  - `playbooks`/`inventory`/`roles` in separate directories

  - `ansible.cfg` configured to work with the specified directory structure.

  - `ansible-lint` is happy

    ![image-20250202160015516](https://i.imgur.com/oXP7cJW.png)

  - `roles/web` defines the following:

    - `tasks` to install `apache2` with `apt` and ensure the service is started and enabled,
    - Jinja2 `template` for server configuration that is copied from controller to node.
      - If the configuration changes, notify a `handler` to restart the server.
    - The template refers to an Ansible variable to configure apache2 log directory
    - The value from role `defaults` is used, unless a higher precedence value is specified (e.g., a CLI option).

- Playbook execution and testing

  ![image-20250202155030087](https://i.imgur.com/dytHdVs.png)

- Server is responding and logs are present in the specified directory.

  ![image-20250202160153568](https://i.imgur.com/6KoYEEj.png)

- Experiment code is available in the same repo, under `experiment` directory.

  https://github.com/DEPI-DevOps/ansible-terraform-lab/tree/main/experiment

## Appendix: Tools Installation

Some BASH to install and prepare needed tools, because why not.

-  Terraform CLI

  ```bash
  cd /tmp
  export RELEASE=$(curl -s https://api.github.com/repos/hashicorp/terraform/releases/latest | jq -r '.tag_name')
  RELEASE="${RELEASE:1}" # Remove the 'v'
  wget "https://releases.hashicorp.com/terraform/${RELEASE}/terraform_${RELEASE}_linux_amd64.zip"
  unzip terraform_${RELEASE}_linux_amd64.zip
  sudo mv terraform /usr/local/bin
  ```

- AWS CLI

  ```bash
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
  unzip awscliv2.zip
  sudo ./aws/install
  aws configure
  ```

- Ansible ([docs](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html#installing-and-upgrading-ansible-with-pipx) recommend installation using [pipx](https://github.com/pypa/pipx), probably to avoid conflicts with system's Python environment)

  ```bash
  # If needed, install pipx first
  sudo apt install -y pipx
  
  # Installs a large ansible package (includes docker and aws collections)
  pipx install --include-deps ansible
  
  # Install ansible-lint and AWS Python SDKs in the same venv with ansible
  pipx inject --include-deps ansible ansible-lint boto3 botocore
  ```

  
