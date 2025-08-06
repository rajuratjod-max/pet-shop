// package DBMSProject; // Remove if you're not using packages

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.sql.Date;
import com.sun.net.httpserver.*;
import com.google.gson.*;
import java.security.MessageDigest;
import java.math.BigInteger;

public class RestApiServer {
    private static Main mainApp = new Main();
    private static Gson gson = new Gson();
    
    // Database connection details - Update these for your setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/miniproject";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "raju"; // Update with your MySQL password
    
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        
        System.out.println("🚀 Starting Pet Adoption API Server...");
        
        // Test database connection on startup
        testDatabaseConnection();
        
        // Enable CORS for all endpoints
        server.createContext("/", new CorsHandler());
        
        // Pet endpoints
        server.createContext("/api/pets", new PetsHandler());
        server.createContext("/api/pets/adopt", new AdoptPetHandler());
        server.createContext("/api/pets/add", new AddPetHandler());
        server.createContext("/api/pets/specific", new SpecificPetHandler());
        
        // Product endpoints
        server.createContext("/api/products", new ProductsHandler());
        server.createContext("/api/products/buy", new BuyProductHandler());
        server.createContext("/api/products/add", new AddProductHandler());
        server.createContext("/api/products/update-price", new UpdatePriceHandler());
        
        // Customer endpoints
        server.createContext("/api/customers", new CustomersHandler());
        server.createContext("/api/customers/add", new AddCustomerHandler());
        server.createContext("/api/customers/register", new RegisterCustomerHandler());
        server.createContext("/api/customers/login", new CustomerLoginHandler());
        server.createContext("/api/customers/history", new CustomerHistoryHandler());
        
        // Admin endpoints
        server.createContext("/api/admin/login", new AdminLoginHandler());
        server.createContext("/api/admin/sales", new SalesReportHandler());
        server.createContext("/api/admin/adopted-pets", new AdoptedPetsHandler());
        
        // Order endpoints
        server.createContext("/api/orders", new OrdersHandler());
        server.createContext("/api/orders/history", new OrderHistoryHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("🌟 Pet Adoption API Server started on port " + port);
        System.out.println("📱 Frontend can now connect to: http://localhost:" + port);
        System.out.println("🔍 Debug logging is enabled for troubleshooting");
        System.out.println("🔐 Admin login: username='admin', password='admin123'");
    }
    
