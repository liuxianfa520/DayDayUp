# [多租户Multi-Tenancy](http://niyanchun.com/multitenant-database-design-in-saas.html)

![img](https://img2018.cnblogs.com/blog/1303876/201906/1303876-20190621114239354-1621489760.png)

多租户（Multi-Tenancy）是SaaS中一个基础功能，本文介绍多租户下的数据库设计。

## 多租户设计

因为数据库分了database、schema、table这三个层次（尽管并非所有数据库都实现了），所以多租户也有了三种比较常用的设计：

1. **database-based multitenancy**：也称per-database-per-tenant，即一个租户一个数据库实例。
2. **schema-based multitenancy**：也称per-schema-per-tenant，即一个租户一个schema，但都共享同一个数据库实例。
3. **table-based multitenancy**：也称partitioned (discriminator) approach，即所有租户都使用一个表，然后通过在所有表中增加一个字段（通常就是租户id）来区分不同租户。数据库实例和表都是共享的。

审视一下三种设计方案，从1到3隔离程度越来越低，共享程度越来越高。我们从以下一些维度对比一下它们各自的优劣（注意：**对比主要是从数据库角度看的，而不是整个SaaS**）：

- **可扩展性**：隔离度越高，扩展性越差。数据库实例在数据库中是一个比较重的资源，虽然RDBMS中一般没有对database的个数做限制，但一个数据库服务器上面创建成千上万个数据库实例的场景应该是很少见的吧。所以从1到3，扩展性依次变差。
- **隔离性**：主要是数据的隔离、负载的隔离。这个很明显，隔离性1最好，3最差，2适中。
- **成本/资源利用率**：这里的成本主要指数据库的成本，或者说硬件的资源利用率。隔离程度越高，利用率越差。比如很多业务其实都有业务高峰和低峰，如果能把高峰不在同一时间段的业务部署在一起，自然是能够提升资源的利用率。
- **开发复杂度**：主要体现在查询、过滤、database/schema/table切换等。1和2适中，3难度高一些。
- **运维复杂度**：性能监控、管理；database/schema/table的管理；租户数据恢复；容灾等。扩展其实也算运维的一部分，第一个已经讨论过了，这里就不包含扩展了。从监控、管理、租户数据恢复、容灾等考虑，隔离度越高，越简单。
- **可定制性**：根据不同租户的需求进行定制的难度，这个自然也是隔离度越高，定制化越好做。

实际中如何选择呢？这个要根据实际业务场景和各个方案的优劣进行选择了，没有完美方案，只有更适合你的方案。而且比如你选择了MySQL，那方案2就不存在了，因为MySQL中没有区分Database和Schema。还有这里只是比较学术的划分了一下设计方案，实际中一些大型SaaS会实现更复杂、灵活的多租户数据库方案，以平衡各个方案的优劣，这部分推荐一篇文章：[Multi-tenant SaaS database tenancy patterns](https://docs.microsoft.com/en-us/azure/sql-database/saas-tenancy-app-design-patterns)。

原文链接：http://niyanchun.com/multitenant-database-design-in-saas.html





# [SpringBoot项目使用动态切换数据源实现多租户SaaS方案](https://blog.csdn.net/qq_36521507/article/details/103452961)

这种实现方案的问题

- 只能在程序启动的时候，把所有租户的数据源都初始化。



# Nb多租户数据库切换方案





# 思考

- 如果是让我来从零设计一个方案。我会如何思考？如何在网上查找资料？
- 我的思路估计也会使用 **AbstractRoutingDataSource** 来实现。

  - 经过验证，**AbstractRoutingDataSource** 无法实现。





# 参考文章

[Multi-tenant SaaS database tenancy patterns](https://docs.microsoft.com/en-us/azure/sql-database/saas-tenancy-app-design-patterns)

[SaaS模式与技术架构](http://www.360doc.com/content/20/0227/16/36367108_895232258.shtml)