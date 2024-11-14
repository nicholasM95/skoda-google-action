module "vault" {
  source     = "git::https://github.com/nicholasM95/terraform-modules.git//modules/vault?ref=v1.8.2"
  vault_path = var.name
}

module "dns" {
  source    = "git::https://github.com/nicholasM95/terraform-modules.git//modules/dns-cloudflare?ref=v1.8.2"
  ip        = var.server_ip
  name      = var.name
  host_name = var.host_name
}

module "application" {
  source           = "git::https://github.com/nicholasM95/terraform-modules.git//modules/k8s-helm-release?ref=v1.8.2"
  image_tag        = var.image_tag
  application_name = var.name
  namespace_name   = var.namespace
  helm_path        = "../../application"
  docker_config    = var.docker_config
  vault_uri        = var.vault_uri
}

module "vault_connection" {
  depends_on                = [module.vault]
  source                    = "git::https://github.com/nicholasM95/terraform-modules.git//modules/vault-k8s?ref=v1.8.2"
  vault_path                = var.name
  kubernetes_ca_cert        = var.kubernetes_ca_cert
  kubernetes_internal_host  = var.kubernetes_internal_host
  service_account_name      = var.name
  service_account_namespace = var.namespace
}
