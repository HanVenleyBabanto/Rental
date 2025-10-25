package rentalh;

import Config.config;
import java.util.Scanner;

public class Admin {

    Scanner sc = new Scanner(System.in);
    config database = new config();

    public void Admin() {

        int respo;
        do {
            System.out.println("\n=== WELCOME TO ADMIN DASHBOARD ===");
            System.out.println("1. Approve Customer Accounts");
            System.out.println("2. View All Customers");
            System.out.println("3. Manage Properties (Add / Update / Remove)");
            System.out.println("4. View Available Houses");
            System.out.println("5. View Reservations / Bookings");
            System.out.println("6. View or Update Payment Status");
            System.out.println("7. Logout / Exit");
            System.out.print("Enter your choice: ");

            respo = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (respo) {
                case 1:
                    viewUsers();
                    System.out.print("Enter ID to Approve: ");
                    int ids = sc.nextInt();
                    sc.nextLine();
                    String sql = "UPDATE tbl_users SET Status = ? WHERE ID = ?";
                    database.updateRecord(sql, "Approved", ids);
                    System.out.println("✅ User approved successfully!");
                    break;

                case 2:
                    System.out.println("\n--- All Customers ---");
                    String allCustomersQuery = "SELECT * FROM tbl_users";
                    String[] customerHeaders = {"ID", "Name", "Email", "Role", "Cnumber", "Status"};
                    String[] customerColumns = {"ID", "Name", "Email", "Role", "Cnumber", "Status"};
                    database.viewRecords(allCustomersQuery, customerHeaders, customerColumns);
                    break;

                case 3:
                    System.out.println("\n--- Manage Properties ---");
                    System.out.println("1. Add Property");
                    System.out.println("2. Update Property");
                    System.out.println("3. Remove Property");
                    System.out.print("Choose action: ");
                    int propChoice = sc.nextInt();
                    sc.nextLine();
                    switch (propChoice) {
                        case 1:
                            System.out.print("Enter Property Address: ");
                            String address = sc.nextLine();
                            System.out.print("Enter Rent Amount: ");
                            double rent = sc.nextDouble();
                            sc.nextLine();
                            String insertProperty = "INSERT INTO tbl_houses (Address, Rent, Status) VALUES (?, ?, ?)";
                            database.insertRecord(insertProperty, address, rent, "Available");
                            System.out.println("✅ Property added successfully!");
                            break;
                        case 2:
                            System.out.print("Enter Property ID to Update: ");
                            int propId = sc.nextInt();
                            sc.nextLine();
                            System.out.print("Enter New Rent Amount: ");
                            double newRent = sc.nextDouble();
                            sc.nextLine();
                            String updateProperty = "UPDATE tbl_houses SET Rent = ? WHERE ID = ?";
                            database.updateRecord(updateProperty, newRent, propId);
                            System.out.println("✅ Property updated successfully!");
                            break;
                        case 3:
                            System.out.print("Enter Property ID to Remove: ");
                            int removeId = sc.nextInt();
                            sc.nextLine();
                            String deleteProperty = "DELETE FROM tbl_houses WHERE ID = ?";
                            database.updateRecord(deleteProperty, removeId);
                            System.out.println("✅ Property removed successfully!");
                            break;
                        default:
                            System.out.println("❌ Invalid choice!");
                    }
                    break;

                case 4:
                    System.out.println("\n--- Available Houses ---");
                    String availableHouses = "SELECT * FROM tbl_houses WHERE Status = 'Available'";
                    String[] houseHeaders = {"ID", "Address", "Rent", "Status"};
                    String[] houseColumns = {"ID", "Address", "Rent", "Status"};
                    database.viewRecords(availableHouses, houseHeaders, houseColumns);
                    break;

                case 5:
                    System.out.println("\n--- Reservations / Bookings ---");
                    String bookingsQuery = "SELECT r.ID, u.Name AS Customer, h.Address AS House, r.Status " +
                            "FROM tbl_reservations r " +
                            "JOIN tbl_users u ON r.UserID = u.ID " +
                            "JOIN tbl_houses h ON r.HouseID = h.ID";
                    String[] bookingHeaders = {"ID", "Customer", "House", "Status"};
                    String[] bookingColumns = {"ID", "Customer", "House", "Status"};
                    database.viewRecords(bookingsQuery, bookingHeaders, bookingColumns);
                    break;

                case 6:
                    System.out.println("\n--- Payment Status ---");
                    String paymentQuery = "SELECT p.ID, u.Name AS Customer, h.Address AS House, p.Amount, p.Status " +
                            "FROM tbl_payments p " +
                            "JOIN tbl_users u ON p.UserID = u.ID " +
                            "JOIN tbl_houses h ON p.HouseID = h.ID";
                    String[] paymentHeaders = {"ID", "Customer", "House", "Amount", "Status"};
                    String[] paymentColumns = {"ID", "Customer", "House", "Amount", "Status"};
                    database.viewRecords(paymentQuery, paymentHeaders, paymentColumns);

                    System.out.print("Enter Payment ID to Update Status: ");
                    int payId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter new Status (Paid / Pending): ");
                    String payStatus = sc.nextLine();
                    String updatePayment = "UPDATE tbl_payments SET Status = ? WHERE ID = ?";
                    database.updateRecord(updatePayment, payStatus, payId);
                    System.out.println("✅ Payment status updated successfully!");
                    break;

                case 7:
                    System.out.println("👋 Logging out...");
                    break;

                default:
                    System.out.println("❌ Invalid choice!");
            }

        } while (respo != 7); // loop until logout
    }

    public static void viewUsers() {
        String votersQuery = "SELECT * FROM tbl_users";
        String[] votersHeaders = {"ID", "Name", "Email", "Role", "Cnumber", "Status"};
        String[] votersColumns = {"ID", "Name", "Email", "Role", "Cnumber", "Status"};

        config conf = new config();
        conf.viewRecords(votersQuery, votersHeaders, votersColumns);
    }
}
