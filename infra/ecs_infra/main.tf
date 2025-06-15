module "ecs_infra" {
  source         = "git::https://github.com/MichaelCX77/infra-base.git//ecs_infra?ref=develop"
  aws_region     = var.aws_region
  project_name   = var.project_name
  container_port = var.container_port
  vpc_id         = var.vpc_id
  subnet_ids     = var.subnet_ids

  health_check_path              = var.health_check_path
  health_check_interval          = var.health_check_interval
  health_check_timeout           = var.health_check_timeout
  health_check_healthy_threshold = var.health_check_healthy_threshold
  unhealthy_threshold            = var.unhealthy_threshold

  min_capacity     = var.min_capacity
  max_capacity     = var.max_capacity
  cpu_target_value = var.cpu_target_value

  # Repassando as variáveis de schedule
  schedule_down_cron         = var.schedule_down_cron
  schedule_down_min_capacity = var.schedule_down_min_capacity
  schedule_down_max_capacity = var.schedule_down_max_capacity

  schedule_up_cron           = var.schedule_up_cron
  schedule_up_min_capacity   = var.schedule_up_min_capacity
  schedule_up_max_capacity   = var.schedule_up_max_capacity
}
