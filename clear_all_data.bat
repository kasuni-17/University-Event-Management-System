@echo off
set UPLOADS_DIR=c:\Users\ASUS\Desktop\New WEB\UniEvent\uploads
set SQL_FILE=c:\Users\ASUS\Desktop\New WEB\UniEvent\clear_data.sql

echo Cleaning uploads directory: %UPLOADS_DIR%...
del /F /Q "%UPLOADS_DIR%\*.*"
echo Uploads cleaned.

echo Executing SQL cleanup using %SQL_FILE%...
mysql -u root -pYumeth2004 unievent_db < "%SQL_FILE%"
echo Database cleaned.

echo Final result:
dir "%UPLOADS_DIR%"

echo Done.
