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

@WebServlet("/search-api")
public class serve extends HttpServlet {

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject requestBody = new JSONObject(sb.toString());
            String action = requestBody.optString("action");

            if ("searchContact".equals(action)) {
                String phoneHash = requestBody.optString("phoneHash");
                String maskedName = requestBody.optString("maskedName");
                String maskedPhone = requestBody.optString("maskedPhone");
                out.print(searchContact(phoneHash, maskedName, maskedPhone));
            } else if ("listOrganizationContacts".equals(action)) {
                String organizationName = requestBody.optString("organizationName");
                out.print(listOrganizationContacts(organizationName));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "Invalid action"));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", e.getMessage()));
        }
    }

    private JSONObject searchContact(String phoneHash, String maskedName, String maskedPhone) throws SQLException {
        String query = "SELECT * FROM contact WHERE phone_hash = ? OR (masked_name = ? AND masked_phone = ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, phoneHash);
            stmt.setString(2, maskedName);
            stmt.setString(3, maskedPhone);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JSONObject contact = new JSONObject();
                contact.put("id", rs.getInt("id"));
                contact.put("organization", rs.getString("organization"));
                contact.put("phone_number", rs.getString("phone_number"));
                return contact;
            } else {
                return new JSONObject().put("message", "Contact not found");
            }
        }
    }

    private JSONArray listOrganizationContacts(String organizationName) throws SQLException {
        String query = "SELECT * FROM contact WHERE organization = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, organizationName);

            ResultSet rs = stmt.executeQuery();
            JSONArray contacts = new JSONArray();
            while (rs.next()) {
                JSONObject contact = new JSONObject();
                contact.put("id", rs.getInt("id"));
                contact.put("organization", rs.getString("organization"));
                contact.put("phone_number", rs.getString("phone_number"));
                contacts.put(contact);
            }
            return contacts;
        }
    }

    @Override
    public void destroy() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
