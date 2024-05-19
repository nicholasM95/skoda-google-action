variable "docker_config" {
  description = "Docker config to pull an image"
  type        = string
}

variable "server_ip" {
  description = "Server ip"
  type        = string
}

variable "host_name" {
  description = "Host name"
  type        = string
}

variable "vault_uri" {
  description = "Vault uri"
  type        = string
}

variable "aws_region" {
  description = "aws region"
  type        = string
  default     = "eu-west-1"
}

variable "namespace" {
  description = "Kubernetes namespace"
  type        = string
  default     = "skoda-google-actions"
}

variable "name" {
  description = "Application name"
  type        = string
  default     = "skoda-google-actions"
}

variable "image_tag" {
  description = "Image tag"
  type        = string
}

variable "skoda_email" {
  description = "Skoda email"
  type        = string
}

variable "skoda_password" {
  description = "Skoda password"
  type        = string
}

variable "skoda_vin" {
  description = "Skoda vin"
  type        = string
}

variable "skoda_pin" {
  description = "Skoda pin"
  type        = string
}

variable "issuer_url" {
  description = "Issuer url"
  type        = string
}

variable "sentry_dsn" {
  description = "Sentry dsn"
  type = string
}
