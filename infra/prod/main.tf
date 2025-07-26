module "vault" {
  source     = "git::https://github.com/nicholasM95/terraform-modules.git//modules/vault?ref=v1.12.10"
  vault_path = var.name
}

module "application" {
  source           = "git::https://github.com/nicholasM95/terraform-modules.git//modules/k8s-helm-release?ref=v1.12.10"
  image_tag        = var.image_tag
  application_name = var.name
  namespace_name   = var.namespace
  helm_path        = "../../application"
  docker_config    = var.docker_config
}

module "vault_connection" {
  depends_on                = [module.vault]
  source                    = "git::https://github.com/nicholasM95/terraform-modules.git//modules/vault-k8s?ref=v1.12.10"
  vault_path                = var.name
  service_account_name      = var.name
  service_account_namespace = var.namespace
}