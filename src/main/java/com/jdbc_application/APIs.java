package com.jdbc_application;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.ext.Provider;

import com.mesh.model.Contact;

@Provider
@Path("/contacts")
public class APIs implements javax.ws.rs.container.ContainerRequestFilter, javax.ws.rs.container.ContainerResponseFilter {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/servlet_application";
    private static final String DB_USER = "root";
    private String password = "";  // Your database password

    // Database connection helper method
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, this.password);
    }

    // Method to fetch contacts from the database
    private List<Contact> fetchContactsFromDatabase() throws SQLException {
        List<Contact> contactList = new ArrayList<>();
        String query = "SELECT Username, maskedname, maskedphone FROM contact ORDER BY date_register";

        try (Connection con = getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String username = rs.getString("Username");
                String maskedName = rs.getString("maskedname");
                String maskedPhone = rs.getString("maskedphone");

                // Create and add contact to the list
                contactList.add(new Contact(username, maskedName, maskedPhone));
            }
        }
        return contactList;
    }

    // GET method to retrieve the list of contacts
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllContacts() {
        try {
            List<Contact> contacts = fetchContactsFromDatabase();

            // If contacts are found, return them as JSON response
            if (contacts.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT)
                               .entity("No contacts found")
                               .build();
            }

            // Return the contact list as JSON
            return Response.ok(contacts).build();

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Database error: " + e.getMessage())
                           .build();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Optional: Implement logic for incoming request filtering (e.g., authentication)
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // Optional: Implement logic for modifying the response (e.g., add headers)
    }
}
