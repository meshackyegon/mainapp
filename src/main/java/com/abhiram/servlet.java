package com.abhiram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/contact-api")
public class servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String dbUser = "root";
    private final String dbPassword = "";
    private final String dbURL = "jdbc:mysql://localhost:3306/servlet_application"; // Corrected URL
    private Connection con; // Declare Connection

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load Driver
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword); // Establish Connection
        } catch (Exception e) {
            throw new ServletException("Database connection failed", e);
        }
    }

    // CREATE (Insert a new contact)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Convert the received JSON string to a JSONObject
        JSONObject receivedJson = new JSONObject(sb.toString());

        JSONObject responseJson = new JSONObject();
        responseJson.put("received_data", receivedJson); // Send back received data
        out.print(receivedJson.toString());

//        // Send JSON response
//        out.print(responseJson.toString());
//        out.flush();
//             JSONObject json = new JSONObject();
//            json.put("status", "success");
//            json.put("message", "Contact saved successfully.");
//            out.print(json.toString());
       } 
    
//        try {
//            String username = request.getParameter("username");
//            String password = request.getParameter("password");
//            String dob = request.getParameter("date_of_birth");
//            String email = request.getParameter("email");
//            String address = request.getParameter("address");
//            String phone = request.getParameter("phonenumber");
//
//            String maskedName = maskName(username);
//            String maskedPhone = maskPhoneNumber(phone);
//            String hashedPhone = hashPhoneNumber(phone);
//
//            insertContact(username, password, dob, email, address, phone, maskedName, maskedPhone, hashedPhone);
//
//            JSONObject json = new JSONObject();
//            json.put("status", "success");
//            json.put("message", "Contact saved successfully.");
//            out.print(json.toString());
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            sendErrorResponse(out, "Database error: " + e.getMessage());
//        } finally {
//            out.close();
//        }
//    }

    private void sendErrorResponse(PrintWriter out, String message) {
        JSONObject json = new JSONObject();
        json.put("status", "error");
        json.put("message", message);
        out.print(json.toString());
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
             JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("message", "data retrieved successfully via API.");
            out.print(json.toString());
    }

    // Helper method to insert contact
    private void insertContact(String username, String password, String dob, String email, 
          String address, String phone, String maskedName, String maskedPhone, String hashedPhone)
          throws SQLException {
        String sql = "INSERT INTO contact (Username, Password, date_of_birth, email, phone_number, address, maskedname, maskedphone, hashedphone) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setDate(3, Date.valueOf(dob));
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.setString(6, address);
            stmt.setString(7, maskedName);
            stmt.setString(8, maskedPhone);
            stmt.setString(9, hashedPhone);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Failed to insert contact.");
            }
        }
    }

    @Override
    public void destroy() {
        try {
            if (con != null && !con.isClosed()) {
                con.close(); // Close connection when the servlet is destroyed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String maskName(String name) {
        return name.substring(0, 1) + "****"; // Simple masking
    }

    private String maskPhoneNumber(String phone) {
        return "****" + phone.substring(phone.length() - 4);
    }

    private String hashPhoneNumber(String phone) {
        return String.valueOf(phone.hashCode());
    }
}
