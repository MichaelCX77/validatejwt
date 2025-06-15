output "security_group_id" {
  value = module.ecs_infra.security_group_id
}

output "cluster_id" {
  value = module.ecs_infra.cluster_id
}

output "target_group_arn" {
  value = module.ecs_infra.target_group_arn
}