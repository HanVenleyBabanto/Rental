package rentalh;

import Config.config;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public final class Customer {

    Scanner sc = new Scanner(System.in);
    config database = new config();
    int userId; // Logged-in customer ID

    // ✅ Constructor that receives the customer ID
    public Customer(int userId) {
        this.userId = userId;
        customerMenu();
    }

    public void customerMenu() {
        int respo;
        do {
            System.out.println("\n=== WELCOME TO CUSTOMER DASHBOARD ===");
            System.out.println("1. View Available Houses");
            System.out.println("2. Make a Booking");
            System.out.println("3. View My Bookings");
            System.out.println("4. Make Payments");
            System.out.println("5. Request Maintenance");
            System.out.println("6. Logout / Exit");
            System.out.print("Enter your choice: ");

            respo = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (respo) {
                case 1:
                    System.out.println("\n--- Available Houses ---");
                    String availableHouses = "SELECT * FROM tbl_houses WHERE Status = 'Available'";
                    String[] houseHeaders = {"ID", "Address", "Rent", "Status"};
                    String[] houseColumns = {"ID", "Address", "Rent", "Status"};
                    database.viewRecords(availableHouses, houseHeaders, houseColumns);
                    break;

                case 2:
                    System.out.println("\n--- Make a Booking ---");
                    System.out.print("Enter House ID to book: ");
                    int houseId = sc.nextInt();
                    sc.nextLine();
                    String insertBooking = "INSERT INTO tbl_reservations (UserID, HouseID, Status) VALUES (?, ?, ?)";
                    database.insertRecord(insertBooking, userId, houseId, "Pending");
                    System.out.println("✅ Booking request submitted!");
                    break;

                case 3:
                    viewMyBookings();
                    break;

                case 4:
                    makePayment();
                    break;

                case 5:
                    System.out.println("\n--- Request Maintenance ---");
                    System.out.print("Enter House ID for maintenance request: ");
                    int houseReqId = sc.nextInt();
                    sc.nextLine();
                    String maintenanceSql = "INSERT INTO tbl_maintenance (UserID, HouseID, Status) VALUES (?, ?, ?)";
                    database.insertRecord(maintenanceSql, userId, houseReqId, "Pending");
                    System.out.println("✅ Maintenance request submitted!");
                    break;

                case 6:
                    System.out.println("👋 Logging out...");
                    break;

                default:
                    System.out.println("❌ Invalid choice!");
            }

        } while (respo != 6);
    }

    private void viewMyBookings() {
        System.out.println("\n--- My Bookings ---");
        
        // Try different column names for the date
        String myBookings = "SELECT r.ID, h.Address, h.Rent, r.Status, " +
                           "COALESCE(r.BookingDate, r.Date, r.CreatedDate, 'N/A') as BookingDate " +
                           "FROM tbl_reservations r " +
                           "JOIN tbl_houses h ON r.HouseID = h.ID " +
                           "WHERE r.UserID = ?";
        
        String[] bookingHeaders = {"Reservation ID", "Address", "Rent", "Status", "Booking Date"};
        String[] bookingColumns = {"ID", "Address", "Rent", "Status", "BookingDate"};
        
        // Use fetchRecords for parameterized query
        List<Map<String, Object>> bookings = database.fetchRecords(myBookings, userId);
        
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        
        // Display bookings manually
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("| ");
        for (String header : bookingHeaders) {
            System.out.printf("%-20s | ", header);
        }
        System.out.println("\n--------------------------------------------------------------------------------");
        
        for (Map<String, Object> booking : bookings) {
            System.out.print("| ");
            for (String column : bookingColumns) {
                Object value = booking.get(column);
                System.out.printf("%-20s | ", value != null ? value.toString() : "");
            }
            System.out.println();
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private void makePayment() {
        System.out.println("\n--- Make Payment ---");
        
        // First, show user's pending reservations
        System.out.println("\nYour Pending Reservations:");
        String pendingReservations = "SELECT r.ID, h.Address, h.Rent " +
                                   "FROM tbl_reservations r " +
                                   "JOIN tbl_houses h ON r.HouseID = h.ID " +
                                   "WHERE r.UserID = ? AND r.Status = 'Pending'";
        
        List<Map<String, Object>> reservations = database.fetchRecords(pendingReservations, userId);
        
        if (reservations.isEmpty()) {
            System.out.println("No pending reservations found.");
            return;
        }
        
        // Display reservations
        System.out.println("--------------------------------------------");
        System.out.println("| Reservation ID | Address         | Rent  |");
        System.out.println("--------------------------------------------");
        for (Map<String, Object> reservation : reservations) {
            System.out.printf("| %-14s | %-15s | %-5s |\n", 
                reservation.get("ID"), 
                reservation.get("Address"), 
                reservation.get("Rent"));
        }
        System.out.println("--------------------------------------------");

        System.out.print("Enter Reservation ID to pay: ");
        int resvId = sc.nextInt();
        sc.nextLine();

        // Get the reservation amount
        double amount = getReservationAmount(resvId);
        if (amount == -1) {
            System.out.println("❌ Invalid Reservation ID or reservation not found!");
            return;
        }

        System.out.printf("Payment Amount: $%.2f%n", amount);

        // Payment method selection
        System.out.println("\n--- Select Payment Method ---");
        System.out.println("1. Cash");
        System.out.println("2. Credit/Debit Card");
        System.out.println("3. E-Wallet");
        System.out.print("Choose payment method (1-3): ");
        
        int paymentMethod = sc.nextInt();
        sc.nextLine();

        String paymentType = "";
        String paymentDetails = "";

        switch (paymentMethod) {
            case 1: // Cash
                paymentType = "Cash";
                System.out.println("💵 Please prepare cash for the agent.");
                paymentDetails = "Cash payment - to be collected by agent";
                break;

            case 2: // Card
                paymentType = "Card";
                System.out.print("Enter card number: ");
                String cardNumber = sc.nextLine();
                System.out.print("Enter cardholder name: ");
                String cardName = sc.nextLine();
                System.out.print("Enter expiry date (MM/YY): ");
                String expiryDate = sc.nextLine();
                System.out.print("Enter CVV: ");
                String cvv = sc.nextLine();
                
                // Mask card number for security
                String maskedCard = "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
                paymentDetails = "Card: " + maskedCard + ", Name: " + cardName;
                System.out.println("✅ Card payment processed successfully!");
                break;

            case 3: // E-Wallet
                paymentType = "E-Wallet";
                System.out.println("Select E-Wallet Provider:");
                System.out.println("1. PayPal");
                System.out.println("2. GCash");
                System.out.println("3. PayMaya"); // Fixed typo from PayWaya
                System.out.println("4. GrabPay");
                System.out.print("Choose provider (1-4): ");
                
                int walletChoice = sc.nextInt();
                sc.nextLine();
                
                String provider = "";
                switch (walletChoice) {
                    case 1: provider = "PayPal"; break;
                    case 2: provider = "GCash"; break;
                    case 3: provider = "PayMaya"; break;
                    case 4: provider = "GrabPay"; break;
                    default: 
                        System.out.println("❌ Invalid provider selection!");
                        return;
                }
                
                System.out.print("Enter " + provider + " account/phone number: ");
                String walletAccount = sc.nextLine();
                paymentDetails = provider + " - " + walletAccount;
                System.out.println("✅ " + provider + " payment initiated!"); // Fixed the checkmark
                break;

            default:
                System.out.println("❌ Invalid payment method!");
                return;
        }

        // Check if tbl_payments has ReservationID column, if not use alternative
        try {
            // Try with ReservationID first
            String insertPayment = "INSERT INTO tbl_payments (UserID, ReservationID, Amount, PaymentMethod, PaymentDetails, Status) " +
                                 "VALUES (?, ?, ?, ?, ?, 'Completed')";
            database.insertRecord(insertPayment, userId, resvId, amount, paymentType, paymentDetails);
        } catch (Exception e) {
            // If ReservationID doesn't exist, try with HouseID instead
            System.out.println("Note: Using HouseID for payment record...");
            int houseId = getHouseIdFromReservation(resvId);
            String insertPayment = "INSERT INTO tbl_payments (UserID, HouseID, Amount, PaymentMethod, PaymentDetails, Status) " +
                                 "VALUES (?, ?, ?, ?, ?, 'Completed')";
            database.insertRecord(insertPayment, userId, houseId, amount, paymentType, paymentDetails);
        }

        // Update reservation status
        String updateReservation = "UPDATE tbl_reservations SET Status = 'Confirmed' WHERE ID = ? AND UserID = ?";
        database.updateRecord(updateReservation, resvId, userId);

        System.out.println("✅ Payment completed successfully!");
        System.out.println("📄 Payment Method: " + paymentType);
        System.out.printf("💰 Amount Paid: $%.2f%n", amount);
        System.out.println("📋 Reservation ID: " + resvId + " has been confirmed!");
    }

    private double getReservationAmount(int reservationId) {
        try {
            String query = "SELECT h.Rent FROM tbl_reservations r " +
                          "JOIN tbl_houses h ON r.HouseID = h.ID " +
                          "WHERE r.ID = ? AND r.UserID = ?";
            
            List<Map<String, Object>> result = database.fetchRecords(query, reservationId, userId);
            
            if (!result.isEmpty()) {
                Object rentValue = result.get(0).get("Rent");
                if (rentValue != null) {
                    return Double.parseDouble(rentValue.toString());
                }
            }
            return -1;
        } catch (Exception e) {
            System.out.println("Error getting reservation amount: " + e.getMessage());
            return -1;
        }
    }

    private int getHouseIdFromReservation(int reservationId) {
        try {
            String query = "SELECT HouseID FROM tbl_reservations WHERE ID = ? AND UserID = ?";
            List<Map<String, Object>> result = database.fetchRecords(query, reservationId, userId);
            
            if (!result.isEmpty()) {
                Object houseId = result.get(0).get("HouseID");
                if (houseId != null) {
                    return Integer.parseInt(houseId.toString());
                }
            }
            return -1;
        } catch (Exception e) {
            System.out.println("Error getting HouseID: " + e.getMessage());
            return -1;
        }
    }
}