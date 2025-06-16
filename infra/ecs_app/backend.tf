terraform {
  backend "s3" {
    bucket = "terraform-version-435769559418"
    key    = "infra/ecs_app/terraform.tfstate"
    region = "sa-east-1"
    encrypt = true
  }
}