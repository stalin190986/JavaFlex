cls
set rutaApache=C:\Archivos de programa\Apache Software Foundation\Apache Tomcat 6.0.26\webapps\
erase "%rutaApache%EntradasProveedor.war"
deltree"%rutaApache%EntradasProveedor\"
copy "dist\EntradasProveedor.war" "%rutaApache%"

