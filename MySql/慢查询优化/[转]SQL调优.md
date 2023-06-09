> 本文转载自：[SQL调优](https://www.cnblogs.com/Qian123/p/5666569.html)
>
> 仅作为个人学习使用。



**阅读目录**

[TOC]



## # 问题的提出

　在应用系统开发初期，由于开发数据库数据比较少，对于查询SQL语句，复杂视图的的编写等体会不出SQL语句各种写法的性能优劣，但是如果将应用 系统提交实际应用后，随着数据库中数据的增加，系统的响应速度就成为目前系统需要解决的最主要的问题之一。系统优化中一个很重要的方面就是SQL语句的优化。对于海量数据，劣质SQL语句和优质SQL语句之间的速度差别可以达到上百倍，可见对于一个系统不是简单地能实现其功能就可，而是要写出高质量的 SQL语句，提高系统的可用性。

　　在多数情况下，Oracle使用索引来更快地遍历表，优化器主要根据定义的索引来提高性能。但是，如果在SQL语句的where子句中写的 SQL代码不合理，就会造成优化器删去索引而使用全表扫描，一般就这种SQL语句就是所谓的劣质SQL语句。在编写SQL语句时我们应清楚优化器根据何种原则来删除索引，这有助于写出高性能的SQL语句。

 

随着软件技术的不断发展，系统性能越来越重要。系统性能主要用：**系统响应时间**和**并发性**来衡量。

 

造成SQL语句性能不佳大致有两个原因：

l 开发人员只关注查询结果的正确性，忽视查询语句的效率。

l 开发人员只关注SQL语句本身的效率，对SQL语句执行原理、影响SQL执行效率的主要因素不清楚。

 

\* 前者可以通过深入学习SQL语法及各种SQL调优技巧进行解决。

  SQL调优是一个系统工程，熟悉SQL语法、掌握各种内嵌函数、分析函数的用法只是编写高效SQL的必要条件。

\* 后者从分析SQL语句执行原理入手，指出SQL调优应在优化SQL解析和优化CBO上。

## # SQL语句调优

80%的数据库性能问题都是由于糟糕的SQL语句造成的。

### ##  SQL语句优化的过程:

l 定位有问题的语句

l 检查执行计划

l 检查执行过程中优化器的统计信息

l 分析相关表的记录数、索引情况

l 改写SQL语句、使用HINT、调整索引、表分析

l 有些SQL语句不具备优化的可能，需要优化处理方式

l 达到最佳执行计划

### ## 什么是好的SQL语句？

l 尽量简单，模块化

l 易读、易维护

l 节省资源、内存、CPU

l 扫描的数据块要少

l 少排序

l 不造成死锁

### ## 首先要搞明白什么叫执行计划？

执行计划是数据库根据SQL语句和相关表的统计信息作出的一个查询方案，这个方案是由查询优化器自动分析产生的，比如一条SQL语句如果用来从一个 10万条记录的表中查1条记录，那查询优化器会选择“索引查找”方式，如果该表进行了归档，当前只剩下5000条记录了，那查询优化器就会改变方案，采用 “全表扫描”方式。

可见，执行计划并不是固定的，它是“个性化的”。产生一个正确的“执行计划”有两点很重要：

(1) SQL语句是否清晰地告诉查询优化器它想干什么？

(2)查询优化器得到的数据库统计信息是否是最新的、正确的？

## # 什么是索引？

SQL索引有两种，聚集索引和非聚集索引，索引主要目的是提高了SQL Server系统的性能，加快数据的查询速度与减少系统的响应时间

 

下面举两个简单的例子：

图书馆的例子：一个图书馆那么多书，怎么管理呢？建立一个字母开头的目录，例如：a开头的书，在第一排，b开头的在第二排，这样在找什么书就好说了，这个就是一个聚集索引，可是很多人借书找某某作者的，不知道书名怎么办？图书管理员在写一个目录，某某作者的书分别在第几排，第几排，这就是一个非聚集索引

字典的例子：字典前面的目录，可以按照拼音和部首去查询，我们想查询一个字，只需要根据拼音或者部首去查询，就可以快速的定位到这个汉字了，这个就是索引的好处，拼音查询法就是聚集索引，部首查询就是一个非聚集索引.

看了上面的例子，下面的一句话大家就很容易理解了：聚集索引存储记录是物理上连续存在，而非聚集索引是逻辑上的连续，物理存储并不连续。就像字段，聚集索引是连续的，a后面肯定是b，非聚集索引就不连续了，就像图书馆的某个作者的书，有可能在第1个货架上和第10个货架上。还有一个小知识点就是：聚集索引一个表只能有一个，而非聚集索引一个表可以存在多个。

索引相关介绍：

[软件开发人员真的了解SQL索引吗(聚集索引)](http://www.cnblogs.com/ASPNET2008/archive/2010/12/18/1910147.html)

[软件开发人员真的了解SQL索引吗(索引原理)](http://www.cnblogs.com/ASPNET2008/archive/2010/12/18/1910183.html)

[软件开发人员真的了解SQL索引吗(索引使用原则)](http://www.cnblogs.com/ASPNET2008/archive/2010/12/19/1910218.html)

### ## 索引的存储机制

　首先，无索引的表，查询时，是按照顺序存续的方法扫描每个记录来查找符合条件的记录，这样效率十分低下,举个例子，如果我们将字典的汉字随即打乱，没有前面的按照拼音或者部首查询，那么我们想找一个字，按照顺序的方式去一页页的找，这样效率有多底，大家可以想象。

聚集索引和非聚集索引的根本区别是表记录的排列顺序和与索引的排列顺序是否一致，其实理解起来非常简单，还是举字典的例子：如果按照拼音查询，那么都是从a-z的，是具有连续性的，a后面就是b，b后面就是c，聚集索引就是这样的，他是和表的物理排列顺序是一样的，例如有id为聚集索引，那么1后面肯定是2,2后面肯定是3，所以说这样的搜索顺序的就是聚集索引。非聚集索引就和按照部首查询是一样是，可能按照偏房查询的时候，根据偏旁‘弓’字旁，索引出两个汉字，张和弘，但是这两个其实一个在100页，一个在1000页，（这里只是举个例子），他们的索引顺序和数据库表的排列顺序是不一样的，这个样的就是非聚集索引。

原理明白了，那他们是怎么存储的呢？在这里简单的说一下，聚集索引就是在数据库被开辟一个物理空间存放他的排列的值，例如1-100，所以当插入数据时，他会重新排列整个整个物理空间，而非聚集索引其实可以看作是一个含有聚集索引的表，他只仅包含原表中非聚集索引的列和指向实际物理表的指针。他只记录一个指针，其实就有点和堆栈差不多的感觉了。

### ## 什么情况下设置索引

| 动作描述                   | 使用聚集索引 | 使用非聚集索引 |
| -------------------------- | ------------ | -------------- |
| 外键列                     | 应           | 应             |
| 主键列                     | 应           | 应             |
| 列经常被分组排序(order by) | 应           | 应             |
| 返回某范围内的数据         | 应           | 不应           |
| 小数目的不同值             | 应           | 不应           |
| 大数目的不同值             | 不应         | 应             |
| 频繁更新的列               | 不应         | 应             |
| 频繁修改索引列             | 不应         | 应             |
| 一个或极少不同值           | 不应         | 不应           |

**建立索引的原则**：

\1) 定义主键的数据列一定要建立索引。

\2) 定义有外键的数据列一定要建立索引。

\3) 对于经常查询的数据列最好建立索引。

\4) 对于需要在指定范围内的快速或频繁查询的数据列;

