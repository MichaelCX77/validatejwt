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
  default     = 8080
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

variable "health_check_path" {
  description = "Caminho HTTP para o health check"
  type        = string
  default     = "/actuator/health"
}

variable "health_check_interval" {
  description = "Intervalo em segundos entre as verificações de health check"
  type        = number
  default     = 30
}

variable "health_check_timeout" {
  description = "Timeout em segundos para o health check"
  type        = number
  default     = 5
}

variable "health_check_healthy_threshold" {
  description = "Número de respostas sucessivas para considerar o target saudável"
  type        = number
  default     = 2
}

variable "unhealthy_threshold" {
  description = "Número de respostas falhas para considerar o target não saudável"
  type        = number
  default     = 2
}
