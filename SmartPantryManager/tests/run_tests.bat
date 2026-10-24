@echo off
setlocal
set "TEST_CLASSES=%TEMP%\pantry-tests-%RANDOM%-%RANDOM%"
mkdir "%TEST_CLASSES%"
set "PKG=%~dp0..\app\src\main\java\com\example\smartpantry"
javac -d "%TEST_CLASSES%" "%PKG%\PantryItem.java" "%PKG%\Recipe.java" "%PKG%\IngredientRules.java" "%PKG%\RecipeMatcher.java" "%~dp0MatcherTests.java"
if errorlevel 1 goto failed
java -cp "%TEST_CLASSES%" com.example.smartpantry.MatcherTests
if errorlevel 1 goto failed
rmdir /s /q "%TEST_CLASSES%"
exit /b 0
:failed
rmdir /s /q "%TEST_CLASSES%"
exit /b 1