\5) 经常用在WHERE子句中的数据列。

\6) 经常出现在关键字order by、group by、distinct后面的字段，建立索引。如果建立的是复合索引，索引的字段顺序要和这些关键字后面的字段顺序一致，否则索引不会被使用。

\7) 对于那些查询中很少涉及的列，重复值比较多的列不要建立索引。

\8) 对于定义为text、image和bit的数据类型的列不要建立索引。

\9) 对于经常存取的列避免建立索引

\9) 限制表上的索引数目。对一个存在大量更新操作的表，所建索引的数目一般不要超过3个，最多不要超过5个。索引虽说提高了访问速度，但太多索引会影响数据的更新操作。

\10) 对复合索引，按照字段在查询条件中出现的频度建立索引。在复合索引中，记录首先按照第一个字段排序。对于在第一个字段上取值相同的记录，系统再按照第二个字段的取值排序，以此类推。因此只有复合索引的第一个字段出现在查询条件中，该索引才可能被使用,因此将应用频度高的字段，放置在复合索引的前面，会使系统最大可能地使用此索引，发挥索引的作用。

## # SQL语句编写注意问题

> 数据库系统按着从左到右的顺序来解析一个系列由 AND 连接的表达式，但是 Oracle 却是个例外，它是从右向左地解析表达式。可以利用数据库系统的这一特性，来将概率小的表达示放在前面，或者是如果两个表达式可能性相同，那么可将相对不复杂的表达式放在前面。这样做的话，如果第一个表达式为假的话，那么数据库系统就不必再费力去解析第二个表达式了。

