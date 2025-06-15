module "ecs_infra" {
  source         = "git::https://github.com/MichaelCX77/infra-base.git//ecs_infra?ref=develop"
  aws_region     = var.aws_region
  project_name   = var.project_name
  container_port = var.container_port
  vpc_id         = var.vpc_id
  subnet_ids     = var.subnet_ids
}
