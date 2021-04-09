
-- 创建数据库 test1
CREATE DATABASE IF NOT EXISTS test1;
use test1;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `sex` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

INSERT INTO `user` (`id`, `name`, `sex`) VALUES ('1', '[这是从数据库1中查询出来的名字]', 'man');



-- 创建数据库 test2
CREATE DATABASE IF NOT EXISTS test2;
use test2;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `sex` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

INSERT INTO `user` (`id`, `name`, `sex`) VALUES ('1', '[这是从数据库2中查询出来的名字]', 'man');
