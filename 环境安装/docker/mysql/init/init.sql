-- 密码改成tiger
ALTER USER 'root'@'%' IDENTIFIED BY 'tiger' PASSWORD EXPIRE NEVER;
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '1234567';
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'tiger';

-- 授权root用户客户端都可以访问
GRANT All privileges ON *.* TO 'root'@'%';
