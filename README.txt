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

