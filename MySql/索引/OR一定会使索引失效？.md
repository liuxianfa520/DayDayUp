# [转]mysql5.7关于使用到OR是否会用到索引并提高查询效率的探讨

> 转载自：https://www.cnblogs.com/soysauce/p/10414296.html

相信很多人在mysql中看到了where条件中使用到了or就会以为这样是不会走索引的，通常会使用union all或者in 来进行优化，事实并不是想象的这样具体问题具体分析。

下面我们来看看

首先我们用sysbench生成两个100w行的表，表结构如下

```sql
mysql> show create table sbtest1 \G;
       Table: sbtest1
Create Table: CREATE TABLE `sbtest1` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `k` int(11) NOT NULL DEFAULT '0',
  `c` char(120) NOT NULL DEFAULT '',
  `pad` char(60) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `k_1` (`k`),
  KEY `c_1` (`c`)
) ENGINE=InnoDB AUTO_INCREMENT=1000001 DEFAULT CHARSET=latin1
1 row in set (0.00 sec)


mysql> show create table sbtest2 \G;
       Table: sbtest2
Create Table: CREATE TABLE `sbtest2` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `k` int(11) NOT NULL DEFAULT '0',
  `c` char(120) NOT NULL DEFAULT '',
  `pad` char(60) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `k_2` (`k`),
  KEY `c_2` (`c`)
) ENGINE=InnoDB AUTO_INCREMENT=1000001 DEFAULT CHARSET=latin1
1 row in set (0.00 sec)
```



## 1.同一列带索引字段的进行查询

```sh
mysql> explain select * from sbtest1 where k='501462' or k='502480';   
+----+-------------+---------+------------+-------+---------------+------+---------+------+------+----------+-----------------------+
| id | select_type | table   | partitions | type  | possible_keys | key  | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+---------+------------+-------+---------------+------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | sbtest1 | NULL       | range | k_1           | k_1  | 4       | NULL |  214 |   100.00 | Using index condition |
+----+-------------+---------+------------+-------+---------------+------+---------+------+------+----------+-----------------------+
```

从执行计划中看出，这样是**可以使用到索引的**。

另外我们使用in 或者union all来看：

```sh
mysql> explain select pad from sbtest1 where k in ('501462','502480');
+----+-------------+---------+------------+-------+---------------+------+---------+------+------+----------+-----------------------+
| id | select_type | table   | partitions | type  | possible_keys | key  | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+---------+------------+-------+---------------+------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | sbtest1 | NULL       | range | k_1           | k_1  | 4       | NULL |  214 |   100.00 | Using index condition |
+----+-------------+---------+------------+-------+---------------+------+---------+------+------+----------+-----------------------+
```

in的执行计划和or相同，也使用了索引。

```sh
mysql>  explain select pad from sbtest1 where k='501462' union all select pad from sbtest1 where k='502480';
+----+-------------+---------+------------+------+---------------+------+---------+-------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref   | rows | filtered | Extra |
+----+-------------+---------+------------+------+---------------+------+---------+-------+------+----------+-------+
|  1 | PRIMARY     | sbtest1 | NULL       | ref  | k_1           | k_1  | 4       | const |  113 |   100.00 | NULL  |
|  2 | UNION       | sbtest1 | NULL       | ref  | k_1           | k_1  | 4       | const |  101 |   100.00 | NULL  |
```

虽然执行计划不同，但union all估计的查询行数和上面相同，使用了索引。



## 2.不同列都带索引字段的进行查询

```sh
mysql> explain select pad from sbtest1 where  k='501462' or c='68487932199-96439406143-93774651418-41631865787-96406072701-20604855487-25459966574-28203206787-41238978918-19503783441';
+----+-------------+---------+------------+-------------+---------------+---------+---------+------+------+----------+-----------------------------------+
| id | select_type | table   | partitions | type        | possible_keys | key     | key_len | ref  | rows | filtered | Extra                             |
+----+-------------+---------+------------+-------------+---------------+---------+---------+------+------+----------+-----------------------------------+
|  1 | SIMPLE      | sbtest1 | NULL       | index_merge | k_1,c_1       | k_1,c_1 | 4,120   | NULL |  114 |   100.00 | Using union(k_1,c_1); Using where |
+----+-------------+---------+------------+-------------+---------------+---------+---------+------+------+----------+-----------------------------------
```

这样的情况**也会使用索引**。



## 3.or的条件中有个条件不带索引

**如果or的条件中有个条件不带索引的话，那这条sql就不会使用到索引了**。

`pad`列没创建索引，所以整条的sql就不会使用到索引：

