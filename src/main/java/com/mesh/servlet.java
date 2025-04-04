package com.mesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/contact-api")
public class servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String dbUser = "root";
    private final String dbPassword = "";
    private final String dbURL = "jdbc:mysql://localhost:3306/servlet";
    private Connection con;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
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

        try {
            JSONObject receivedJson = new JSONObject(sb.toString());
            String username = receivedJson.getString("username");
            String password = receivedJson.getString("password");
            String dob = receivedJson.getString("date_of_birth");
            String email = receivedJson.getString("email");
            String address = receivedJson.getString("address");
            String phone = receivedJson.getString("phonenumber");
            String organization = receivedJson.getString("organization");

            String maskedName = maskName(username);
            String maskedPhone = maskPhoneNumber(phone);
            String hashedPhone = hashPhoneNumber(phone);

            insertContact(username, password, dob, email, address, phone, maskedName, maskedPhone, organization, hashedPhone);

            JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("message", "Contact saved successfully.");
            out.print(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendErrorResponse(out, "Database error: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // READ (Fetch all contacts)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            JSONArray contacts = fetchAllContacts();
            JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("data", contacts);
            out.print(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendErrorResponse(out, "Database error: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // UPDATE (Update a contact)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            JSONObject receivedJson = new JSONObject(sb.toString());
            int id = receivedJson.getInt("id");
            String username = receivedJson.getString("username");
            String email = receivedJson.getString("email");
            

            updateContact(id, username, email);

            JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("message", "Contact updated successfully.");
            out.print(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendErrorResponse(out, "Database error: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // DELETE (Delete a contact)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        try {
            int id = Integer.parseInt(idParam);
            deleteContact(id);

            JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("message", "Contact deleted successfully.");
            out.print(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendErrorResponse(out, "Database error: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    private void insertContact(String username, String password, String dob, String email, 
          String address, String phone, String maskedName, String maskedPhone, String hashedPhone, String organization)
          throws SQLException {
        String sql = "INSERT INTO contact (Username, Password, date_of_birth, email, phone_number, address, maskedname, maskedphone, organization, hashedphone) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setDate(3, Date.valueOf(dob));
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.setString(6, address);
            stmt.setString(7, maskedName);
            stmt.setString(8, maskedPhone);
            stmt.setString(9, organization);
            stmt.setString(10, hashedPhone);

            stmt.executeUpdate();
        }
    }

    private JSONArray fetchAllContacts() throws SQLException {
        String sql = "SELECT * FROM contact";
        JSONArray contacts = new JSONArray();

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject contact = new JSONObject();
                contact.put("id", rs.getInt("id"));
                contact.put("username", rs.getString("Username"));
                contact.put("email", rs.getString("email"));
                contact.put("phone_number", rs.getString("phone_number"));
                contact.put("address", rs.getString("address"));
                contacts.put(contact);
            }
        }

        return contacts;
    }

    private void updateContact(int id, String username, String email) throws SQLException {
        String sql = "UPDATE contact SET Username = ?, email = ? WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setInt(3, id);

            stmt.executeUpdate();
        }
    }

    private void deleteContact(int id) throws SQLException {
        String sql = "DELETE FROM contact WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }

    private void sendErrorResponse(PrintWriter out, String message) {
        JSONObject json = new JSONObject();
        json.put("status", "error");
        json.put("message", message);
        out.print(json.toString());
    }

    @Override
    public void destroy() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String maskName(String name) {
        return name.substring(0, 1) + "****";
    }

    private String maskPhoneNumber(String phone) {
        return "****" + phone.substring(phone.length() - 4);
    }

    private String hashPhoneNumber(String phone) {
        return String.valueOf(phone.hashCode());
    }
}
