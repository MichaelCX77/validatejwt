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

variable "cpu" {
  description = "Quantidade de CPU para a task"
  type        = number
  default     = 512
}

variable "memory" {
  description = "Quantidade de memória para a task"
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Número de instâncias da task"
  type        = number
  default     = 2
}

variable "container_port" {
  description = "Porta exposta pelo container"
  type        = number
  default     = 8080
}

variable "subnet_ids" {
  description = "Lista de subnets públicas para Load Balancer e ECS"
  type        = list(string)
  default = ["subnet-001b9943999c46f1e","subnet-064cd7760ca940201"]
}

variable "capacity_provider" {
  description = "Tipo de instancias ['FARGATE','FARGATE_SPOT']"
  type        = string
  default = "FARGATE_SPOT"
}

variable "security_group_id" {
  type = string
}

variable "cluster_id" {
  type = string
}

variable "target_group_arn" {
  description = "ARN do target group do ALB"
  type = string
}

variable "image" {
  type    = string
}

# Role ARN
variable "execution_role_arn" {
  type    = string
  default = "arn:aws:iam::435769559418:role/ECSTaskExecutionRole"
}