    // Test database connection on startup
    private static void testDatabaseConnection() {
        try {
            System.out.println("🔍 Testing database connection...");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Database connection successful!");
            
            // Test if tables exist
            DatabaseMetaData meta = con.getMetaData();
            String[] tables = {"customer", "customer_login", "product", "pet", "admin_login", "orders", "adopts"};
            for (String table : tables) {
                ResultSet rs = meta.getTables(null, null, table, new String[]{"TABLE"});
                if (rs.next()) {
                    System.out.println("✅ " + table + " table found");
                } else {
                    System.out.println("❌ " + table + " table NOT found!");
                }
            }
            
            con.close();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Utility method to hash passwords (SHA-256)
    private static String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        BigInteger number = new BigInteger(1, hash);
        return String.format("%064x", number);
    }
    
    // CORS Handler for cross-origin requests
    static class CorsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
        }
    }
    
    // POST /api/admin/login - Admin login
    static class AdminLoginHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("🔐 POST /api/admin/login - Admin login request");
            
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, "Method Not Allowed. Use POST.");
                return;
            }
            
            try {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), "UTF-8");
                Map<String, Object> loginData = gson.fromJson(body, Map.class);
                
                String username = (String) loginData.get("username");
                String password = (String) loginData.get("password");
                
                System.out.println("🔍 Admin login attempt: " + username);
                
                boolean isValid = validateAdminLogin(username, password);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", isValid);
                if (isValid) {
                    response.put("message", "Admin login successful");
                    response.put("admin_name", "System Administrator");
                    response.put("role", "admin");
                    System.out.println("✅ Admin login successful: " + username);
                } else {
                    response.put("message", "Invalid admin credentials");
                    System.out.println("❌ Failed admin login attempt: " + username);
                }
                
                sendJsonResponse(exchange, gson.toJson(response));
                
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Internal server error");
            }
        }
        
        private boolean validateAdminLogin(String username, String password) {
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT admin_id, password_hash FROM admin_login WHERE username = ? AND is_active = TRUE";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, username);
                
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = hashPassword(password);
                    
                    if (storedHash.equals(inputHash)) {
                        // Update last login
                        String updateSql = "UPDATE admin_login SET last_login = NOW() WHERE username = ?";
                        PreparedStatement updateStmt = con.prepareStatement(updateSql);
                        updateStmt.setString(1, username);
                        updateStmt.executeUpdate();
                        
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    
    // POST /api/customers/login - Customer login
    static class CustomerLoginHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("👤 POST /api/customers/login - Customer login request");
            
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, "Method Not Allowed. Use POST.");
                return;
            }
            
            try {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), "UTF-8");
                Map<String, Object> loginData = gson.fromJson(body, Map.class);
                
                String email = (String) loginData.get("email");
                String password = (String) loginData.get("password");
                
                System.out.println("🔍 Customer login attempt: " + email);
                
                Map<String, Object> customerInfo = validateCustomerLogin(email, password);
                
                Map<String, Object> response = new HashMap<>();
                if (customerInfo != null) {
                    response.put("success", true);
                    response.put("message", "Login successful");
                    response.put("customer_id", customerInfo.get("customer_id"));
                    response.put("customer_name", customerInfo.get("customer_name"));
                    response.put("email", email);
                    System.out.println("✅ Customer login successful: " + email);
                } else {
                    response.put("success", false);
                    response.put("message", "Invalid email or password");
                    System.out.println("❌ Failed customer login attempt: " + email);
                }
                
                sendJsonResponse(exchange, gson.toJson(response));
                
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Internal server error");
            }
        }
        
        private Map<String, Object> validateCustomerLogin(String email, String password) {
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT cl.customer_id, cl.password_hash, c.customer_name " +
                           "FROM customer_login cl " +
                           "JOIN customer c ON cl.customer_id = c.customer_id " +
                           "WHERE cl.email = ? AND cl.is_active = TRUE";
                
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, email);
                
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = hashPassword(password);
                    
                    if (storedHash.equals(inputHash)) {
                        // Update last login
                        String updateSql = "UPDATE customer_login SET last_login = NOW() WHERE email = ?";
                        PreparedStatement updateStmt = con.prepareStatement(updateSql);
                        updateStmt.setString(1, email);
                        updateStmt.executeUpdate();
                        
                        Map<String, Object> customerInfo = new HashMap<>();
                        customerInfo.put("customer_id", rs.getInt("customer_id"));
                        customerInfo.put("customer_name", rs.getString("customer_name"));
                        return customerInfo;
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    // POST /api/customers/register - Register new customer with login credentials
    static class RegisterCustomerHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("👤 POST /api/customers/register - Registration request");
            
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, "Method Not Allowed. Use POST.");
                return;
            }
            
            try {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), "UTF-8");
                Map<String, Object> regData = gson.fromJson(body, Map.class);
                
                System.out.println("📄 Registration data received: " + regData.get("email"));
                
                // Basic validation
                if (!validateRegistrationData(regData)) {
                    sendErrorResponse(exchange, "Missing or invalid registration fields.");
                    return;
                }
                
                boolean success = registerNewCustomer(regData);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", success);
                if (success) {
                    response.put("message", "Registration successful!");
                    System.out.println("✅ New customer registered: " + regData.get("email"));
                } else {
                    response.put("message", "Registration failed. Email may already exist.");
                }
                
                sendJsonResponse(exchange, gson.toJson(response));
                
            } catch (JsonSyntaxException e) {
                sendErrorResponse(exchange, "Invalid JSON format.");
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Internal server error.");
            }
        }
        
        private boolean validateRegistrationData(Map<String, Object> data) {
            if (data == null) return false;
            
            for (String field : new String[]{"customer_name", "address", "mobile_number", "email", "password"}) {
                Object value = data.get(field);
                if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
                    System.err.println("❌ Missing or empty registration field: " + field);
                    return false;
                }
            }
            return true;
        }
        
        private boolean registerNewCustomer(Map<String, Object> regData) {
            Connection con = null;
            PreparedStatement customerStmt = null;
            PreparedStatement loginStmt = null;
            ResultSet generatedKeys = null;
            
            try {
                con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                con.setAutoCommit(false);
                
                // Check if email already exists
                String checkEmailSql = "SELECT email FROM customer_login WHERE email = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkEmailSql);
                checkStmt.setString(1, ((String) regData.get("email")).trim());
                ResultSet emailCheck = checkStmt.executeQuery();
                
                if (emailCheck.next()) {
                    System.err.println("❌ Email already exists: " + regData.get("email"));
                    return false;
                }
                
                // Insert into customer table (customer_id will auto-increment)
                String insertCustomerSQL = "INSERT INTO customer (customer_name, address, mobile_number, email_address) VALUES (?, ?, ?, ?)";
                customerStmt = con.prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS);
                customerStmt.setString(1, ((String) regData.get("customer_name")).trim());
                customerStmt.setString(2, ((String) regData.get("address")).trim());
                customerStmt.setString(3, ((String) regData.get("mobile_number")).trim());
                customerStmt.setString(4, ((String) regData.get("email")).trim());
                
                int affectedRows = customerStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating customer failed, no rows affected.");
                }
                
                generatedKeys = customerStmt.getGeneratedKeys();
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
                int customerId = generatedKeys.getInt(1);
                
                String hashedPassword = hashPassword(((String) regData.get("password")).trim());
                
                // Insert into customer_login table
                String insertLoginSQL = "INSERT INTO customer_login (customer_id, email, password_hash, is_active) VALUES (?, ?, ?, ?)";
                loginStmt = con.prepareStatement(insertLoginSQL);
                loginStmt.setInt(1, customerId);
                loginStmt.setString(2, ((String) regData.get("email")).trim());
                loginStmt.setString(3, hashedPassword);
                loginStmt.setBoolean(4, true);
                
                loginStmt.executeUpdate();
                
                con.commit();
                return true;
                
            } catch (Exception e) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
                return false;
                
            } finally {
                try {
                    if (generatedKeys != null) generatedKeys.close();
                    if (customerStmt != null) customerStmt.close();
                    if (loginStmt != null) loginStmt.close();
                    if (con != null) con.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }
    
    // GET /api/pets - Get all available pets
    static class PetsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("📋 GET /api/pets - Fetching all pets");
            
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Map<String, Object>> pets = new ArrayList<>();
                
                try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String sql = "SELECT * FROM pet WHERE is_adopted = FALSE ORDER BY pet_id";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    
                    while (rs.next()) {
                        Map<String, Object> pet = new HashMap<>();
                        pet.put("pet_id", rs.getInt("pet_id"));
                        pet.put("pet_name", rs.getString("pet_name"));
                        pet.put("pet_type", rs.getString("pet_type"));
                        pet.put("breed", rs.getString("breed"));
                        pet.put("age", rs.getString("age"));
                        pet.put("date_of_rescue", rs.getDate("date_of_rescue"));
                        pets.add(pet);
                    }
                    
                    System.out.println("✅ Found " + pets.size() + " pets available for adoption");
                    String jsonResponse = gson.toJson(pets);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error fetching pets: " + e.getMessage());
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Error fetching pets: " + e.getMessage());
                }
            }
        }
    }
    
    // POST /api/pets/add - Add new pet (Admin)
    static class AddPetHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("🐕 POST /api/pets/add - Add pet request received");
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStream is = exchange.getRequestBody();
                    String requestBody = new String(is.readAllBytes());
                    
                    Map<String, Object> petData = gson.fromJson(requestBody, Map.class);
                    
                    System.out.println("🐕 New pet registration:");
                    System.out.println("  Pet Name: " + petData.get("pet_name"));
                    System.out.println("  Pet Type: " + petData.get("pet_type"));
                    System.out.println("  Breed: " + petData.get("breed"));
                    System.out.println("  Age: " + petData.get("age"));
                    
                    boolean success = insertPetToDatabase(petData);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", success);
                    response.put("message", success ? "Pet added successfully" : "Failed to add pet");
                    
                    String jsonResponse = gson.toJson(response);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error adding pet: " + e.getMessage());
                    sendErrorResponse(exchange, "Error adding pet: " + e.getMessage());
                }
            }
        }
        
        private boolean insertPetToDatabase(Map<String, Object> petData) {
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO pet (pet_name, pet_type, breed, age, date_of_rescue, is_adopted) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                
                pstmt.setString(1, petData.get("pet_name").toString());
                pstmt.setString(2, petData.get("pet_type").toString());
                pstmt.setString(3, petData.get("breed").toString());
                pstmt.setString(4, petData.get("age").toString());
                pstmt.setDate(5, java.sql.Date.valueOf(petData.get("date_of_rescue").toString()));
                pstmt.setBoolean(6, false); // Initially not adopted
                
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("✅ Pet inserted: " + rowsAffected + " rows affected");
                
                return rowsAffected > 0;
                
            } catch (Exception e) {
                System.err.println("❌ Database error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }
    
    // POST /api/pets/adopt - Adopt a pet
    static class AdoptPetHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("🐾 POST /api/pets/adopt - Pet adoption request");
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStream is = exchange.getRequestBody();
                    String requestBody = new String(is.readAllBytes());
                    
                    Map<String, Object> adoptionData = gson.fromJson(requestBody, Map.class);
                    
                    int petId = Integer.parseInt(adoptionData.get("pet_id").toString());
                    int customerId = Integer.parseInt(adoptionData.get("customer_id").toString());
                    String adoptionDate = adoptionData.get("adoption_date").toString();
                    
                    System.out.println("🐾 Adoption: Customer " + customerId + " adopting Pet " + petId);
                    
                    boolean success = processAdoption(petId, customerId, adoptionDate);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", success);
                    response.put("message", success ? "Pet adopted successfully!" : "Failed to process adoption");
                    
                    if (success) {
                        response.put("adoption_id", "A" + System.currentTimeMillis() % 10000);
                    }
                    
                    String jsonResponse = gson.toJson(response);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error processing adoption: " + e.getMessage());
                    sendErrorResponse(exchange, "Error processing adoption");
                }
            }
        }
        
        private boolean processAdoption(int petId, int customerId, String adoptionDate) {
            Connection con = null;
            try {
                con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                con.setAutoCommit(false);
                
                // Insert into adopts table
                String insertAdoptSql = "INSERT INTO adopts (customer_id, pet_id, adoption_date) VALUES (?, ?, ?)";
                PreparedStatement adoptStmt = con.prepareStatement(insertAdoptSql);
                adoptStmt.setInt(1, customerId);
                adoptStmt.setInt(2, petId);
                adoptStmt.setDate(3, java.sql.Date.valueOf(adoptionDate));
                adoptStmt.executeUpdate();
                
                // Update pet as adopted
                String updatePetSql = "UPDATE pet SET is_adopted = TRUE WHERE pet_id = ?";
                PreparedStatement updateStmt = con.prepareStatement(updatePetSql);
                updateStmt.setInt(1, petId);
                updateStmt.executeUpdate();
                
                con.commit();
                System.out.println("✅ Adoption processed successfully");
                return true;
                
            } catch (Exception e) {
                try {
                    if (con != null) con.rollback();
                } catch (SQLException ex) {}
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (con != null) con.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    // GET /api/products - Get all products
    static class ProductsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("📦 GET /api/products - Fetching all products");
            
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Map<String, Object>> products = new ArrayList<>();
                
                try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String sql = "SELECT * FROM product ORDER BY product_id";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    
                    while (rs.next()) {
                        Map<String, Object> product = new HashMap<>();
                        product.put("product_id", rs.getInt("product_id"));
                        product.put("product_name", rs.getString("product_name"));
                        product.put("type", rs.getString("type"));
                        product.put("used_by_pet", rs.getString("used_by_pet"));
                        product.put("cost", rs.getDouble("cost"));
                        product.put("quantity", rs.getInt("quantity"));
                        products.add(product);
                    }
                    
                    System.out.println("✅ Found " + products.size() + " products in database");
                    String jsonResponse = gson.toJson(products);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error fetching products: " + e.getMessage());
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Error fetching products: " + e.getMessage());
                }
            }
        }
    }
    
    // POST /api/products/add - Add new product (Admin)
    static class AddProductHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("🛠️ POST /api/products/add - Add product request received");
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStream is = exchange.getRequestBody();
                    String requestBody = new String(is.readAllBytes());
                    
                    System.out.println("📄 Raw request body: " + requestBody);
                    
                    if (requestBody.isEmpty()) {
                        System.err.println("❌ Request body is empty!");
                        sendErrorResponse(exchange, "Request body is empty");
                        return;
                    }
                    
                    Map<String, Object> productData = gson.fromJson(requestBody, Map.class);
                    
                    System.out.println("🔍 Parsed product data:");
                    System.out.println("  Product Name: " + productData.get("product_name"));
                    System.out.println("  Type: " + productData.get("type"));
                    System.out.println("  Cost: " + productData.get("cost"));
                    System.out.println("  Quantity: " + productData.get("quantity"));
                    
                    boolean success = insertProductToDatabase(productData);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", success);
                    response.put("message", success ? "Product added successfully" : "Failed to add product");
                    
                    String jsonResponse = gson.toJson(response);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error adding product: " + e.getMessage());
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Error adding product: " + e.getMessage());
                }
            }
        }
        
        private boolean insertProductToDatabase(Map<String, Object> productData) {
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO product (product_name, type, used_by_pet, cost, quantity) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                
                pstmt.setString(1, productData.get("product_name").toString());
                pstmt.setString(2, productData.get("type").toString());
                pstmt.setString(3, productData.get("used_by_pet") != null ? 
                              productData.get("used_by_pet").toString() : "Both");
                pstmt.setDouble(4, Double.parseDouble(productData.get("cost").toString()));
                pstmt.setInt(5, (int) Double.parseDouble(productData.get("quantity").toString()));
                
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("✅ Product inserted: " + rowsAffected + " rows affected");
                
                return rowsAffected > 0;
                
            } catch (Exception e) {
                System.err.println("❌ Database error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }
    
    // POST /api/products/buy - Buy product
    static class BuyProductHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("🛒 POST /api/products/buy - Product purchase request");
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStream is = exchange.getRequestBody();
                    String requestBody = new String(is.readAllBytes());
                    
                    Map<String, Object> purchaseData = gson.fromJson(requestBody, Map.class);
                    
                    int productId = Integer.parseInt(purchaseData.get("product_id").toString());
                    int customerId = Integer.parseInt(purchaseData.get("customer_id").toString());
                    int quantity = Integer.parseInt(purchaseData.get("quantity").toString());
                    String orderDate = purchaseData.get("order_date").toString();
                    
                    System.out.println("🛒 Purchase: Customer " + customerId + " buying " + quantity + " of Product " + productId);
                    
                    boolean success = processPurchase(productId, customerId, quantity, orderDate);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", success);
                    response.put("message", success ? "Product purchased successfully!" : "Failed to process purchase");
                    
                    if (success) {
                        response.put("order_id", "O" + System.currentTimeMillis() % 10000);
                    }
                    
                    String jsonResponse = gson.toJson(response);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error processing purchase: " + e.getMessage());
                    sendErrorResponse(exchange, "Error processing purchase");
                }
            }
        }
        
        private boolean processPurchase(int productId, int customerId, int quantity, String orderDate) {
            Connection con = null;
            try {
                con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                con.setAutoCommit(false);
                
                // Check product availability
                String checkSql = "SELECT cost, quantity FROM product WHERE product_id = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkSql);
                checkStmt.setInt(1, productId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (!rs.next()) {
                    System.err.println("❌ Product not found: " + productId);
                    return false;
                }
                
                double cost = rs.getDouble("cost");
                int availableQuantity = rs.getInt("quantity");
                
                if (availableQuantity < quantity) {
                    System.err.println("❌ Insufficient stock. Available: " + availableQuantity + ", Requested: " + quantity);
                    return false;
                }
                
                double totalAmount = cost * quantity;
                
                // Insert order
                String insertOrderSql = "INSERT INTO orders (customer_id, product_id, quantity, order_date, total_amount, status) VALUES (?, ?, ?, ?, ?, 'completed')";
                PreparedStatement orderStmt = con.prepareStatement(insertOrderSql);
                orderStmt.setInt(1, customerId);
                orderStmt.setInt(2, productId);
                orderStmt.setInt(3, quantity);
                orderStmt.setDate(4, java.sql.Date.valueOf(orderDate));
                orderStmt.setDouble(5, totalAmount);
                orderStmt.executeUpdate();
                
                // Update product quantity
                String updateProductSql = "UPDATE product SET quantity = quantity - ? WHERE product_id = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateProductSql);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, productId);
                updateStmt.executeUpdate();
                
                con.commit();
                System.out.println("✅ Purchase processed successfully. Total: $" + totalAmount);
                return true;
                
            } catch (Exception e) {
                try {
                    if (con != null) con.rollback();
                } catch (SQLException ex) {}
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (con != null) con.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    // GET /api/customers - Get all customers (Admin)
    static class CustomersHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("👥 GET /api/customers - Fetching all customers");
            
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Map<String, Object>> customers = new ArrayList<>();
                
                try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String sql = "SELECT * FROM customer ORDER BY customer_id";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    
                    while (rs.next()) {
                        Map<String, Object> customer = new HashMap<>();
                        customer.put("customer_id", rs.getInt("customer_id"));
                        customer.put("customer_name", rs.getString("customer_name"));
                        customer.put("address", rs.getString("address"));
                        customer.put("email_address", rs.getString("email_address"));
                        customer.put("mobile_number", rs.getString("mobile_number"));
                        customers.add(customer);
                    }
                    
                    System.out.println("✅ Found " + customers.size() + " customers in database");
                    String jsonResponse = gson.toJson(customers);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error fetching customers: " + e.getMessage());
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Error fetching customers: " + e.getMessage());
                }
            }
        }
    }
    
    // GET /api/orders - Get all orders
    static class OrdersHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("📋 GET /api/orders - Fetching all orders");
            
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Map<String, Object>> orders = new ArrayList<>();
                
                try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String sql = "SELECT o.*, c.customer_name, p.product_name " +
                               "FROM orders o " +
                               "JOIN customer c ON o.customer_id = c.customer_id " +
                               "JOIN product p ON o.product_id = p.product_id " +
                               "ORDER BY o.order_date DESC";
                    
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    
                    while (rs.next()) {
                        Map<String, Object> order = new HashMap<>();
                        order.put("order_id", rs.getInt("order_id"));
                        order.put("customer_id", rs.getInt("customer_id"));
                        order.put("customer_name", rs.getString("customer_name"));
                        order.put("product_id", rs.getInt("product_id"));
                        order.put("product_name", rs.getString("product_name"));
                        order.put("quantity", rs.getInt("quantity"));
                        order.put("order_date", rs.getDate("order_date"));
                        order.put("total_amount", rs.getDouble("total_amount"));
                        order.put("status", rs.getString("status"));
                        orders.add(order);
                    }
                    
                    System.out.println("✅ Found " + orders.size() + " orders in database");
                    String jsonResponse = gson.toJson(orders);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error fetching orders: " + e.getMessage());
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Error fetching orders: " + e.getMessage());
                }
            }
        }
    }
    
    // GET /api/admin/sales - Get sales report (Admin)
    static class SalesReportHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("📊 GET /api/admin/sales - Generating sales report");
            
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, Object> salesReport = new HashMap<>();
                
                try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    // Total sales amount
                    String totalSalesSql = "SELECT SUM(total_amount) as total_sales FROM orders";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(totalSalesSql);
                    
                    double totalSales = 0;
                    if (rs.next()) {
                        totalSales = rs.getDouble("total_sales");
                    }
                    
                    // Total orders count
                    String totalOrdersSql = "SELECT COUNT(*) as total_orders FROM orders";
                    rs = stmt.executeQuery(totalOrdersSql);
                    int totalOrders = 0;
                    if (rs.next()) {
                        totalOrders = rs.getInt("total_orders");
                    }
                    
                    // Total customers
                    String totalCustomersSql = "SELECT COUNT(*) as total_customers FROM customer";
                    rs = stmt.executeQuery(totalCustomersSql);
                    int totalCustomers = 0;
                    if (rs.next()) {
                        totalCustomers = rs.getInt("total_customers");
                    }
                    
                    // Total pets adopted
                    String adoptedPetsSql = "SELECT COUNT(*) as adopted_pets FROM adopts";
                    rs = stmt.executeQuery(adoptedPetsSql);
                    int adoptedPets = 0;
                    if (rs.next()) {
                        adoptedPets = rs.getInt("adopted_pets");
                    }
                    
                    salesReport.put("total_sales", totalSales);
                    salesReport.put("total_orders", totalOrders);
                    salesReport.put("total_customers", totalCustomers);
                    salesReport.put("adopted_pets", adoptedPets);
                    salesReport.put("report_generated", new java.util.Date().toString());
                    salesReport.put("status", "success");
                    
                    System.out.println("✅ Sales report generated successfully");
                    String jsonResponse = gson.toJson(salesReport);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error generating sales report: " + e.getMessage());
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Error generating sales report: " + e.getMessage());
                }
            }
        }
    }
    
    // GET /api/admin/adopted-pets - Get adopted pets report (Admin)
    static class AdoptedPetsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("🏠 GET /api/admin/adopted-pets - Fetching adopted pets report");
            
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Map<String, Object>> adoptedPets = new ArrayList<>();
                
                try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String sql = "SELECT a.*, c.customer_name, p.pet_name, p.pet_type, p.breed " +
                               "FROM adopts a " +
                               "JOIN customer c ON a.customer_id = c.customer_id " +
                               "JOIN pet p ON a.pet_id = p.pet_id " +
                               "ORDER BY a.adoption_date DESC";
                    
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    
                    while (rs.next()) {
                        Map<String, Object> adoption = new HashMap<>();
                        adoption.put("adoption_id", rs.getInt("adoption_id"));
                        adoption.put("customer_id", rs.getInt("customer_id"));
                        adoption.put("customer_name", rs.getString("customer_name"));
                        adoption.put("pet_id", rs.getInt("pet_id"));
                        adoption.put("pet_name", rs.getString("pet_name"));
                        adoption.put("pet_type", rs.getString("pet_type"));
                        adoption.put("breed", rs.getString("breed"));
                        adoption.put("adoption_date", rs.getDate("adoption_date"));
                        adoptedPets.add(adoption);
                    }
                    
                    System.out.println("✅ Found " + adoptedPets.size() + " adopted pets");
                    String jsonResponse = gson.toJson(adoptedPets);
                    sendJsonResponse(exchange, jsonResponse);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error fetching adopted pets: " + e.getMessage());
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Error fetching adopted pets: " + e.getMessage());
                }
            }
        }
    }
    
    // Remaining handler stubs
    static class AddCustomerHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            
            System.out.println("👤 POST /api/customers/add - Add customer (legacy endpoint)");
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Use /api/customers/register for new registrations");
            response.put("success", false);
            
            sendJsonResponse(exchange, gson.toJson(response));
        }
    }
    
    static class SpecificPetHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            System.out.println("🔍 GET /api/pets/specific - Specific pet lookup");
            sendJsonResponse(exchange, "{\"message\":\"Specific pet lookup endpoint\"}");
        }
    }
    
    static class UpdatePriceHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            System.out.println("💰 POST /api/products/update-price - Update price");
            sendJsonResponse(exchange, "{\"message\":\"Update price endpoint\"}");
        }
    }
    
    static class CustomerHistoryHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            System.out.println("📜 GET /api/customers/history - Customer history");
            sendJsonResponse(exchange, "{\"message\":\"Customer history endpoint\"}");
        }
    }
    
    static class OrderHistoryHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            new CorsHandler().handle(exchange);
            System.out.println("📚 GET /api/orders/history - Order history");
            sendJsonResponse(exchange, "{\"message\":\"Order history endpoint\"}");
        }
    }
    
    // Utility methods
    private static String extractValue(String line) {
        int colonIndex = line.indexOf(":");
        if (colonIndex != -1 && colonIndex < line.length() - 1) {
            return line.substring(colonIndex + 1).trim();
        }
        return "";
    }
    
    private static void sendJsonResponse(HttpExchange exchange, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonResponse.getBytes());
        os.close();
    }
    
    private static void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", errorMessage);
        String jsonResponse = gson.toJson(error);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(500, jsonResponse.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonResponse.getBytes());
        os.close();
    }
}
