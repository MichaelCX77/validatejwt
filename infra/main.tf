module "ecs_service" {
  source          = "git::https://github.com/MichaelCX77/infra-base.git//modules/ecs_service?ref=develop"
  project_name    = var.project_name
  cpu             = var.cpu
  memory          = var.memory
  container_port  = var.container_port
  desired_count   = var.desired_count
  vpc_name        = var.vpc_name
}