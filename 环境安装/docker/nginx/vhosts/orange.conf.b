server  {
        listen       80;
        server_name jisuan.zheshige.com;
        proxy_set_header Host $host; 
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 120;
        proxy_send_timeout 120;
        access_log /var/log/nginx/jisuan_access.log main;
        
	location ^~ /api {
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_http_version 1.1;
           proxy_set_header Upgrade $http_upgrade;
           proxy_set_header Connection $connection_upgrade;
           rewrite ^/api/(.*)$ /$1 break;
           proxy_pass http://orange;
      	}
         
        location / {
           root /usr/share/nginx/wwwroot/orange;
	   index index.html index.htm;
        }
                 
}
