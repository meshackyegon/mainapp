<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Data Insertion Form</title>
    <!-- Bootstrap 4 CDN -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>

<body>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Contacts Form</h1>

        <!-- View Data Button -->
        <!-- <form action="./view.jsp" method="POST">
            <div class="form-group">
                <input type="submit" class="btn btn-primary" value="Click to see all data in the table">
            </div>
        </form> -->

        <% 
            String username = (String) session.getAttribute("Username");
            // before asking for credentials, check the session.
            if (username != null) {
                session.setAttribute("Username", username);
                response.sendRedirect("home_user.jsp");
                return;
            }
            else {
                System.out.println("Register page(session): " + username);
            }

            // check for any cookies.
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("Username")) {
                        username = cookie.getValue();
                        System.out.println("Register page(cookie): " + username);
                        break;
                    }
                }
            }
            if (username != null) {
                response.sendRedirect("home_user.jsp");
                return;
            }
        %>

        <!-- Registration Form -->
        <form action="./success" method="POST">
            <div class="form-group">
                <label for="Username">Username: </label>
                <input type="text" class="form-control" name="Username" id="Username" autocomplete="off" required>
            </div>

            <div class="form-group">
                <label for="Password">Password: </label>
                <input type="password" class="form-control" name="Password" id="Password" required>
            </div>

            <div class="form-group">
                <label for="date_of_birth">Date of Birth: </label>
                <input type="date" class="form-control" name="date_of_birth" id="date_of_birth" required>
            </div>

            <!-- Additional columns/fields -->
            <div class="form-group">
                <label for="email">Email: </label>
                <input type="email" class="form-control" name="email" id="email" autocomplete="off" required>
            </div>

            <div class="form-group">
                <label for="phone_number">Phone Number: </label>
                <input type="tel" class="form-control" name="phone_number" id="phone_number" pattern="[0-9]{10}" placeholder="1234567890" required>
            </div>

            <div class="form-group">
                <label for="address">Address: </label>
                <textarea class="form-control" name="address" id="address" rows="4" placeholder="Enter your address here" required></textarea>
            </div>

            <div class="form-group text-center">
                <input type="submit" class="btn btn-success" value="Register" name="button">
            </div>
        </form>
    </div>

    <!-- Bootstrap JS and dependencies -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.0/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
