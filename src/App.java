import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.sqlite.JDBC;

public class App {
    public static void main(String[] args) {
        // Declare a Connection reference variable
        // Connection is an Interface 
        //   A connection (session) with a specific database. 
        //   SQL statements are able to be created and results are returned
        //   within the context of a connection.
        // We will use:
        //  Statement createStatement() to create a Statement (see below)
        //  void setAutoCommit(boolean) to allow us to manage transactions manually
        //  void commit() to mark the end of a transaction and save all of the chantes
        Connection c = null;

        // Declare a Statement reference variable
        // Statement is an Interface 
        //      For executing a static SQL statement
        //      and returning the results it produces.
        // We will use:
        //  int executeUpdate(String) to execute the given string as an SQL statement
        //  void close() to releases this Statement object's database and JDBC resources immediately 
        //      instead of waiting for this to happen when it is automatically closed.
        Statement stmt = null;

        try {
           // Using the static method getConnection with an sqlite 
           // database url of the form:
           //  jdbc:subprotocol:name
           c = DriverManager.getConnection("jdbc:sqlite:testdb1.sqlite");
        } catch (SQLException e) {
           System.err.println(e.getClass().getName()+": "+e.getMessage());
           System.exit(-1);  
        }
        System.out.println("Opened database successfully!");        

        Scanner user = new Scanner(System.in);

        System.out.print("Create a table? (y/n)");

        String response = user.next();
        boolean create_table = response.toLowerCase().trim().equals("y");

        System.out.print("What is the name of the table (no spaces)?");
        String table_name = user.next().trim().toUpperCase();
        if(table_name.indexOf(' ') != -1) {
            // for now, simple check
            System.out.println("Only single word tables");
            System.exit(-1);
        }            

        if(create_table)
        {   
            try {
                stmt = c.createStatement();
                String sql = "CREATE TABLE " + table_name +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " AGE            INT     NOT NULL, " +
                    " ADDRESS        CHAR(50), " +
                    " SALARY         REAL)";
                stmt.executeUpdate(sql);
                stmt.close();
            // c.close();
            } catch ( SQLException e ) {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(-1);
            }
            System.out.printf("Table %s created successfully%n", table_name);
        }

        System.out.print("Add an employee to the table? (y/n)");

        response = user.next();
        boolean add_user = response.toLowerCase().trim().equals("y");

        if(add_user)
        {
            System.out.print("What is the name of the employee (no spaces)?");
            String data_value = user.next().trim();

            try {
                c.setAutoCommit(false);
   
                stmt = c.createStatement();
                String record = String.format("(%d, '%s', %d, '%s', %.2f)",(int)(Math.random() * 100000), data_value,26, "123 Main Street", 15000.00);
                String sql = String.format("INSERT INTO %s (ID,NAME,AGE,ADDRESS,SALARY) VALUES %s ;", table_name, record);

                stmt.executeUpdate(sql);
   
                stmt.close();
                c.commit();
            } catch (SQLException e) {            
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(-1);
            }
            System.out.println("Records inserted successfully");    
        }

        try {
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql =  "SELECT * FROM  " + table_name + " ;";
            ResultSet rs = stmt.executeQuery(sql);
            String formats = "%-5s %-25s %-5s %-25s %-10s%n";
            System.out.printf(formats,"ID","NAME", "AGE", "ADDRESS" ,"SALARY");
            formats = "%-5d %-25s %-5d %-25s %-10.2f%n";
            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  name = rs.getString("name");
                int age  = rs.getInt("age");
                String  address = rs.getString("address");
                float salary = rs.getFloat("salary");
                System.out.printf(formats, id, name, age, address, salary);
            }
            rs.close();
            stmt.close();            
        } catch (SQLException e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() + e.getSQLState());        
            System.exit(-1);
        }
        System.out.println("Records retrieved successfully");    
         

        System.out.print("Change an employee's salary? (y/n)");

        response = user.next();
        boolean change_salary = response.toLowerCase().trim().equals("y");

        if(change_salary)
        {
            System.out.print("What is the ID of the employee? ");
            String id = user.next().trim();

            System.out.print("What is the new Salary for the employee?");
            float salary = user.nextFloat();

            try {
                c.setAutoCommit(false);
    
                stmt = c.createStatement(); 
                String sql = String.format("UPDATE %s SET SALARY = %.2f WHERE ID = %s;", table_name, salary, id);
                stmt.executeUpdate(sql);
    
                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.err.println(e.getClass().getName()+": "+ e.getMessage() );
            }
            System.out.println("Records updated successfully");   
        }

        try {
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql =  "SELECT * FROM  " + table_name + " ;";
            ResultSet rs = stmt.executeQuery(sql);
            String formats = "%-5s %-25s %-5s %-25s %-10s%n";
            System.out.printf(formats,"ID","NAME", "AGE", "ADDRESS" ,"SALARY");
            formats = "%-5d %-25s %-5d %-25s %-10.2f%n";
            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  name = rs.getString("name");
                int age  = rs.getInt("age");
                String  address = rs.getString("address");
                float salary = rs.getFloat("salary");
                System.out.printf(formats, id, name, age, address, salary);
            }
            rs.close();
            stmt.close();            
        } catch (SQLException e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() + e.getSQLState());        
            System.exit(-1);
        }
        System.out.println("Records retrieved successfully");            

        System.out.print("Delete an employee? (y/n)");

        response = user.next();
        boolean delete_employee = response.toLowerCase().trim().equals("y");

        if(delete_employee)
        {
            System.out.print("What is the ID of the employee? ");
            String id = user.next().trim();
            int recordsAffected = 0;

            try {
                c.setAutoCommit(false);
    
                stmt = c.createStatement(); 

                String sql = String.format("DELETE from %s WHERE ID = %s;", table_name, id);
                recordsAffected = stmt.executeUpdate(sql);

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.err.println(e.getClass().getName()+": "+ e.getMessage() );
            }
            System.out.println(recordsAffected + " records where ID = " + id + " deleted successfully");    
        }

        try {
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql =  "SELECT * FROM  " + table_name + " ;";
            ResultSet rs = stmt.executeQuery(sql);
            String formats = "%-5s %-25s %-5s %-25s %-10s%n";
            System.out.printf(formats,"ID","NAME", "AGE", "ADDRESS" ,"SALARY");
            formats = "%-5d %-25s %-5d %-25s %-10.2f%n";
            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  name = rs.getString("name");
                int age  = rs.getInt("age");
                String  address = rs.getString("address");
                float salary = rs.getFloat("salary");
                System.out.printf(formats, id, name, age, address, salary);
            }
            rs.close();
            stmt.close();            
        } catch (SQLException e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() + e.getSQLState());        
            System.exit(-1);
        }
        System.out.println("Records retrieved successfully");            

        try {
            if(c != null) {
                c.close();
            }
        }
        catch(SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(-1);
        }
    }
}