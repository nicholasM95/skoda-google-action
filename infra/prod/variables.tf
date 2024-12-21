variable "docker_config" {
  description = "Docker config to pull an image"
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

variable "kubernetes_ca_cert" {
  description = "Kubernetes ca cert"
  type        = string
}

variable "kubernetes_internal_host" {
  description = "Kubernetes internal host"
  type        = string
}
