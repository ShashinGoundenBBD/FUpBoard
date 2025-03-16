
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    region  = "af-south-1"
  }
}

provider "aws" {
  region =  "af-south-1"
}

resource "aws_default_vpc" "default_vpc" {
  tags = {
    Name = "default_vpc"
  }
}

data "aws_availability_zones" "available_zones" {
  
}

resource "aws_default_subnet" "subnet_az1" {
  availability_zone = data.aws_availability_zones.available_zones.names[0]
}

resource "aws_default_subnet" "subnet_az2" {
  availability_zone = data.aws_availability_zones.available_zones.names[1]
}

resource "aws_security_group" "allow_postgres" {
  name = "allow_postgres"

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "fupboarddb" {
  identifier             = "fupboarddb"
  engine                 = "postgres"
  engine_version         = "16.3"
  instance_class         = "db.t4g.micro"
  db_name                = "fupboarddb"
  allocated_storage      = 20
  storage_type           = "gp2"
  publicly_accessible    = true
  username               = var.db_username
  password               = var.db_password
  skip_final_snapshot    = true
  vpc_security_group_ids = [aws_security_group.allow_postgres.id]
  tags = {
    Name = "fupboarddb"
  }
}

output "db_host" {
  value = aws_db_instance.fupboarddb.endpoint
  description = "The endpoint of the SQL Server RDS instance"
}

# RSA key of size 4096 bits
resource "tls_private_key" "rsa_4096" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "key_pair" {
  key_name   = var.key_name
  public_key = tls_private_key.rsa_4096.public_key_openssh
}

resource "local_file" "private_key"{
  content = tls_private_key.rsa_4096.private_key_pem
  filename = var.key_name
}

resource "aws_instance" "fup_ec2_instance" {
  ami           = "ami-00d6d5db7a745ff3f"
  instance_type = "t3.micro"
  key_name = aws_key_pair.key_pair.key_name
  tags = {
    Name = "fup_ec2_instance"
  }

   user_data = <<-EOF
              #!/bin/bash
              # Install necessary packages
              apt-get update -y
              apt-get install -y openjdk-23-jre wget

              # Download the JAR file from S3
              aws s3 cp s3://${AWS_BUCKET_NAME}/fupboard-api-0.0.1-SNAPSHOT.jar /home/ubuntu/fupboard-api.jar

              # Run the JAR file
              nohup java -jar /home/ubuntu/fupboard-api.jar > /home/ubuntu/fupboard-api.log 2>&1 &
              EOF
}

