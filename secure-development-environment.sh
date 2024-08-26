#!/bin/bash

# Country
read -p "Enter the Country (e.g., US): " country

# State or Province
read -p "Enter the State or Province (e.g., New York): " state

# City
read -p "Enter the City (e.g., New York): " city

# Organization Name
read -p "Enter the Organization Name (e.g., THE7OFDIAMONDS.TECH): " org_name

# Organizational Unit Name
read -p "Enter the Organization Department (e.g., Development): " org_unit_name

# Domain Name
read -p "Enter the domain name (e.g., the7ofdiamonds.development): " domain_name

# Email
read -p "Enter your email: " email

# Create directories
sudo mkdir -p ./nginx/logs ./nginx/ssl/certs ./nginx/ssl/private

# Create Self Signed Certificate
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout ./nginx/ssl/private/nginx-selfsigned.key -out ./nginx/ssl/certs/nginx-selfsigned.crt -subj "/C=$country/ST=$state/L=$city/O=$org_name/OU=$org_unit_name/CN=$domain_name/emailAddress=$email"

sudo openssl dhparam -out ./nginx/ssl/certs/dhparam.pem 2048

# Run docker-compose.yaml
docker-compose up -d

# Create secure local domain
echo "127.0.0.1   $domain_name" | sudo tee -a /etc/hosts