1.对查询进行优化，要尽量**避免全表扫描，首先应考虑在 where 及 order by 涉及的列上建立索引**。

2.应尽量**避免在 where 子句中对字段进行 null 值判断**，否则将导致引擎放弃使用索引而进行全表扫描，如：

```
select id from t where num is null
```

最好不要给数据库留NULL，尽可能的使用 NOT NULL填充数据库.

备注、描述、评论之类的可以设置为 NULL，其他的，最好不要使用NULL。

不要以为 NULL 不需要空间，比如：char(100) 型，在字段建立时，空间就固定了， 不管是否插入值（NULL也包含在内），都是占用 100个字符的空间的，如果是varchar这样的变长字段， null 不占用空间。

可以在num上设置默认值0，确保表中num列没有null值，然后这样查询：

```
select id from t where num = 0
```

3.应尽量**避免在 where 子句中使用 != 或 <> 操作符**，否则将引擎放弃使用索引而进行全表扫描。
4.应尽量**避免在 where 子句中使用 or 来连接条件**，如果一个字段有索引，一个字段没有索引，将导致引擎放弃使用索引而进行全表扫描，如：

```
select id from t where num=10 or Name = 'admin'
```

可以这样查询：

```
select id from t where num = 10
union all
select id from t where Name = 'admin'
```

5.**in 和 not in 也要慎用**，否则会导致全表扫描，如：

```
select id from t where num in(1,2,3)
```

对于**连续的数值，能用 between** 就不要用 in 了：

```
select id from t where num between 1 and 3
```

很多时候**用 exists 代替 in** 是一个好的选择：

```
select num from a where num in(select num from b)
```

用下面的语句替换：

```
select num from a where exists(select 1 from b where num=a.num)
```

6.下面的查询也将导致全表扫描：

```
select id from t where name like ‘%abc%’
```

若要提高效率，可以考虑全文检索。
7.如果在 where 子句中使用参数，也会导致全表扫描。因为SQL只有在运行时才会解析局部变量，但优化程序不能将访问计划的选择推迟到运行时；它必须在编译时进行选择。然而，如果在编译时建立访问计划，变量的值还是未知的，因而无法作为索引选择的输入项。如下面语句将进行全表扫描：

```
select id from t where num = @num
```

可以改为强制查询使用索引：

```
select id from t with(index(索引名)) where num = @num
```

8.应尽量避免在 where 子句中对字段进行表达式操作，这将导致引擎放弃使用索引而进行全表扫描。如：

```
select id from t where num/2 = 100
```

应改为:

```
select id from t where num = 100*2
```

9.应尽量避免在where子句中对字段进行函数操作，这将导致引擎放弃使用索引而进行全表扫描。如：

```
select id from t where substring(name,1,3) = ’abc’       -–name以abc开头的id
select id from t where datediff(day,createdate,’2005-11-30′) = 0    -–‘2005-11-30’    --生成的id
```

应改为:

```
select id from t where name like 'abc%'
select id from t where createdate >= '2005-11-30' and createdate < '2005-12-1'
```

