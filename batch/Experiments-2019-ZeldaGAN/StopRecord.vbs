Set oShell = CreateObject("WScript.Shell")
oShell.AppActivate "Recording"
WScript.Sleep 3000
oShell.SendKeys "q"
oShell.SendKeys "exit"