<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Table Data</title>
    <!-- Bootstrap 4 CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.*" %>
        <%@ page import="java.lang.*" %>
            <%@ page import="java.sql.Connection" %>
                <%@ page import="java.sql.DriverManager" %>
                    <%@ page import="java.sql.Statement" %>
                        <%@ page import="java.sql.ResultSet" %>

                            <h2 class="text-center my-4">The database has these users registered through this form.</h2>

                            <%! String password="" ; %>

                                <% // this makes the previous page out of the cached pages.
                                    response.setHeader("Cache-control","no-cache, no-store, must-revalidate");
                                    Class.forName("com.mysql.cj.jdbc.Driver"); Connection
                                    con=DriverManager.getConnection("jdbc:mysql://localhost:3306/servlet_application","root",this.password);
                                    Statement stmt=con.createStatement(); ResultSet rs=stmt.executeQuery("SELECT * FROM
                                    store_user ORDER BY date_register;"); %>

                                    <div class="container">
                                        <div class="table-responsive">
                                            <table class="table table-bordered table-striped">
                                                <thead class="thead-dark">
                                                    <tr>
                                                        <th>S NO.</th>
                                                        <th>Username</th>
                                                        <th>Password</th>
                                                        <th>Date of Birth</th>
                                                        <th>Registered Date</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <% int i=0; while(rs.next()){ %>
                                                        <tr>
                                                            <td>
                                                                <%= i+1 %>
                                                            </td>
                                                            <td>
                                                                <%= rs.getString(1) %>
                                                            </td>
                                                            <td>
                                                                <%= rs.getString(2) %>
                                                            </td>
                                                            <td>
                                                                <%= rs.getString(3) %>
                                                            </td>
                                                            <td>
                                                                <%= rs.getString(4) %>
                                                            </td>
                                                        </tr>
                                                        <% i++; } i=0; %>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>

                                    <!-- Bootstrap 4 JS (Optional) -->
                                    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
                                    <script
                                        src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
                                    <script
                                        src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>