10.不要在 where 子句中的“=”左边进行函数、算术运算或其他表达式运算，否则系统将可能无法正确使用索引。
11.在使用索引字段作为条件时，如果该索引是复合索引，那么必须使用到该索引中的第一个字段作为条件时才能保证系统使用该索引，否则该索引将不会被使用，并且应尽可能的让字段顺序与索引顺序相一致。
12.不要写一些没有意义的查询，如需要生成一个空表结构：

```
select col1,col2 into #t from t where 1=0
```

这类代码不会返回任何结果集，但是会消耗系统资源的，应改成这样：
create table #t(…)

13.Update 语句，如果只更改1、2个字段，不要Update全部字段，否则频繁调用会引起明显的性能消耗，同时带来大量日志。
14.对于多张大数据量（这里几百条就算大了）的表JOIN，要先分页再JOIN，否则逻辑读会很高，性能很差。
15.select count(*) from table；这样不带任何条件的count会引起全表扫描，并且没有任何业务意义，是一定要杜绝的。

16.索引并不是越多越好，索引固然可以提高相应的 select 的效率，但同时也降低了 insert 及 update 的效率，因为 insert 或 update 时有可能会重建索引，所以怎样建索引需要慎重考虑，视具体情况而定。一个表的索引数最好不要超过6个，若太多则应考虑一些不常使用到的列上建的索引是否有 必要。
17.应尽可能的避免更新 clustered 索引数据列，因为 clustered 索引数据列的顺序就是表记录的物理存储顺序，一旦该列值改变将导致整个表记录的顺序的调整，会耗费相当大的资源。若应用系统需要频繁更新 clustered 索引数据列，那么需要考虑是否应将该索引建为 clustered 索引。
18.尽量使用数字型字段，若只含数值信息的字段尽量不要设计为字符型，这会降低查询和连接的性能，并会增加存储开销。这是因为引擎在处理查询和连 接时会逐个比较字符串中每一个字符，而对于数字型而言只需要比较一次就够了。
19.**尽可能的使用 varchar/nvarchar 代替 char/nchar** ，因为首先变长字段存储空间小，可以节省存储空间，其次对于查询来说，在一个相对较小的字段内搜索效率显然要高些。
20.任何地方都**不要使用 select \* from t ，用具体的字段列表代替“\*”**，不要返回用不到的任何字段。
21.尽量使用表变量来代替临时表。如果表变量包含大量数据，请注意索引非常有限（只有主键索引）。
\22. 避免频繁创建和删除临时表，以减少系统表资源的消耗。临时表并不是不可使用，适当地使用它们可以使某些例程更有效，例如，当需要重复引用大型表或常用表中的某个数据集时。但是，对于一次性事件， 最好使用导出表。
23.在新建临时表时，如果一次性插入数据量很大，那么可以使用 select into 代替 create table，避免造成大量 log ，以提高速度；如果数据量不大，为了缓和系统表的资源，应先create table，然后insert。
24.如果使用到了临时表，在存储过程的最后务必将所有的临时表显式删除，先 truncate table ，然后 drop table ，这样可以避免系统表的较长时间锁定。
25.尽量避免使用游标，因为游标的效率较差，如果游标操作的数据超过1万行，那么就应该考虑改写。
26.使用基于游标的方法或临时表方法之前，应先寻找基于集的解决方案来解决问题，基于集的方法通常更有效。
27.与临时表一样，游标并不是不可使用。对小型数据集使用 FAST_FORWARD 游标通常要优于其他逐行处理方法，尤其是在必须引用几个表才能获得所需的数据时。在结果集中包括“合计”的例程通常要比使用游标执行的速度快。如果开发时 间允许，基于游标的方法和基于集的方法都可以尝试一下，看哪一种方法的效果更好。
28.在所有的存储过程和触发器的开始处设置 SET NOCOUNT ON ，在结束时设置 SET NOCOUNT OFF 。无需在执行存储过程和触发器的每个语句后向客户端发送 DONE_IN_PROC 消息。
29.尽量避免大事务操作，提高系统并发能力。
30.尽量避免向客户端返回大数据量，若数据量过大，应该考虑相应需求是否合理。

 

