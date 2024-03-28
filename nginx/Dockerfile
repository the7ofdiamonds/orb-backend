FROM nginx

COPY conf.d/default.conf /etc/nginx/conf.d/default.conf
COPY ssl/certs/dhparam.pem /etc/ssl/certs/dhparam.pem
COPY ssl/certs/nginx-selfsigned.crt /etc/ssl/certs/nginx-selfsigned.crt
COPY ssl/private/nginx-selfsigned.key /etc/ssl/private/nginx-selfsigned.key
COPY snippets/self-signed.conf /etc/nginx/snippets/self-signed.conf
COPY snippets/ssl-params.conf /etc/nginx/snippets/ssl-params.conf

ENTRYPOINT ["nginx", "-g", "daemon off;"]