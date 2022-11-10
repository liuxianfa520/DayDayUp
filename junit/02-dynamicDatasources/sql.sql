CREATE DATABASE `order` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE DATABASE `user` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;



use `user`;

CREATE TABLE `t_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;







use `order`;

CREATE TABLE `t_order` (
   `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
   `order_no` int(11) DEFAULT NULL,
   `sku_id` int(11) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