**实际案例分析**：拆分大的 DELETE 或INSERT 语句，批量提交SQL语句
　　如果你需要在一个在线的网站上去执行一个大的 DELETE 或 INSERT 查询，你需要非常小心，要避免你的操作让你的整个网站停止相应。因为这两个操作是会锁表的，表一锁住了，别的操作都进不来了。
　　Apache 会有很多的子进程或线程。所以，其工作起来相当有效率，而我们的服务器也不希望有太多的子进程，线程和数据库链接，这是极大的占服务器资源的事情，尤其是内存。
　　如果你把你的表锁上一段时间，比如30秒钟，那么对于一个有很高访问量的站点来说，这30秒所积累的访问进程/线程，数据库链接，打开的文件数，可能不仅仅会让你的WEB服务崩溃，还可能会让你的整台服务器马上挂了。
　　所以，如果你有一个大的处理，你一定把其拆分，使用 LIMIT oracle(rownum),sqlserver(top)条件是一个好的方法。下面是一个mysql示例：

```
while(1){
 　　//每次只做1000条
　　 mysql_query(“delete from logs where log_date <= ’2012-11-01’ limit 1000”);
 　　if(mysql_affected_rows() == 0){　　 　　//删除完成，退出！
　　 　　break；
　　}
//每次暂停一段时间，释放表让其他进程/线程访问。
usleep(50000)
}
```

 SQL优化参考文档：http://www.jfox.info/SQL-you-hua

## # 针对专门操作符的调优

