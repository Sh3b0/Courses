output "instance_id" {
  description = "The ID of the EC2 instance"
  value       = aws_instance.demo.id
}

output "instance_public_ip" {
  description = "The public IP of the EC2 instance"
  value       = aws_instance.demo.public_ip
}

output "instance_dns_name" {
  description = "The DNS name of the EC2 instance"
  value       = aws_instance.demo.public_dns
}
