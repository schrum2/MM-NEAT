Set oShell = CreateObject("WScript.Shell")
oShell.AppActivate "Recording"
oShell.SendKeys "q"
oShell.SendKeys "exit"