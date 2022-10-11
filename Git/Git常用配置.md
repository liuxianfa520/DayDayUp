#  注意

我的电脑是windows所以下面的一些命令都是基于windows的。

比如findstr对应linux的grep命令

# Git全局配置和单个仓库的用户名邮箱配置

配置全局的用户名和邮箱

```
git config --global user.name "github’s Name"

git config --global user.email "github@xx.com"

git config --list --show-origin | findstr user
```

![img](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

如果你公司的项目是放在自建的gitlab上面, 如果你不进行配置用户名和邮箱的话, 则会使用全局的, 这个时候是错误的, 正确的做法是针对公司的项目, 在项目根目录下进行单独配置

```bash
# 设置git本地项目用户名、邮箱 —— 使用个人邮箱
git config user.name "LiuXianfa" && git config user.email "745024471@qq.com" && git config --list --show-origin | findstr user && git config credential.helper "store --file .git/.git-credentials" && git config --list --show-origin | findstr credential


# 设置git本地项目用户名、邮箱 —— 使用企业邮箱
git config user.name "LiuXianfa" && git config user.email "xianfaliu2@creditease.cn" && git config --list --show-origin | findstr user && git config credential.helper "store --file .git/.git-credentials" && git config --list --show-origin | findstr credential
```

![img](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

git config --list 查看项目配置+全局配置, 使用的时候会优先使用当前项目的配置

加上 *--show-origin* 参数可以展示出是全局配置还是项目配置

参考：https://blog.csdn.net/pintu274111451/article/details/79767970

## GIT 拉取所有远程分支

```
git clone https://github.com/tanguosheng/spring-framework.git
git branch -r | grep -v '\->' | while read remote; do git branch --track "${remote#origin/}" "$remote"; done
git fetch --all
git pull --all
```

![img](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

注：window系统cmd无法识别第一步中的命令，请使用git bash命令行

参考：https://www.freesion.com/article/89381170624/

# 推送所有本地分支到remote仓库

```
git remote add origin http://gitlab.xxx.com/xxx/xxx.git
git push -u origin --all
git push -u origin --tags
```

![img](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

# 设置git remote仓库地址

git remote -v # 查看远程仓库地址

![img](images/up-0c76bb92b59036cb695365dcf70f0e808d6.png)

其中origin对应的源为默认源。

```
git remote set-url origin http://gitlab.xxx.com/xxx/xxx.git  
```

![img](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)