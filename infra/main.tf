module "ecs_service" {
  source          = "../../infra-base/"
  project_name    = var.project_name
  cpu             = var.cpu
  memory          = var.memory
  container_port  = var.container_port
  desired_count   = var.desired_count
  vpc_id          = var.vpc_id
  subnet_ids      = var.subnet_ids
  capacity_provider     = var.capacity_provider
}