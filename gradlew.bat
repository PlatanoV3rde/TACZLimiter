@echo off
set DIR=%~dp0
set JAVA_EXE=java

if defined JAVA_HOME set JAVA_EXE=%JAVA_HOME%\bin\java

"%JAVA_EXE%" %JAVA_OPTS% -classpath "%DIR%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
