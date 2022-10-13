#Include %A_LineFile%\..\JSON.ahk
 
json_str = %clipboard%
stringified := JSON.Dump(JSON.Load(json_str),, 4)

MsgBox, %stringified%

return