@echo off
del /Q thrift\gen-js\*.js
del /Q thrift\gen-java\*.java
del /Q /S war\WEB-INF\appengine-generated\*.* 
del /Q /S war\WEB-INF\classes\*.class
del /Q /S war\gen-js 