```sh
mysql> explain select pad from sbtest1 where  k='501462' or pad='00592560354-80393027097-78244247549-39135306455-88936868384';
+----+-------------+---------+------------+------+---------------+------+---------+------+--------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows   | filtered | Extra       |
+----+-------------+---------+------------+------+---------------+------+---------+------+--------+----------+-------------+
|  1 | SIMPLE      | sbtest1 | NULL       | ALL  | k_1           | NULL | NULL    | NULL | 986400 |    19.00 | Using where |
+----+-------------+---------+------------+------+---------------+------+---------+------+--------+----------+-------------+
```

假设使用union all来改写一样需要全表扫描，所以意义也不大，如下

```sh
mysql>  explain select pad from sbtest1 where  k='501462' union all select pad from sbtest1 where pad='00592560354-80393027097-78244247549-39135306455-88936868384';
+----+-------------+---------+------------+------+---------------+------+---------+-------+--------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref   | rows   | filtered | Extra       |
+----+-------------+---------+------------+------+---------------+------+---------+-------+--------+----------+-------------+
|  1 | PRIMARY     | sbtest1 | NULL       | ref  | k_1           | k_1  | 4       | const |    113 |   100.00 | NULL        |
|  2 | UNION       | sbtest1 | NULL       | ALL  | NULL          | NULL | NULL    | NULL  | 986400 |    10.00 | Using where |
+----+-------------+---------+------------+------+---------------+------+---------+-------+--------+----------+-------------+
```

上面的结果，第二条数据`type=AL`，`rows=986400`，所以意义也不大。



## 4.多表关联查询

```sh
mysql> explain select a.pad,b.pad from sbtest1 a,sbtest2 b where a.id=b.id and (a.c='123' or b.c='1234');                 
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+--------+----------+-------------+
| id | select_type | table | partitions | type   | possible_keys | key     | key_len | ref       | rows   | filtered | Extra       |
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+--------+----------+-------------+
|  1 | SIMPLE      | a     | NULL       | ALL    | PRIMARY,c_1   | NULL    | NULL    | NULL      | 986400 |   100.00 | NULL        |
|  1 | SIMPLE      | b     | NULL       | eq_ref | PRIMARY,c_2   | PRIMARY | 4       | test.a.id |      1 |   100.00 | Using where |
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+--------+----------+-------------+
2 rows in set, 1 warning (0.00 sec)

mysql> explain select a.pad,b.pad from sbtest1 a,sbtest2 b where a.id=b.id and (a.c='123' or a.c='1234'); 
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+------+----------+-----------------------+
| id | select_type | table | partitions | type   | possible_keys | key     | key_len | ref       | rows | filtered | Extra                 |
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+------+----------+-----------------------+
|  1 | SIMPLE      | a     | NULL       | range  | PRIMARY,c_1   | c_1     | 120     | NULL      |    2 |   100.00 | Using index condition |
|  1 | SIMPLE      | b     | NULL       | eq_ref | PRIMARY       | PRIMARY | 4       | test.a.id |    1 |   100.00 | NULL                  |
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+------+----------+-----------------------+
2 rows in set, 1 warning (0.00 sec)

mysql> 
```

可以看出在多表查询的情况下or条件如果不在同一个表内，执行计划表a的查询不走索引。

我们试试看用union all来进行改写

```sh
mysql> explain select a.pad,a.c from sbtest1 a,sbtest2 b where a.id=b.id and a.c='123' union all select a.pad,a.c from sbtest1 a,sbtest2 b where a.id=b.id and b.c='1234';
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+------+----------+-------------+
| id | select_type | table | partitions | type   | possible_keys | key     | key_len | ref       | rows | filtered | Extra       |
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+------+----------+-------------+
|  1 | PRIMARY     | a     | NULL       | ref    | PRIMARY,c_1   | c_1     | 120     | const     |    1 |   100.00 | NULL        |
|  1 | PRIMARY     | b     | NULL       | eq_ref | PRIMARY       | PRIMARY | 4       | test.a.id |    1 |   100.00 | Using index |
|  2 | UNION       | b     | NULL       | ref    | PRIMARY,c_2   | c_2     | 120     | const     |    1 |   100.00 | Using index |
|  2 | UNION       | a     | NULL       | eq_ref | PRIMARY       | PRIMARY | 4       | test.b.id |    1 |   100.00 | NULL        |
+----+-------------+-------+------------+--------+---------------+---------+---------+-----------+------+----------+-------------+
```

在or的条件不在同一个表的情况下 使用union all来改写扫描行数减少且会走索引。

 

 





转载自：https://www.cnblogs.com/soysauce/p/10414296.html