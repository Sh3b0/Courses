resource "aws_security_group" "allow_http_ssh" {
  name        = "allow_http_ssh"
  description = "Allow HTTP and SSH traffic"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "gitlab-server" {
  ami                    = var.ami
  instance_type          = "t2.medium"
  key_name               = var.key_name
  security_groups = [ aws_security_group.allow_http_ssh.name ]

  root_block_device {
    volume_size = 16
    volume_type = "gp2"
  }

  tags = {
    Name = "GitLab Server"
  }
}

resource "aws_instance" "gitlab-runner" {
  ami                    = var.ami
  instance_type          = "t2.small"
  key_name               = var.key_name
  security_groups = [ aws_security_group.allow_http_ssh.name ]

  tags = {
    Name = "GitLab Runner"
  }
}

resource "aws_instance" "deployment-server" {
  ami                    = var.ami
  instance_type          = "t2.small"
  key_name               = var.key_name
  security_groups = [ aws_security_group.allow_http_ssh.name ]

  tags = {
    Name = "Deployment Server"
  }
}
