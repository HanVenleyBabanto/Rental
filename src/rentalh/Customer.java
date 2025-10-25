package rentalh;

import Config.config;
import java.util.Scanner;

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
                    
                case 4:
                    System.out.println("\n--- Make Payment ---");
                    System.out.print("Enter Reservation ID to pay: ");
                    int resvId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Payment Amount: ");
                    double amount = sc.nextDouble();
                    sc.nextLine();
                    String insertPayment = "INSERT INTO tbl_payments (UserID, HouseID, Amount, Status) " +
                                           "SELECT UserID, HouseID, ?, 'Pending' FROM tbl_reservations WHERE ID = ?";
                    database.insertRecord(insertPayment, amount, resvId);
                    System.out.println("✅ Payment recorded! Awaiting confirmation.");
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
}
