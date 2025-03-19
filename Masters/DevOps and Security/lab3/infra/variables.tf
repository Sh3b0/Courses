variable "instance_type" {
  description = "Type of the EC2 instance"
  type        = string
  default     = "t3.medium"
}

variable "key_name" {
  description = "Name of the key pair to use for the instance"
  type        = string
  default     = "vockey"
}

variable "ami" {
  description = "Amazon Machine Image Identifier"
  type        = string
  default     = "ami-04a81a99f5ec58529" # Ubuntu 24.04
}

