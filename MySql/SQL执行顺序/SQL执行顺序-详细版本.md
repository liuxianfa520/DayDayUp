> 本文转载自：[SQL语句执行顺序](https://www.cnblogs.com/Qian123/p/5669259.html)
>
> 仅作为个人学习使用。

# 阅读目录

[TOC]



　　SQL 不同于与其他编程语言的最明显特征是处理代码的顺序。在大数编程语言中，代码按编码顺序被处理，但是在SQL语言中，第一个被处理的子句是FROM子句，尽管SELECT语句第一个出现，但是几乎总是最后被处理。

   每个步骤都会产生一个虚拟表，该虚拟表被用作下一个步骤的输入。这些虚拟表对调用者（客户端应用程序或者外部查询）不可用。只是最后一步生成的表才会返回 给调用者。如果没有在查询中指定某一子句，将跳过相应的步骤。

先来一段伪代码，首先你能看懂么？

```
SELECT DISTINCT <select_list>
FROM <left_table>
<join_type> JOIN <right_table>
ON <join_condition>
WHERE <where_condition>
GROUP BY <group_by_list>
HAVING <having_condition>
ORDER BY <order_by_condition>
LIMIT <limit_number>
```

如果你知道每个关键字的意思，作用，如果你还用过的话，那再好不过了。但是，你知道这些语句，它们的执行顺序你清楚么？



# 准备工作

首先声明下，一切测试操作都是在MySQL数据库上完成，关于MySQL数据库的一些简单操作，请阅读一下文章：

- 《[MySQL扫盲篇](http://www.jellythink.com/archives/636)》
- 《[MySQL存储引擎介绍](http://www.jellythink.com/archives/640)》
- 《[MySQL数据类型和属性](http://www.jellythink.com/archives/642)》
- 《[MySQL处理数据库和表的常用命令](http://www.jellythink.com/archives/646)》

继续做以下的前期准备工作：

1、新建一个测试数据库TestDB；

```
 create database TestDB;
```

2、创建测试表table1和table2；

```
CREATE TABLE table1
 (
     customer_id VARCHAR(10) NOT NULL,
     city VARCHAR(10) NOT NULL,
     PRIMARY KEY(customer_id)
 )ENGINE=INNODB DEFAULT CHARSET=UTF8;

 CREATE TABLE table2
 (
     order_id INT NOT NULL auto_increment,
     customer_id VARCHAR(10),
     PRIMARY KEY(order_id)
 )ENGINE=INNODB DEFAULT CHARSET=UTF8;
```

3、插入测试数据；

```
 INSERT INTO table1(customer_id,city) VALUES('163','hangzhou');
 INSERT INTO table1(customer_id,city) VALUES('9you','shanghai');
 INSERT INTO table1(customer_id,city) VALUES('tx','hangzhou');
 INSERT INTO table1(customer_id,city) VALUES('baidu','hangzhou');

 INSERT INTO table2(customer_id) VALUES('163');
 INSERT INTO table2(customer_id) VALUES('163');
 INSERT INTO table2(customer_id) VALUES('9you');
 INSERT INTO table2(customer_id) VALUES('9you');
 INSERT INTO table2(customer_id) VALUES('9you');
 INSERT INTO table2(customer_id) VALUES('tx');
 INSERT INTO table2(customer_id) VALUES(NULL);
```

准备工作做完以后，table1和table2看起来应该像下面这样：

```
 mysql> select * from table1;
 +-------------+----------+
 | customer_id | city     |
 +-------------+----------+
 | 163         | hangzhou |
 | 9you        | shanghai |
 | baidu       | hangzhou |
 | tx          | hangzhou |
 +-------------+----------+
 4 rows in set (0.00 sec)

 mysql> select * from table2;
 +----------+-------------+
 | order_id | customer_id |
 +----------+-------------+
 |        1 | 163         |
 |        2 | 163         |
 |        3 | 9you        |
 |        4 | 9you        |
 |        5 | 9you        |
 |        6 | tx          |
 |        7 | NULL        |
 +----------+-------------+
 7 rows in set (0.00 sec)
```

4、准备SQL逻辑查询测试语句

```
SELECT a.customer_id, COUNT(b.order_id) as total_orders
 FROM table1 AS a
 LEFT JOIN table2 AS b
 ON a.customer_id = b.customer_id
 WHERE a.city = 'hangzhou'
 GROUP BY a.customer_id
 HAVING count(b.order_id) < 2
 ORDER BY total_orders DESC;
```

使用上述SQL查询语句来获得来自杭州，并且订单数少于2的客户。

# SQL逻辑查询语句执行顺序

还记得上面给出的那一长串的SQL逻辑查询规则么？那么，到底哪个先执行，哪个后执行呢？现在，我先给出一个查询语句的执行顺序：

```
(7)     SELECT 
(8)     DISTINCT <select_list>
(1)     FROM <left_table>
(3)     <join_type> JOIN <right_table>
(2)     ON <join_condition>
(4)     WHERE <where_condition>
(5)     GROUP BY <group_by_list>
(6)     HAVING <having_condition>
(9)     ORDER BY <order_by_condition>
(10)    LIMIT <limit_number>
```

上面在每条语句的前面都标明了执行顺序号，那么各条查询语句是如何执行的呢？

**逻辑查询处理阶段简介**

**![img](https://images2015.cnblogs.com/blog/690102/201607/690102-20160714101317482-683714239.png)**

1. **FROM：**对FROM子句中的前两个表执行**笛卡尔积**（Cartesian product)(交叉联接），生成虚拟表VT1
2. **ON：**对VT1应用ON筛选器。只有那些使<join_condition>为真的行才被插入VT2。
3. **OUTER(JOIN)：**如果指定了OUTER JOIN（相对于CROSS JOIN 或(INNER JOIN),保留表（preserved table：左外部联接把左表标记为保留表，右外部联接把右表标记为保留表，完全外部联接把两个表都标记为保留表）中未找到匹配的行将作为外部行添加到 VT2,生成VT3.如果FROM子句包含两个以上的表，则对上一个联接生成的结果表和下一个表重复执行步骤1到步骤3，直到处理完所有的表为止。
4. **WHERE：**对VT3应用WHERE筛选器。只有使<where_condition>为true的行才被插入VT4.
5. **GROUP BY：**按GROUP BY子句中的列列表对VT4中的行分组，生成VT5.
6. **CUBE|ROLLUP：**把超组(Suppergroups)插入VT5,生成VT6.
7. **HAVING：**对VT6应用HAVING筛选器。只有使<having_condition>为true的组才会被插入VT7.
8. **SELECT：**处理SELECT列表，产生VT8.
9. **DISTINCT：**将重复的行从VT8中移除，产生VT9.
10. **ORDER BY：**将VT9中的行按ORDER BY 子句中的列列表排序，生成游标（VC10).
11. **TOP：**从VC10的开始处选择指定数量或比例的行，生成表VT11,并返回调用者。

注：

笛卡尔积简单介绍：假设集合A={a, b}，集合B={0, 1, 2}，则两个集合的笛卡尔积为{(a, 0), (a, 1), (a, 2), (b, 0), (b, 1), (b, 2)}。

步骤10，按ORDER BY子句中的列列表排序上步返回的行，返回游标VC10.这一步是第一步也是唯一一步可以使用SELECT列表中的列别名的步骤。这一步不同于其它步骤的 是，它不返回有效的表，而是返回一个游标。SQL是基于集合理论的。集合不会预先对它的行排序，它只是成员的逻辑集合，成员的顺序无关紧要。对表进行排序 的查询可以返回一个对象，包含按特定物理顺序组织的行。ANSI把这种对象称为游标。理解这一步是正确理解SQL的基础。

因为这一步不返回表（而是返回游标），使用了ORDER BY子句的查询不能用作表表达式。表表达式包括：视图、内联表值函数、子查询、派生表和共用表达式。它的结果必须返回给期望得到物理记录的客户端应用程序。例如，下面的派生表查询无效，并产生一个错误：

```
select * 
from(select orderid,customerid from orders order by orderid) 
as d
```

下面的视图也会产生错误：

```
create view my_view
as
select *
from orders
order by orderid
```

在SQL中，表表达式中不允许使用带有ORDER BY子句的查询，而在T—SQL中却有一个例外（应用TOP选项）。

   所以要记住，不要为表中的行假设任何特定的顺序。换句话说，除非你确定要有序行，否则不要指定ORDER BY 子句。排序是需要成本的。

# # 执行FROM语句

在这些SQL语句的执行过程中，都会产生一个虚拟表，用来保存SQL语句的执行结果（这是重点），我现在就来跟踪这个虚拟表的变化，得到最终的查询结果的过程，来分析整个SQL逻辑查询的执行顺序和过程。

第一步，执行`FROM`语句。我们首先需要知道最开始从哪个表开始的，这就是`FROM`告诉我们的。现在有了`<left_table>`和`<right_table>`两个表，我们到底从哪个表开始，还是从两个表进行某种联系以后再开始呢？它们之间如何产生联系呢？——笛卡尔积

关于什么是笛卡尔积，请自行Google补脑。经过FROM语句对两个表执行笛卡尔积，会得到一个虚拟表，暂且叫VT1（vitual table 1），内容如下：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| 163         | hangzhou |        1 | 163         |
| 9you        | shanghai |        1 | 163         |
| baidu       | hangzhou |        1 | 163         |
| tx          | hangzhou |        1 | 163         |
| 163         | hangzhou |        2 | 163         |
| 9you        | shanghai |        2 | 163         |
| baidu       | hangzhou |        2 | 163         |
| tx          | hangzhou |        2 | 163         |
| 163         | hangzhou |        3 | 9you        |
| 9you        | shanghai |        3 | 9you        |
| baidu       | hangzhou |        3 | 9you        |
| tx          | hangzhou |        3 | 9you        |
| 163         | hangzhou |        4 | 9you        |
| 9you        | shanghai |        4 | 9you        |
| baidu       | hangzhou |        4 | 9you        |
| tx          | hangzhou |        4 | 9you        |
| 163         | hangzhou |        5 | 9you        |
| 9you        | shanghai |        5 | 9you        |
| baidu       | hangzhou |        5 | 9you        |
| tx          | hangzhou |        5 | 9you        |
| 163         | hangzhou |        6 | tx          |
| 9you        | shanghai |        6 | tx          |
| baidu       | hangzhou |        6 | tx          |
| tx          | hangzhou |        6 | tx          |
| 163         | hangzhou |        7 | NULL        |
| 9you        | shanghai |        7 | NULL        |
| baidu       | hangzhou |        7 | NULL        |
| tx          | hangzhou |        7 | NULL        |
+-------------+----------+----------+-------------+
```

总共有28（table1的记录条数 * table2的记录条数）条记录。这就是VT1的结果，接下来的操作就在VT1的基础上进行。

# # 执行ON过滤

执行完笛卡尔积以后，接着就进行`ON a.customer_id = b.customer_id`条件过滤，根据`ON`中指定的条件，去掉那些不符合条件的数据，得到VT2表，内容如下：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| 163         | hangzhou |        1 | 163         |
| 163         | hangzhou |        2 | 163         |
| 9you        | shanghai |        3 | 9you        |
| 9you        | shanghai |        4 | 9you        |
| 9you        | shanghai |        5 | 9you        |
| tx          | hangzhou |        6 | tx          |
+-------------+----------+----------+-------------+
```

VT2就是经过`ON`条件筛选以后得到的有用数据，而接下来的操作将在VT2的基础上继续进行。

# # 添加外部行

这一步只有在连接类型为`OUTER JOIN`时才发生，如`LEFT OUTER JOIN`、`RIGHT OUTER JOIN`和`FULL OUTER JOIN`。在大多数的时候，我们都是会省略掉`OUTER`关键字的，但`OUTER`表示的就是外部行的概念。

`LEFT OUTER JOIN`把左表记为保留表，得到的结果为：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| 163         | hangzhou |        1 | 163         |
| 163         | hangzhou |        2 | 163         |
| 9you        | shanghai |        3 | 9you        |
| 9you        | shanghai |        4 | 9you        |
| 9you        | shanghai |        5 | 9you        |
| tx          | hangzhou |        6 | tx          |
| baidu       | hangzhou |     NULL | NULL        |
+-------------+----------+----------+-------------+
```

`RIGHT OUTER JOIN`把右表记为保留表，得到的结果为：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| 163         | hangzhou |        1 | 163         |
| 163         | hangzhou |        2 | 163         |
| 9you        | shanghai |        3 | 9you        |
| 9you        | shanghai |        4 | 9you        |
| 9you        | shanghai |        5 | 9you        |
| tx          | hangzhou |        6 | tx          |
| NULL        | NULL     |        7 | NULL        |
+-------------+----------+----------+-------------+
```

`FULL OUTER JOIN`把左右表都作为保留表，得到的结果为：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| 163         | hangzhou |        1 | 163         |
| 163         | hangzhou |        2 | 163         |
| 9you        | shanghai |        3 | 9you        |
| 9you        | shanghai |        4 | 9you        |
| 9you        | shanghai |        5 | 9you        |
| tx          | hangzhou |        6 | tx          |
| baidu       | hangzhou |     NULL | NULL        |
| NULL        | NULL     |        7 | NULL        |
+-------------+----------+----------+-------------+
```

添加外部行的工作就是在VT2表的基础上添加保留表中被过滤条件过滤掉的数据，非保留表中的数据被赋予NULL值，最后生成虚拟表VT3。

由于我在准备的测试SQL查询逻辑语句中使用的是`LEFT JOIN`，过滤掉了以下这条数据：

```
| baidu       | hangzhou |     NULL | NULL        |
```

现在就把这条数据添加到VT2表中，得到的VT3表如下：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| 163         | hangzhou |        1 | 163         |
| 163         | hangzhou |        2 | 163         |
| 9you        | shanghai |        3 | 9you        |
| 9you        | shanghai |        4 | 9you        |
| 9you        | shanghai |        5 | 9you        |
| tx          | hangzhou |        6 | tx          |
| baidu       | hangzhou |     NULL | NULL        |
+-------------+----------+----------+-------------+
```

接下来的操作都会在该VT3表上进行。

# # 执行WHERE过滤

对添加外部行得到的VT3进行WHERE过滤，只有符合<where_condition>的记录才会输出到虚拟表VT4中。当我们执行`WHERE a.city = 'hangzhou'`的时候，就会得到以下内容，并存在虚拟表VT4中：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| 163         | hangzhou |        1 | 163         |
| 163         | hangzhou |        2 | 163         |
| tx          | hangzhou |        6 | tx          |
| baidu       | hangzhou |     NULL | NULL        |
+-------------+----------+----------+-------------+
```

但是在使用WHERE子句时，需要注意以下两点：

1. 由于数据还没有分组，因此现在还不能在WHERE过滤器中使用`where_condition=MIN(col)`这类对分组统计的过滤；
2. 由于还没有进行列的选取操作，因此在SELECT中使用列的别名也是不被允许的，如：`SELECT city as c FROM t WHERE c='shanghai';`是不允许出现的。

# # 执行GROUP BY分组

`GROU BY`子句主要是对使用`WHERE`子句得到的虚拟表进行分组操作。我们执行测试语句中的`GROUP BY a.customer_id`，就会得到以下内容：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| 163         | hangzhou |        1 | 163         |
| baidu       | hangzhou |     NULL | NULL        |
| tx          | hangzhou |        6 | tx          |
+-------------+----------+----------+-------------+
```

得到的内容会存入虚拟表VT5中，此时，我们就得到了一个VT5虚拟表，接下来的操作都会在该表上完成。

# # 执行HAVING过滤

`HAVING`子句主要和`GROUP BY`子句配合使用，对分组得到的VT5虚拟表进行条件过滤。当我执行测试语句中的`HAVING count(b.order_id) < 2`时，将得到以下内容：

```
+-------------+----------+----------+-------------+
| customer_id | city     | order_id | customer_id |
+-------------+----------+----------+-------------+
| baidu       | hangzhou |     NULL | NULL        |
| tx          | hangzhou |        6 | tx          |
+-------------+----------+----------+-------------+
```

这就是虚拟表VT6。

# # SELECT列表

现在才会执行到`SELECT`子句，不要以为`SELECT`子句被写在第一行，就是第一个被执行的。

我们执行测试语句中的`SELECT a.customer_id, COUNT(b.order_id) as total_orders`，从虚拟表VT6中选择出我们需要的内容。我们将得到以下内容：

```
+-------------+--------------+
| customer_id | total_orders |
+-------------+--------------+
| baidu       |            0 |
| tx          |            1 |
+-------------+--------------+
```

不，还没有完，这只是虚拟表VT7。

# # 执行DISTINCT子句

如果在查询中指定了`DISTINCT`子句，则会创建一张内存临时表（如果内存放不下，就需要存放在硬盘了）。这张临时表的表结构和上一步产生的虚拟表VT7是一样的，不同的是对进行DISTINCT操作的列增加了一个唯一索引，以此来除重复数据。

由于我的测试SQL语句中并没有使用DISTINCT，所以，在该查询中，这一步不会生成一个虚拟表。

# # 执行ORDER BY子句

对虚拟表中的内容按照指定的列进行排序，然后返回一个新的虚拟表，我们执行测试SQL语句中的`ORDER BY total_orders DESC`，就会得到以下内容：

```
+-------------+--------------+
| customer_id | total_orders |
+-------------+--------------+
| tx          |            1 |
| baidu       |            0 |
+-------------+--------------+
```

可以看到这是对total_orders列进行降序排列的。上述结果会存储在VT8中。

# # 执行LIMIT子句

`LIMIT`子句从上一步得到的VT8虚拟表中选出从指定位置开始的指定行数据。对于没有应用ORDER BY的LIMIT子句，得到的结果同样是无序的，所以，很多时候，我们都会看到LIMIT子句会和ORDER BY子句一起使用。

MySQL数据库的LIMIT支持如下形式的选择：

```
LIMIT n, m
```

表示从第n条记录开始选择m条记录。而很多开发人员喜欢使用该语句来解决分页问题。对于小数据，使用LIMIT子句没有任何问题，当数据量非常大的时候，使用`LIMIT n, m`是非常低效的。因为LIMIT的机制是每次都是从头开始扫描，如果需要从第60万行开始，读取3条数据，就需要先扫描定位到60万行，然后再进行读取，而扫描的过程是一个非常低效的过程。所以，对于大数据处理时，是非常有必要在应用层建立一定的缓存机制（貌似现在的大数据处理，都有缓存哦）。

# 参考文档

[SQL逻辑查询语句执行顺序](http://www.jellythink.com/archives/924)

