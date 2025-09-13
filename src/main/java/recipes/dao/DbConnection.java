package recipes.dao;
//dao = Data Access Object
//dao class:
    // Deals with communicating with a database
	// Open connections
    // run SQL queries
    // handle insert, update, delete, select statements

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import recipes.exception.DbException;


public class DbConnection {
//Builds connection string
//private = can only be accessed inside this class DbConnection
//static = tied to the class, not objects
//makes the variable a constant meaning it can't be changed
private static final String SCHEMA = "recipes";
private static final String USER ="recipes";
private static final String PASSWORD = "recipes";
private static final String HOST = "localhost";
private static final int PORT =3306;


public static Connection getConnection() {//method return type Connnection method name = getConnection()
	//builds JDBC URL to connect to database
	String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
			HOST, PORT,SCHEMA,USER,PASSWORD);
	

	
	try {
		Connection conn = DriverManager.getConnection(url);
		System.out.println("Successfully obtained connection!");
		return conn;
		
//if connection fails(e.g.; wrong password) will throw DbException "e". 
//will get passed to constructor in DbException class:
		//public DbException(Throwable cause) {
		   // super(cause);// this extends to built in super class RunTimeException
		//}
	} catch (SQLException e) { 
      throw new DbException(e);
	}
}

}

