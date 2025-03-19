# ansible-terraform-lab

Demo with Terraform and Ansible.

## Steps Taken

> Check [Tool-Installation](#tool-installation) first if needed

1. Using an AWS EC2 instance for a simple deployment of the application in `app` directory.

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

3. Using Ansible for installing Docker on the target machine and deploying the app.

4. In `ansible` directory

   - Added a role `install_docker` and used it in `main.yaml`

   - Added a task to pull and run app image from DockerHub (image is public)

   - Used [aws_ec2_inventory](https://docs.ansible.com/ansible/latest/collections/amazon/aws/aws_ec2_inventory.html) for dynamic inventory

     ![image-20250130230704669](https://i.imgur.com/3kGojba.png)

   - Started an `ssh-agent` then used `ssh-add` to add the `.pem` key for connecting to the machine

   - Run `ansible-lint`

     ![image-20250130230755052](https://i.imgur.com/RFa9Pia.png)

   - Execute the playbook with `ansible-playbook main.yaml`

     ![image-20250131000425725](https://i.imgur.com/x8luGgR.png)

   - Verify docker is installed and container is running in the instance

     ![image-20250131001100813](https://i.imgur.com/hvJBqW0.png)  

   - Verify app is accessible over HTTP

     ![image-20250131000819481](https://i.imgur.com/n4oysIl.png)

## Tool Installation

- Terraform CLI

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
