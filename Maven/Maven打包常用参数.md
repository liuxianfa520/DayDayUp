# 常用命令

> -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -DskipTests=true
>
> 跳过javaDoc             跳过源码包             跳过测试用例执行

在idea中可以如下设置：

![img](https://oscimg.oschina.net/oscnet/up-d178ff7e941227693feac9a6607c38d9a8d.png)

 

## 常用参数解释

**mvn clean deploy -Dfile.encoding=UTF-8 -DskipTests=true -s "C:\Program App\Apache-maven-3.3.9\conf\settings.xml" -U -am -pl cc-admin**

- **-pl 指定打包项目** *cc-admin 是项目模块名称*
- **-am (**--also-make**) 如果项目依赖了其他项目，则也打包依赖的项目。**
- **-amd (**-also-make-dependents**) 同时构建所列模块的子模块**
- **-U 强制更新依赖。**
  - -U,--update-snapshots  Forces a check for missing releases and updated snapshots on remote repositories
    意思是：强制刷新本地仓库**不存在release版**和**所有的snapshots版本**。
    对于release版本，本地已经存在，则不会重复下载
    对于snapshots版本，不管本地是否存在，都会强制刷新，但是刷新并不意味着把jar重新下载一遍。
    只下载几个比较小的文件，通过这几个小文件确定本地和远程仓库的版本是否一致，再决定是否下载
- **-s 指定settings.xml配置文件。**



## 命令使用案例

```
mvn clean
[INFO] Reactor Build Order:
[INFO]
[INFO] configuration-center
[INFO] cc-common
[INFO] cc-conf-core
[INFO] cc-rely
[INFO] cc-admin
[INFO] cc-api
[INFO] cc-generator
[INFO] cc-conf-samples
[INFO] cc-conf-sample-springboot
[INFO] cc-conf-sample-springboot-use-propertiesfile



mvn clean -pl cc-admin
[INFO] Building cc-admin 0.0.1-SNAPSHOT



mvn clean -pl cc-admin -am
[INFO] Reactor Build Order:
[INFO]
[INFO] configuration-center
[INFO] cc-common
[INFO] cc-conf-core
[INFO] cc-rely
[INFO] cc-admin



mvn clean -pl cc-admin -amd
[INFO] Building cc-admin 0.0.1-SNAPSHOT



mvn clean -pl cc-conf-samples -amd
[INFO] Reactor Build Order:
[INFO]
[INFO] cc-conf-samples
[INFO] cc-conf-sample-springboot
[INFO] cc-conf-sample-springboot-use-propertiesfile






mvn clean -pl configuration-center -amd
[ERROR] [ERROR] Could not find the selected project in the reactor: configuration-center @
[ERROR] Could not find the selected project in the reactor: configuration-center -> [Help 1]
org.apache.maven.MavenExecutionException: Could not find the selected project in the reactor: configuration-center
        at org.apache.maven.graph.DefaultGraphBuilder.trimSelectedProjects(DefaultGraphBuilder.java:182)
        at org.apache.maven.graph.DefaultGraphBuilder.dependencyGraph(DefaultGraphBuilder.java:115)
        at org.apache.maven.graph.DefaultGraphBuilder.build(DefaultGraphBuilder.java:92)
        at org.apache.maven.DefaultMaven.buildGraph(DefaultMaven.java:491)
        at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:219)
        at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:193)
        at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:106)
        at org.apache.maven.cli.MavenCli.execute(MavenCli.java:863)
        at org.apache.maven.cli.MavenCli.doMain(MavenCli.java:288)
        at org.apache.maven.cli.MavenCli.main(MavenCli.java:199)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.codehaus.plexus.classworlds.launcher.Launcher.launchEnhanced(Launcher.java:289)
        at org.codehaus.plexus.classworlds.launcher.Launcher.launch(Launcher.java:229)
        at org.codehaus.plexus.classworlds.launcher.Launcher.mainWithExitCode(Launcher.java:415)
        at org.codehaus.plexus.classworlds.launcher.Launcher.main(Launcher.java:356)
```

**-rf 选项可以在完整的反应堆构建顺序基础上，指定从哪个模块开始构建。**

```
mvn clean -rf account-email ，由于~email位于第三，它之后有~persist，因此会得到如下输出模块：~email和~persist。
```

 

# 跳过deploy

**[maven deploy 时，通常需要忽略生成war的model，简单调整一下配置即可：](https://www.oschina.net/action/GoToLink?url=http%3A%2F%2Fwww.cnblogs.com%2Fjessezeng%2Fp%2F7134488.html)**

```xml
<plugins>
            <!-- deploy 时忽略此model-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <version>2.8.2</version>
                <configuration><skip>true</skip></configuration>
            </plugin>
</plugins>
```

# 源码打包插件

```xml
    <build>
        <plugins>
            <!-- 源码打包插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals><goal>jar</goal></goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

 







原文地址：https://my.oschina.net/anxiaole/blog/2873064