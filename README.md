🏦 Bank Management System (Java Swing & MySQL)
A desktop-based banking application designed to manage customer accounts and track financial history using a modern Card Layout interface.

📄 System Description
This system provides a digital solution for bank tellers to handle daily operations. It bridges the gap between a user-friendly Java GUI and a persistent MySQL database.

Core Features
Account Management: Create and manage Savings and Current accounts.

Transaction Engine: Process real-time Deposits and Withdrawals with automated balance updates.

Audit Logging: Every transaction is logged with a unique ID, timestamp, and metadata.

Advanced Filtering: Filter history by Date, Account ID, or Transaction Type via a dynamic ComboBox interface.

Secure Database: Utilizes JDBC for secure and reliable database communication.

🗺️ Entity Relationship Diagram (ERD) Explanation
The database follows a relational model to ensure data integrity and historical accuracy.

Account Table: The "Parent" table. It stores the account_id, account_type, and the balance.

Note: The combination of ID and Type serves as the unique identifier, allowing one user to have multiple account types.

Transaction Table: The "Child" table. It stores a row for every movement of money.

One-to-Many Relationship: One account can have many transaction entries.

Auto-Increment: The transaction_id is handled by the database to prevent manual errors.

Timestamping: Uses CURRENT_TIMESTAMP to ensure audit logs cannot be tampered with.

🚀 How to Run the Program
Prerequisites
JDK 8+

MySQL Server

MySQL Connector/J (JDBC Driver)

NetBeans IDE (Recommended)

1. Database Setup
Execute the following SQL in your MySQL Workbench to prepare the environment:

SQL
CREATE DATABASE bank;
USE bank;

CREATE TABLE account (
    account_id VARCHAR(10),
    account_type VARCHAR(20),
    balance DOUBLE,
    PRIMARY KEY (account_id, account_type)
);

CREATE TABLE transaction (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    account_id VARCHAR(10),
    transaction_type VARCHAR(20),
    amount DOUBLE,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
2. Project Configuration
Clone this repository.

Add the mysql-connector-java-x.x.x.jar to your project libraries.

Open DBConnection.java and update your credentials:

Java
String url = "jdbc:mysql://localhost:3306/bank";
String user = "your_username";
String password = "your_password";
3. Execution
Run the MainFrame.java file.

The application will launch in the center of your screen.

Use the navigation panel to switch between Account Manager and Transaction History.

🛠️ Built With
Java Swing - GUI Framework

MySQL - Database

JDBC - Database Connectivity

Card Layout - UI Management
