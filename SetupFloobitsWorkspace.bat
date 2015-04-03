@echo off
mkdir %CD%\MovingWorld\project_libraries
set GRADLE_USER_HOME=%CD%\MovingWorld\project_libraries
gradlew setupDecompWorkspace idea eclipse