





;; alt+小键盘1：打开选中的文件夹    主要配合WGestures 使用。
^!+]::
Send ^c
run %clipboard%
return

; Windows10子系统ubuntu如何重启
::ubts::net stop LxssManager
::ubtr::net stop LxssManager;net start LxssManager
::wbts::net stop LxssManager
::wbtr::net stop LxssManager;net start LxssManager

::find::find / -name "*xxxx*"

::mvn i::mvn clean install -Dfile.encoding=UTF-8 -DskipTests=true -s "D:\Software\Apache-maven-3.3.9\conf\sbc_settings.xml" -am -pl -U{left}{left}{left}
::mvn d::mvn clean deploy  -Dfile.encoding=UTF-8 -DskipTests=true -s "D:\Software\Apache-maven-3.3.9\conf\sbc_settings.xml" -am -pl -U{left}{left}{left}

; 安装arthas
::install arthas::curl -O https`://arthas.aliyun.com/arthas-boot.jar;java -jar arthas-boot.jar
::install fullarthas::curl -O http://s3.jishu.idc/jjztxm-internal-read/arthas-full-lib-packaging-3.5.5-bin.zip
::arthas i::curl -O https`://arthas.aliyun.com/arthas-boot.jar;java -jar arthas-boot.jar
::farthas i::curl -O http://s3.jishu.idc/jjztxm-internal-read/arthas-full-lib-packaging-3.5.5-bin.zip

::taa::tail -222f /viewlogs/mam{tab}
::tee::tail -222f /viewlogs/mam{tab} | grep ERROR -A10
::taa1::tailf /lain/logs/default/current
::tta::tail -222f catalina.log
::tte::tail -f /data/*/*/log/catalina.log | grep Exception -n10
::netstat::netstat -tunlp | grep 


; awk命令格式：awk '{print $2}'  数字从1开始
::awk::awk '{{}print ${}}'{left}{left}
::ll::ls -ltrha








; ::mu::mvn clean compile -Dfile.encoding=UTF-8 -DskipTests=true -s "C:\Program App\Apache-maven-3.3.9\conf\newbanker.xml" -U
; ::mtree::mvn dependency:tree -Dverbose -Dincludes=*`:`:*{left}{left}
; ::mud::mvn clean deploy -Dfile.encoding=UTF-8 -DskipTests=true -s "C:\Program App\Apache-maven-3.3.9\conf\newbanker.xml" -U -am -pl 






