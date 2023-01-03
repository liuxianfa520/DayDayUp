创建文件：

`run.vbs`

```vbscript
Dim WinScriptHost
Set WinScriptHost = CreateObject("WScript.Shell")
WinScriptHost.Run Chr(34) & "run.bat" & Chr(34), 0
Set WinScriptHost = Nothing
```

修改文本中的 run.bat 内容，改成自己的bat命令文件即可。