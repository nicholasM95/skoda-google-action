terraform {
  required_providers {
    helm = {
      source  = "hashicorp/helm"
      version = "2.14.0"
    }

    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "4.38.0"
    }

    random = {
      source  = "hashicorp/random"
      version = "3.6.2"
    }

    vault = {
      source  = "hashicorp/vault"
      version = "4.3.0"
    }

    aws = {
      source  = "hashicorp/aws"
      version = "5.59.0"
    }
  }

  backend "s3" {
    bucket = "nicholasmeyers-skoda-google-action-prd-terraform-state"
    key    = "terraform.tfstate"
    region = "eu-west-1"
  }
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

provider "vault" {
}

provider "random" {
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      "app" = "skoda_google_action"
    }
  }
}
