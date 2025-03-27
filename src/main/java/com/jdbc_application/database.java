package com.jdbc_application;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class database {
    String password = "";
    private String maskName(String name) {
        if (name == null || name.length() <= 2) {
            return name;
        }
        StringBuilder masked = new StringBuilder();
        masked.append(name.charAt(0));
        for (int i = 1; i < name.length() - 1; i++) {
            masked.append('*');
        }
        masked.append(name.charAt(name.length() - 1));
        return masked.toString();
    }

    // Helper method to mask a phone number (keeps last 4 digits)
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() <= 4) {
            return phoneNumber;
        }
        return phoneNumber.replaceAll("\\d(?=(?:\\D*\\d){4})", "*");
    }

    // Helper method to create SHA-256 hash of phone number
    private String hashPhoneNumber(String phoneNumber) throws NoSuchAlgorithmException {
        if (phoneNumber == null) {
            return null;
        }
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(phoneNumber.getBytes());
        
        // Convert bytes to hexadecimal
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }

    public void put_data(String Username, String Password, String date_of_birth, String email, String address, String phonenumber, String maskedname, String maskedphone, String hashedphone)
 throws Exception {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", this.password);
    Statement stmt1 = con.createStatement();
    stmt1.executeUpdate("CREATE DATABASE IF NOT EXISTS servlet ;");
    stmt1.executeUpdate("USE servlet;");
    stmt1.executeQuery("SELECT DATABASE();");
    
    // Create table with new columns
    String query1 = ("CREATE TABLE IF NOT EXISTS contact(" + 
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "Username VARCHAR(30) NOT NULL UNIQUE," +
                     "Password VARCHAR(30) NOT NULL," +
                     "date_of_birth date NOT NULL," + 
                     "email varchar(100) NOT NULL," +
                     "phone_number varchar(50) NOT NULL," +
                     "address varchar(100) NOT NULL," +
                     "maskedname varchar(255) NOT NULL," +
                     "maskedphone varchar(255) NOT NULL," +
                     "hashedphone varchar(255) NOT NULL," +
                     "date_register timestamp NOT NULL);");
    stmt1.executeUpdate(query1);
    String maskedName = maskName(Username);
    String maskedPhone = maskPhoneNumber(phonenumber);
    String hashedPhone = hashPhoneNumber(phonenumber);
    
    
    PreparedStatement stmt2;
    stmt2 = con.prepareStatement("INSERT INTO contact (Username, Password, date_of_birth, email, phone_number, address, maskedname, maskedphone, hashedphone, date_register) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
    System.out.println(email);
    // Set parameters for the query
    stmt2.setString(1, Username);
    stmt2.setString(2, Password);
    stmt2.setDate(3, Date.valueOf(date_of_birth));  
    stmt2.setString(4, email);
    stmt2.setString(5, phonenumber);
    stmt2.setString(6, address);
    stmt2.setString(7, maskedname);
    stmt2.setString(8, maskedphone);
    stmt2.setString(9, hashedphone);
    // stmt2.setDate(3, Date.valueOf(date_of_birth));
    
    // Set the current timestamp for the `date_register` field
    java.sql.Timestamp timestamp = getCurrentJavaSqlTimestamp();
    stmt2.setTimestamp(10, timestamp);
    
    // Execute the insertion
    stmt2.executeUpdate();
    
    // Close the connection
    con.close();
    }

    private Timestamp getCurrentJavaSqlTimestamp() {
        java.util.Date date = new java.util.Date();
        return new java.sql.Timestamp(date.getTime());
    }

        public void get_data(HttpServletRequest request, HttpServletResponse response)
            throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/servlet", "root", this.password);
        
        Statement stmt = con.createStatement();
        ResultSet rs1 = stmt.executeQuery(
                "SELECT Username, maskedname, maskedphone FROM contact ORDER BY date_register;");
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        
        out.println("<table border='1'>");
        out.println("<tr><th>Username</th><th>Masked Name</th><th>Masked Phone</th></tr>");
        
        while (rs1.next()) {
            out.println("<tr>");
            out.println("<td>" + rs1.getString(1) + "</td>");
            out.println("<td>" + rs1.getString(2) + "</td>");
            out.println("<td>" + rs1.getString(3) + "</td>");
            out.println("</tr>");
        }
        
        out.println("</table>");
        con.close();
    }

    public void login(String username, String password, HttpServletRequest request, HttpServletResponse response)
            throws ClassNotFoundException, SQLException, IOException, ServletException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/servlet", "root",
                this.password);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM contact WHERE Username=" + "'" + username + "';");
        if (!rs.next()) {
            throw new SQLException("No Username found");
        } else {
            rs = stmt.executeQuery("SELECT * FROM contact WHERE Username=" + "'" + username + "' AND password=" + "'"
                    + password + "';");
            if (!(rs.next())) {
                throw new SQLException("Invalid Password");
            } else {
                // if username and password is found in the database,
                // put details in the session till user logs out.
                String method = response.getHeader("method");
                System.out.println("Login: " + method);
                if (method == "session") {
                    HttpSession session = request.getSession();
                    // storing the username in the session is enough as it is primary key
                    session.setAttribute("Username", username);
                } else if (method == "cookie") {
                    Cookie loginCookie = new Cookie("Username", username);
                    response.addCookie(loginCookie);
                }
                // redirect to User home page.
                response.sendRedirect("home_user.jsp");
            }
        }
    }

    public void delete_row(String username) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/servlet", "root",
                this.password);
        Statement stmt = con.createStatement();

        // https://stackoverflow.com/questions/2571915/return-number-of-rows-affected-by-sql-update-statement-in-java
        int update_count = stmt.executeUpdate("DELETE FROM contact WHERE Username='" + username + "';");
        if (update_count > 0) {
            throw new SQLException("User data deleted!");
        } else {
            throw new SQLException("No User with specified Username found!");
        }
    }

    public void update_row(String username, String oldPassword, String newPassword)
            throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/servlet", "root",
                this.password);
        Statement stmt = con.createStatement();

        int update_count = stmt.executeUpdate("UPDATE contact SET password='" + newPassword + "' WHERE username='"
                + username + "' AND password='" + oldPassword + "';");
        if (update_count > 0) {
            throw new SQLException("User password updated!");
        } else {
            throw new SQLException("Username/password not found!");
        }
    }
}