参考文档：[SQL 语句性能调优](http://www.ibm.com/developerworks/cn/data/library/techarticles/dm-1002limh/) 

### 与 (AND) 

数据库系统按着从左到右的顺序来解析一个系列由 AND 连接的表达式，但是 Oracle 却是个例外，它是从右向左地解析表达式。可以利用数据库系统的这一特性，来将概率小的表达示放在前面，或者是如果两个表达式可能性相同，那么可将相对不复杂的表达式放在前面。这样做的话，如果第一个表达式为假的话，那么数据库系统就不必再费力去解析第二个表达式了。例如，可以这样转换：

```
 ... WHERE column1 = 'A' AND column2 = 'B'
```

转换成：

```
 ... WHERE column2 = 'B' AND column1 = 'A'
```

这里假设 column2 = 'B'的概率较低，如果是 Oracle 数据库的话，只需将规则反过来用即可。

### 或 (OR)

和与 (AND) 操作符相反，在用或 (OR) 操作符写 SQL 语句时，就应该将概率大的表达示放在左面，因为如果第一个表达示为假的话，OR 操作符意味着需要进行下一个表达示的解析。

### 与 + 或

按照集合的展开法则，

```
 A AND (B OR C) 与 (A AND B) OR (A AND C) 是等价表达示。
```

假设有如表 3 所示的一张表，要执行一个 AND 操作符在前的表达示

```
 SELECT * FROM Table1 
 
 WHERE (column1 = 1 AND column2 = 'A') 
 
 OR (column1 = 1 AND column2 = 'B')
```

### 表 3. AND+OR 查询

| Row# | Colmun1 | Column2 |
| ---- | ------- | ------- |
| 1    | 3       | A       |
| 2    | 2       | B       |
| 3    | 1       | C       |

当数据库系统按照查询语进行搜索时，它按照下面的步骤执行：

- 索引查找 column1 = 1, 结果集 = {row 3}
- 索引查找 column2 = ‘ A ’ , 结果集 = {row1}
- AND 合并结果集，结果集 = {}
- 索引查找 column 1 = 1, 结果集 = {row 3}
- 索引查找 column 2 = ‘ B ’ , 结果集 = {row2}
- AND 合并结果集，结果集 = {}
- OR 合并结集，结果集 = {}

现在根据集合的展开法则，对上面的语句进行转换：

```
 SELECT * FROM Table1 
 WHERE column1 = 1 
 AND (column2 = 'A' OR column2 = 'B')
```

按照新的顺序进行查搜索时，它按照下面的步骤执行：

- 索引查找 column2 = ‘ A ’ , 结果集 = {row1}
- 索引查找 column 2 = ‘ B ’ , 结果集 = {row2}
- OR 合并结集，结果集 = {}
- 索引查找 column1 = 1, 结果集 = {row 3}
- AND 合并结果集，结果集 = {}

由此可见搜索次数少了一次。虽然一些数据库操作系统会自动的进行这样的转换，但是对于简单的查询来说，这样的转换还是有好处的。

### 非 (NOT)

让非 (NOT) 表达示转换成更易读的形式。简单的条件能通过将比较操作符进行反转来达到转换的目的，例如：

```
 ... WHERE NOT (column1 > 5)
```

转换成：

```
 ... WHERE column1 <= 5
```

比较复杂的情况，根据集合的摩根定理：

```
 NOT (A AND B) = (NOT A) OR (NOT B) 和 NOT (A OR B) = (NOT A) AND (NOT B)
```

根据这一定理，可以看出它可以至少二次的搜索有可能减少为一次。如下的查询条件：

```
 ... WHERE NOT (column1 > 5 OR column2 = 7)
```

可以转换成：

```
 ... WHERE column1 <= 5 

 AND column2 <> 7
```

但是，当转换成后的表达示中有不等操作符 <>，那么性能就会下降，毕竟，在一个值平均分布的集合中，不等的值的个数要远远大于相等的值的个数，正因为如此，一些数据库系统不会对非比较进行索引搜索，但是他们会为大于或小于进行索引搜索，所以可以将下面的查询进行如下转换：

```
 ... WHERE NOT (column1 = 0)
```

转换成：

```
 ... WHERE column <0 

 OR column > 0
```

### IN

很多人认为如下的两个查询条件没有什么差别，因为它们返回的结果集是相同的：

条件 1：

```
 ... WHERE column1 = 5 

 OR column1 = 6
```

条件 2：

```
 ... WHERE column1 IN (5, 6)
```

这样的想法并不完全正确，对于大多数的数据库操作系统来说，IN 要比 OR 执行的快。所以如果可以的话，要将 OR 换成 IN

当 IN 操作符，是一系列密集的整型数字时，最好是查找哪些值不符合条件，而不是查找哪些值符合条件，因此，如下的查询条件就应该进行如下的转换：

```
 ... WHERE column1 IN (1, 3, 4, 5)
```

转换成：

```
 ... WHERE column1 BETWEEN 1 AND 5 
 AND column1 <> 2
```

当一系列的离散的值转换成算数表达示时，也可获得同样的性能提高。

### UNION

在 SQL 中，两个表的 UNION 就是两个表中不重复的值的集合，即 UNION 操作符返返回的两个或多个查询结果中不重复行的集合。这是一个很好的合并数据的方法，但是这并不是最好的方法。

查询 1：

```
 SELECT * FROM Table1 

 WHERE column1 = 5 

 UNION 

 SELECT * FROM Table1 

 WHERE column2 = 5
```

查询 2：

```
 SELECT DISTINCT * FROM Table1 

 WHERE column1 = 5 

 OR column2 = 5
```

在上面的例子中，column1 和 column2 都没有索引。如果查询 2 总是比查询 1 执行的快的话，那么就可以建议总是将查询 1 转换成查询 2，但是有一种情况，这样做在一些数据库系统中可能会带来性能变差，这是由于两个优化缺陷所造成的。

第一个优化缺陷就是很多优化器只优化一个 SELECT 语句中一个 WHERE 语句，所以查询 1 的两个 SELECT 语句都被执行。首先优化器根据查询条件 column1 = 5 为真来查找所有符合条件的所有行，然后据查询条件 column2 = 5 为真来查找所有符合条件的所有行，即两次表扫描，因此，如果 column1 = 5 没有索引的话，查询 1 将需要 2 倍于查询 2 所需的时间。如果 column1 = 5 有索引的话，仍然需要二次扫描，但是只有在某些数据库系统存在一个不常见的优化缺陷却将第一个优化缺陷给弥补了。当一些优化器发现查询中存在 OR 操作符时，就不使用索引查询，所以在这种情况下，并且只有在这种情况下，UNION 才比 OR 性能更高。这种情况很少见，所以仍然建议大家当待查询的列没有索引时使用 OR 来代替 UNION。

