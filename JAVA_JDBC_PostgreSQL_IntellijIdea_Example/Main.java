package com.company;

//How to get PostgreSQL to work with Intellij Idea on Ubuntu 18.04 LTS:
//1) Create new JAVA project.
//2) Install posgresql: $sudo apt install postgresql.
//3) Download postgresql JDBC driver from: https://jdbc.postgresql.org/download.html#archived.
//"Binary JAR file downloads of the JDBC driver are available here and
//the current version with Maven Repository. Because Java is platform
//neutral, it is a simple process of just downloading the appropriate
//JAR file and dropping it into your classpath. Source versions are
//also available here for recent driver versions.
//4)https://stackoverflow.com/questions/7695962/postgresql-password-authentication-failed-for-user-postgres:
//$sudo -u postgres psql postgres - launch posgresql from the terminal and
//Inside the psql shell you can give the DB user postgres a password:
//ALTER USER postgres PASSWORD 'newPassword'; - i wrote "mysteel".
//THIS WILL SAVE FROM password authentication failed for user “postgres”.

//BEFORE WORK, THE DB, WITH WHICH YOU WANT TO WORK, SHOULD HAVE ALREADY BEEN CREATED
//OUTSIDE JAVA IDE. YOU CONNECT WITH IT AS "jdbc:postgresql:db1", WHERE db1 - DATABASE NAME.

//THIS COMMAND SHOWS WHERE db IS STORED:
//frpm posgresql console: show data_directory;
//OUPUT:
//   data_directory
//-----------------------------
//  /var/lib/postgresql/10/main


//STEP 1. Import required packages
import java.sql.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class Main {

   //  Database credentials
      static final String DB_URL = "jdbc:postgresql:db1";
      static final String USER = "postgres";
      static final String PASS = "mysteel";

  public static void main(String[] argv) throws ClassNotFoundException, SQLException{

	System.out.println("Testing connection to PostgreSQL JDBC");

	try {
		Class.forName("org.postgresql.Driver");
	} catch (ClassNotFoundException e) {
		System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
		e.printStackTrace();
		return;
	}

	System.out.println("PostgreSQL JDBC Driver successfully connected");
	Connection connection = null;

	try {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);

	} catch (SQLException e) {
		System.out.println("Connection Failed");
		e.printStackTrace();
		return;
	}

	if (connection != null) {
		System.out.println("You successfully connected to database now");
		connection.setAutoCommit(false);
		Statement statement = connection.createStatement();
		String SQL;
		SQL="DROP TABLE IF EXISTS books CASCADE;";
		statement.executeUpdate(SQL);
		SQL = "CREATE TABLE IF NOT EXISTS books(" +
				"id SERIAL PRIMARY KEY," +
				"name VARCHAR(30)" +
				");";
		statement.executeUpdate(SQL);
        statement.close();
        statement = connection.createStatement();
        SQL = "INSERT INTO books (name) VALUES ('Bible');";
        statement.executeUpdate(SQL);
		SQL = "INSERT INTO books (name) VALUES ('War of Worlds');";
        statement.executeUpdate(SQL);
        SQL = "INSERT INTO books (name) VALUES ('Neznaika');";
        statement.executeUpdate(SQL);
        SQL = "INSERT INTO books (name) VALUES ('Blossom');";
		statement.executeUpdate(SQL);
		SQL = "INSERT INTO books (name) VALUES ('Ways');";
        statement.executeUpdate(SQL);

		SQL = "ALTER TABLE books ADD COLUMN price INT NOT NULL DEFAULT 2000;";
		statement.executeUpdate(SQL);
		SQL = "UPDATE books Set price=10000 WHERE  id=1;";
		statement.executeUpdate(SQL);

        SQL = "SELECT * FROM books ORDER BY id;";
		ResultSet resultSet = statement.executeQuery(SQL);
		while(resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
			int price = resultSet.getInt("price");
            System.out.println("\n================\n");
            System.out.println("id: " + id);
            System.out.println("Name: " + name);
			System.out.println("Price: " + price);
        }

		SQL="DROP TABLE IF EXISTS publishers;";
		statement.executeUpdate(SQL);
		SQL = "CREATE TABLE IF NOT EXISTS publishers(" +
				"id SERIAL PRIMARY KEY," +
				"name VARCHAR(30)," +
				"FOREIGN KEY(id) REFERENCES books(id)" +
				");";
		statement.executeUpdate(SQL);

		SQL="INSERT INTO publishers (name) VALUES('France');";
		statement.addBatch(SQL);
		SQL="INSERT INTO publishers (name) VALUES('Moscow');";
		statement.addBatch(SQL);
		SQL="INSERT INTO publishers (name) VALUES('New York');";
		statement.addBatch(SQL);
		statement.executeBatch();
		connection.commit();

	} else {
		System.out.println("Failed to make connection to database");
	}
  }
}