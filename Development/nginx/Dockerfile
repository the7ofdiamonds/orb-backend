FROM ubuntu

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y bash nginx wget openssl

RUN chown -R www-data:www-data /var/log/nginx/ \
    && chmod -R 755 /var/log/nginx/

COPY ./nginx.conf /etc/nginx/nginx.conf

# COPY ./java.conf /etc/nginx/java.conf

COPY ./sites-available /etc/nginx/sites-available

COPY ./ssl/certs/dhparam.pem /etc/ssl/certs/dhparam.pem

COPY ./ssl/certs/nginx-selfsigned.crt /etc/ssl/certs/nginx-selfsigned.crt
     
COPY ./ssl/private/nginx-selfsigned.key /etc/ssl/private/nginx-selfsigned.key

COPY ./snippets/self-signed.conf /etc/nginx/snippets/self-signed.conf

COPY ./snippets/ssl-params.conf /etc/nginx/snippets/ssl-params.conf

RUN ln -s /etc/nginx/sites-available/index.conf /etc/nginx/sites-enabled/

WORKDIR /var/www

RUN rm /etc/nginx/sites-enabled/default \
    && rm /etc/nginx/sites-available/default \
    && rm -r /etc/nginx/conf.d

RUN apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["nginx", "-g", "daemon off;"]