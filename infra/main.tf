module "ecs_service" {
  source          = "git::https://github.com/MichaelCX77/infra-base.git//modules/ecs_service?ref=develop"
  project_name    = "meu-projeto-filho"
  cpu             = 256
  memory          = 512
  container_port  = 8080
  desired_count   = 2
}