; 获取git所有分支
::ggab::git branch -r | grep -v '\->' | while read remote`; do git branch --track "`${{}remote{#}origin/{}}" "$remote"`; done 
::ggp::git fetch --all;git pull --all
::gitforce::git push origin HEAD --force
::gitf::git push origin HEAD --force
::gforce::git push origin HEAD --force


;; 下面的语句块只在git shell中生效
#IfWinActive ahk_exe mintty.exe
	;复制，粘贴
	^c::send ^{Insert}
	^v::send +{Insert}
return
#if






;; win+g 获取应用的ahk_class   并自定复制到剪切板
#g::
    WinGetClass, title,A
    clipboard=%title%
    tooltip,%title%
    sleep 1000
    tooltip,
return



;; ctrl+Shift+F 格式化json
#Include %A_LineFile%\..\JSON.ahk
#f::
	; 复制当前选中的文本
	send ^c
	
	; 格式化文本
	stringified := JSON.Dump(JSON.Load(clipboard),, 4)
  clipboard = %stringified%
	MsgBox, %stringified%
return




;   win + `
download_path=C:\Users\AnXiaole\Downloads
idea_path=C:\Program App\IntelliJ IDEA 2020.2.2\bin\idea64.exe
#`::
inputBox,command,命令行,百度搜索：baidu`n打开IDEA：idea 或 ii `n打开下载文件夹：dl或dd`n清除回收站：ee
if ErrorLevel                                                                            
  return  
else if (command=="baidu" || command=="百度")
	run http://www.baidu.com  ;;; 快速打开百度
else if (command=="c" || command=="d" || command=="z")
	run %command%:/
else if (command=="ee")
	FileRecycleEmpty
else if (command=="dl" || command=="dd")
	run C:\Users\AnXiaole\Downloads,,max
else 
	run %clipboard%
return




;;选中一个文件，按下CTRL+SHIT+C就可以拿到文件的全路径了
;;下面的语句块只在[文件夹管理器]中生效
#IfWinActive ahk_exe Explorer.EXE
	^+c::
		send ^c
		sleep,100
		clipboard=%clipboard%
		tooltip,%clipboard%
		sleep,500
		tooltip,
	return
return 




;; 下面的语句块只在chrome中生效
#IfWinActive ahk_class Chrome_WidgetWin_1
	!a::Send ^+{Tab}
	!s::Send ^{Tab}
return 
#if






;; 下面的语句块只在Xshell6中生效
#IfWinActive ahk_exe Xshell.exe
	!a::Send ^+{Tab}
	!s::Send ^{Tab}
	
	;复制，粘贴
	^c::send ^{Insert}
	^v::send +{Insert}
return 
#if



;; 下面的语句块只在Navicat中生效  mysql
#IfWinActive ahk_exe navicat.exe
	!a::Send ^+{Tab}
	!s::Send ^{Tab}
	; alt+y 改成 ctrl+shift+left
	!y::Send ^+{left}
	

	;; 映射成 方向键
	!l::send {right}
	!k::send {down}
	!i::send {up}
	!j::send {left}
	!;::send {end}
	
	
;mysql
::sf::select * from  {enter}where 1=1  {enter}limit 10;{up}{up}{End}{left}
::df::delete from  {left}
::uf::update  set {enter}
::lj::left join      on {left}{left}{left}{left}{left}
::lk:: like '%%'{left}{left}
::je::replace(json_extract(field_value,'$.'),'"',''){left}{left}{left}{left}{left}{left}{left}{left}{left}{left}{left}{left}{left}{left}


return 
#if



;; 下面的语句块只在Typora中生效
#IfWinActive ahk_exe Typora.exe
	;; 映射成 方向键
	!l::send {right}
	!k::send {down}
	!i::send {up}
	!j::send {left}
	!;::send {end}
	; alt+y 改成 ctrl+shift+left
	!y::Send ^+{left}
return 
#if





;; 下面的语句块只在idea中生效
#IfWinActive ahk_exe idea64.exe
	;; ctrl + w 映射成ctrl+F4
	^w::send ^{F4}

	;; 映射成 方向键
	;!l::send {right}
return 






;; 下面的语句块只在charles中生效
#IfWinActive ahk_exe charles.exe
	;; ctrl+c 
	;^c::
	;	Send ^c
	;	NewStr := RegExReplace(clipboard, "URL	https://(.*)/http/(.*)", "/$2") ; 获取 /saas/repay/search.json
	;	NewStr := StrReplace(NewStr, "POST /http/(.*) HTTP/1.1", "/$1") ;把 POST /http/saas/common/logList.json HTTP/1.1   改成：/common/logList.json
	;	NewStr := StrReplace(NewStr, "api?_=", "") ;把 api?_=wbs.exhibitioncenter.get  的 api?_= 去掉
	;	NewStr := RegExReplace(NewStr, "(.*)	(.*)", "$2") ;token	315b9d235-3647-4b70-bacb-e4287d727baf  去掉前面的
	;	NewStr := RegExReplace(NewStr, """(.*)"": ""(.*)"",?", "$2") ;"name": "webapi.index.indexStatistics",
	;	clipboard = %NewStr%    ;把url保存到剪切板
	;	
	;	; send #1 ; 使用win+1 打开idea
	;	return
		

return










;; 【官方文档：http://ahkcn.sourceforge.net/docs/Hotkeys.htm】
;; 【https://blog.csdn.net/u013332124/article/details/80680038】
;;   符号               说明
;;   #	                window键
::awin::{#}
::awindow::{#}
;;   !	                alt键
::aalt::{!}
;;   ^	                ctrl键
::actrl::{^}
;;   +	                shift键
::ashift::{+}
;;   <	                有两个相同键时,表示左边那个键.比如alt有左右两个键,
;;   >	                表示相同键中右边的那个键
;;   LButton	        鼠标左键
;;   RButton	        鼠标右键
;;   MButton	        鼠标中间键
;;   WheelDown	        滚轮向下
;;   WheelUp	        滚轮向上
;;   Backspace	        退格键
;;   CapsLock	        大小写切换键
;;   Escape	        退出键