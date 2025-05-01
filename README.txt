To run, execute the main method of the Main class.

SQL SCHEMA:

CREATE DATABASE atm;
USE atm;

CREATE TABLE Accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    pin VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    balance DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL
);



Final Questions:
1. System Design drawing is in the submission
2. Class Diagrams are in the submission
3. My project uses a layered architecture. The three layers are:
    Presentation (Main),
    Business (Account, AccountService, TransactionServiceTest, AppModule),
    Database (Database, DatabaseService, IDatabase)
4. I set up test statements for all the classes with public methods. I used Jacoco for code coverage
and the latest coverage can be found in target/site/jacoco/index.html which can be opened with:
start target\site\jacoco\index.html
And my code coverage is 93%.
5. Pros and cons:
* Plain Old Computers
    Pros:
        - Easiest to use with almost no setup and overhead
        - Resources are designed for this use and you usually have full control over resources
        - All your data is in one environment so you don't need to waste time transferring resources
    Cons:
        - Poor portability because most likely people are using different machines
        - Can be hard to reproduce bugs, especially when collaborating
        - Not as scalable as other environments
        - Operating systems may be different, so code may not work as intended on all machines
* Virtual Machines
    Pros:
        - You can choose the operating system to ensure all programmers are in the same kind of environment
        - Scalable for production
        - Accurate testing since
        - Isolated environment, meaning fatal errors won't poison the base computer
    Cons:
        - Requires some setup
        - You don't have access to all the files on the base computer
        - VMs often run slower than the base computer
        - VMs may need a license and can be expensive
* Docker
    Pros:
        - Has a fast startup and low overhead
        - Is portable for all devices that support Docker
        - Allows for easy collaboration
        - Free and open
    Cons:
        - Native to Linux, and running on another OS uses a VM
        - Requires some setup
        - Can be confusing for beginners
6.
    - I set up EditorConfig with the file .editorconfig which has native support from Intellij
    - I used Checkstyle because I heard it is better for Java projects. I also fixed all the style complaints it had
    - I added Javadocs comments to every public method and class
    - I created the class documentation with Javadocs using: mvn javadoc:javadoc
    - I set up a build system using a powershell script. This is located at build.ps1 in the root folder
    -

