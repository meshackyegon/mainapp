package com.jdbc_application;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class main_servlet extends HttpServlet {
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        try {
            String username = request.getParameter("Username");
            String password = request.getParameter("Password");
            String date_of_birth = request.getParameter("date_of_birth");
            String address = request.getParameter("address");
            String email = request.getParameter("email");
            String phone_number = request.getParameter("phone_number");

            String maskedName = maskName(username);
            String maskedPhone = maskPhoneNumber(phone_number);
            String hashedPhone = hashPhoneNumber(phone_number);
            database db = new database();
            String cssTag = "<link rel='stylesheet' type='text/css' href='./styling/style.css'>";
            out.println("<head><title>Servlet Home Page</title>" + cssTag);
            out.println("<meta http-equiv='X-UA-Compatible' content='IE=edge'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'></head>");
            out.println("<body>");
            String conditional = (String) request.getParameter("button");
            if (conditional.contentEquals("Register")) {
                try {
                    // db.put_data(username, password, date_of_birth, email, address, phone_number);
                    db.put_data(username, password, date_of_birth, email, address, phone_number,
                            maskedName, maskedPhone, hashedPhone);
                    out.println("<h2>Registration Page, <br> Successful Append into Database</h2>");
                    db.get_data(request, response);
                } catch (Exception e) {
                    RequestDispatcher rd = request.getRequestDispatcher("data_insertion.jsp");
                    rd.include(request, response);
                    out.println("<h3>Entered in wrong format, exception occured!" + "<br>Error!" + e + "</h3>");
                }
            } else if (conditional.contentEquals("Login Using HttpSession")) {
                response.setHeader("method", "session");
                try {
                    db.login(username, password, request, response);
                } catch (Exception e) {
                    RequestDispatcher rd = request.getRequestDispatcher("data_search.jsp");
                    rd.include(request, response);
                    out.println("<h3>Entered in wrong format, exception occured!" + "<br>Error!" + e + "</h3>");
                }
            } else if (conditional.contentEquals("Login Using Cookies")) {
                response.setHeader("method", "cookie");
                try {
                    db.login(username, password, request, response);
                } catch (Exception e) {
                    RequestDispatcher rd = request.getRequestDispatcher("data_search.jsp");
                    rd.include(request, response);
                    out.println("<h3>Entered in wrong format, exception occured!" + "<br>Error!" + e + "</h3>");
                }
            } else if (conditional.contentEquals("Update password")) {
                try {
                    String oldUsername = (String) request.getParameter("old_username");
                    String oldPassword = (String) request.getParameter("old_password");
                    String newPassword = (String) request.getParameter("new_password");
                    String confirmNewPassword = (String) request.getParameter("confirm_new_password");

                    if (newPassword == null || newPassword.isEmpty() || confirmNewPassword == null
                            || confirmNewPassword.isEmpty()) {
                        throw new Exception("Invalid password!");
                    }
                    if (!newPassword.equals(confirmNewPassword)) {
                        throw new Exception("Passwords do not match!");
                    }

                    db.update_row(oldUsername, oldPassword, newPassword);

                } catch (SQLException e) {
                    RequestDispatcher rd = request.getRequestDispatcher("data_search.jsp");
                    rd.include(request, response);
                    out.println("<h3>" + e + "</h3>");
                } catch (Exception e) {
                    RequestDispatcher rd = request.getRequestDispatcher("data_search.jsp");
                    rd.include(request, response);
                    out.println("<h3>" + "<br>Error!" + e + "</h3>");
                }
            } else if (conditional.contentEquals("Logout")) {
                HttpSession session = request.getSession(false);
                String user = (String) session.getAttribute("Username");
                // check in session storage
                if (user != null) {
                    session.removeAttribute("Username");
                    session.invalidate();
                    System.out.println("Logged out, removed column from (session): " + user);
                }
                // check in cookie storage
                else {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            user = cookie.getName();
                            if (user.equals("Username")) {
                                cookie.setMaxAge(0);
                                response.addCookie(cookie);
                                user = cookie.getValue();
                                break;
                            }
                        }
                        System.out.println("Logged out, removed column from (cookie): " + user);
                    }
                }
                // redirect to login page
                response.sendRedirect("data_search.jsp");
            } else if (conditional.contentEquals("Delete")) {
                try {
                    db.delete_row(username);
                } catch (Exception e) {
                    RequestDispatcher rd = request.getRequestDispatcher("view.jsp");
                    rd.include(request, response);
                    out.println("<h3>Query completed!" + "<br>" + e + "</h3>");
                }
            }
        } catch (Exception e) {
            RequestDispatcher rd = request.getRequestDispatcher("index.html");
            rd.include(request, response);
            out.println("<h3>Entered in wrong format, exception occured!" + "<br>Error!" + e + "</h3>");
        } finally {
            out.println("</body>");
            out.close();
        }
    }
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
    private String hashPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(phoneNumber.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
