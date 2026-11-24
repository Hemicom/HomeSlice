HomeSlice is a household management application Created for COCS 412(software engineering). This application is currently at its
beginning stages and has yet to scratch the surface of its potential. 

The Application is made to help roommates build communication and organization by planning chores and finances. For now these will be 
functions of the application but there are features yet to come such as calendar and event functions. 

Current features of the application include:
Login page, dashboard: providing overview of expenses and tasks, Chore management: allowing the MOD to assign chores and 
expense management: which allows for MODS to assign expenses needed to be made at a certain date. 

Data for the expenses and chores is stored within SpringBoot and MYSQL. 

Application Limitations: Currently, we have not added the ability to add users. We have default users which will be seen when the 
application is ran. In addition, the application is currently set to run locally and not as a public website. 

Technologies used:
SpringBoot & Spring Secuirty
MYSQL
Java
HTML, CSS and Javascript

To run this program you must first:
Install: Java Development Kit (JDK) 21: MySQL Server 8.x (start the server before launching HomeSlice) and 
A terminal or an IDE such as IntelliJ IDEA

Once everything is installed, you can clone the code with this in your intelliJ terminal:
git clone https://github.com/Hemicom/HomeSlice.git
cd HomeSlice

You will then need to start MYSQL by:
Start the local MySQL server.
Sign in to MySQL using an administrator account (for example, mysql -u root -p).
Run the following SQL, replacing PASSWORD with your own password:

CREATE DATABASE IF NOT EXISTS homeslice;
CREATE USER IF NOT EXISTS 'homeslice_app'@'localhost'
IDENTIFIED BY 'CHOOSE_A_STRONG_LOCAL_PASSWORD';
GRANT ALL PRIVILEGES ON homeslice.* TO 'homeslice_app'@'localhost';

You can then run the app with: 

1. Open the HomeSlice project folder in IntelliJ.
2. Select JDK 21 for the project.
3. Open Run - Edit Configurations and configure HomesliceApplication with an environment variable
   named DB_PASSWORD, set to the password for homeslice_app.
4. Confirm MySQL is running.
5. Run HomesliceApplication.java.
6. Wait for the console message indicating that the application has started.


TroubleShooting: 
Login fails: Check the current Spring Boot startup console for the password; a restart may change it.
MySQL “Access denied”: Verify the homeslice_app username, password, and the DB_PASSWORD environment variable.
Database connection errors: Check that the MySQL server is running on localhost:3306 and that the homeslice database exists.
