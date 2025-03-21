variable "db_password" {
  description = "The password for the RDS instance"
  type        = string
  sensitive   = true
}

variable "db_username" {
  description = "The username for the RDS instance"
  type        = string
}

variable "key_name"{
  description = "Key name"
  type = string
}
