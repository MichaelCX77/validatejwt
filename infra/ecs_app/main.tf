module "ecs_app" {
  source            = "git::https://github.com/MichaelCX77/infra-base.git//ecs_app?ref=develop"
  project_name      = var.project_name
  cpu               = var.cpu
  memory            = var.memory
  container_port    = var.container_port
  desired_count     = var.desired_count
  vpc_id            = var.vpc_id
  subnet_ids        = var.subnet_ids
  capacity_provider = var.capacity_provider
  cluster_id        = var.cluster_id
  target_group_arn  = var.target_group_arn
  security_group_id = var.security_group_id
  image             = var.image
}
