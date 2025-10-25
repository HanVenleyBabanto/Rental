package Config;

import java.sql.*;

public class config {
    public static Connection con = null;

    public static Connection connectDB() {
        if (con == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection("jdbc:sqlite:rental.db");
                System.out.println("Connection Successful");
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Connection Failed: " + e);
            }
        }
        return con;
    }

    public static void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                con = null;
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public void addRecord(String sql, Object... values) {
        try (PreparedStatement pstmt = connectDB().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }

    public void updateRecord(String sql, Object... values) {
        try (PreparedStatement pstmt = connectDB().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Record updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }

    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (PreparedStatement pstmt = connectDB().prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            StringBuilder headerLine = new StringBuilder();
            headerLine.append("--------------------------------------------------------------------------------\n| ");
            for (String header : columnHeaders) {
                headerLine.append(String.format("%-20s | ", header));
            }
            headerLine.append("\n--------------------------------------------------------------------------------");
            System.out.println(headerLine.toString());

            while (rs.next()) {
                StringBuilder row = new StringBuilder("| ");
                for (String colName : columnNames) {
                    String value = rs.getString(colName);
                    row.append(String.format("%-20s | ", value != null ? value : ""));
                }
                System.out.println(row.toString());
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    public void deleteRecord(String sql, Object... values) {
        try (PreparedStatement pstmt = connectDB().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Record deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    public java.util.List<java.util.Map<String, Object>> fetchRecords(String sqlQuery, Object... values) {
        java.util.List<java.util.Map<String, Object>> records = new java.util.ArrayList<>();

        try (PreparedStatement pstmt = connectDB().prepareStatement(sqlQuery)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                records.add(row);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching records: " + e.getMessage());
        }

        return records;
    }

    public String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }

    // ✅ Fixed insertRecord method to add houses
    public void insertRecord(String sql, Object... values) {
        try (PreparedStatement pstmt = connectDB().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Record inserted successfully!");
        } catch (SQLException e) {
            System.out.println("Error inserting record: " + e.getMessage());
        }
    }
    
 public void viewMyBookings(String sqlQuery, String[] bookingHeaders, String[] bookingColumns) {
        if (bookingHeaders.length != bookingColumns.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (PreparedStatement pstmt = connectDB().prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            StringBuilder headerLine = new StringBuilder();
            headerLine.append("--------------------------------------------------------------------------------\n| ");
            for (String header : bookingHeaders) {
                headerLine.append(String.format("%-20s | ", header));
            }
            headerLine.append("\n--------------------------------------------------------------------------------");
            System.out.println(headerLine.toString());

            while (rs.next()) {
                StringBuilder row = new StringBuilder("| ");
                for (String colName : bookingColumns) {
                    String value = rs.getString(colName);
                    row.append(String.format("%-20s | ", value != null ? value : ""));
                }
                System.out.println(row.toString());
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

   }
