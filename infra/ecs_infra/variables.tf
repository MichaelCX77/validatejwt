variable "aws_region" {
  description = "Região da AWS onde os recursos serão criados"
  type        = string
  default     = "sa-east-1"
}

variable "project_name" {
  description = "Nome base do projeto"
  type        = string
  default = "validatejwt"
}

variable "container_port" {
  description = "Porta exposta pelo container"
  type        = number
  default     = 80
}

variable "vpc_id" {
  description = "ID da VPC"
  type        = string
  default = "vpc-095a2552dec82c1b2"
}

variable "subnet_ids" {
  description = "Lista de subnets públicas para Load Balancer e ECS"
  type        = list(string)
  default = ["subnet-001b9943999c46f1e","subnet-064cd7760ca940201"]
}