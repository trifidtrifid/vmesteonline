@echo off
cd thrift
for %%f in (*.thrift) do (
	thrift --gen js %%f
	thrift --gen java %%f
)
if not exist ..\war\gen-js. mkdir ..\war\gen-js
for %%F IN (gen-js\*.js) do ( 
	type %%F | ..\repl.bat "if \(args instanceof " "if (args && args instanceof " >..\war\%%F
)
del /Q gen-js\*.js 
cd ..