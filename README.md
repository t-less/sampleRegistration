# sampleRegistration
Sample registration project with Spring Security, MySQL and Thymeleaf

# Build and Deploy the Project
```
mvn clean install
```
# Set up MySQL
```
mysql -u root -p 
> CREATE USER reguser@localhost IDENTIFIED BY 'reguser';
> GRANT ALL PRIVILEGES ON *.* TO reguser@localhost;
> FLUSH PRIVILEGES;
```
# Set up Email
You need to configure the email by providing your own username and password in application.properties

