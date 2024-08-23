resource "random_uuid" "vault_role_id" {
}

resource "random_uuid" "vault_secret_id" {
}

module "vault" {
  source     = "git::https://github.com/nicholasM95/terraform-modules.git//modules/vault?ref=v1.3.4"
  role_id    = random_uuid.vault_role_id.result
  secret_id  = random_uuid.vault_secret_id.result
  vault_path = "skoda-google-actions"
  data_json = jsonencode(
    {
      SKODA_EMAIL    = var.skoda_email,
      SKODA_PASSWORD = var.skoda_password,
      SKODA_PIN      = var.skoda_pin,
      SKODA_VIN      = var.skoda_vin,
      ISSUER_URI     = var.issuer_url,
    }
  )
}

module "dns" {
  source    = "git::https://github.com/nicholasM95/terraform-modules.git//modules/dns-cloudflare?ref=v1.3.4"
  ip        = var.server_ip
  name      = "skoda-google-actions"
  host_name = var.host_name
}

module "application" {
  source           = "git::https://github.com/nicholasM95/terraform-modules.git//modules/k8s-helm-release?ref=v1.3.4"
  image_tag        = var.image_tag
  application_name = var.name
  namespace_name   = var.namespace
  helm_path        = "../../application"
  docker_config    = var.docker_config
  vault_uri        = var.vault_uri
  vault_role_id    = random_uuid.vault_role_id.result
  vault_secret_id  = random_uuid.vault_secret_id.result
}
