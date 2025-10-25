package rentalh;

import Config.config;
import java.util.Scanner;
import static rentalh.Admin.viewUsers;

public class RentalH {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            config database = new config();
            config.connectDB();
            char cont = 0;
            int choice;

            do {
                System.out.println("\nHOUSE RENTAL RECORDING SYSTEM");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. View Users");
                System.out.println("4. Update User");
                System.out.println("5. Delete User");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter Email: ");
                        String email = sc.nextLine();
                        System.out.print("Enter Password: ");
                        String pass = sc.nextLine();

                        String hashPass = database.hashPassword(pass);

                        while (true) {
                            String qry = "SELECT * FROM tbl_users WHERE Email = ? AND Password = ?";
                            java.util.List<java.util.Map<String, Object>> result = database.fetchRecords(qry, email, hashPass);

                            if (result.isEmpty()) {
                                System.out.println("INVALID CREDENTIALS");
                                break;
                            } else {
                                java.util.Map<String, Object> user = result.get(0);
                                String stat = user.get("Status").toString();
                                String type = user.get("Role").toString();

                                if (stat.equalsIgnoreCase("Pending")) {
                                    System.out.println("Account is Pending, Contact the Admin!");
                                    break;
                                } else {
                                    System.out.println("LOGIN SUCCESS!");

                                    if (type.equalsIgnoreCase("Admin")) {
                                        Admin admin = new Admin();
                                        admin.Admin();
                                        break;
                                    } else if (type.equalsIgnoreCase("Customer")) {
                                        // ✅ FIXED: Pass userId to Customer constructor
                                        int userId = Integer.parseInt(user.get("ID").toString());
                                        Customer customer = new Customer(userId);
                                    }
                                    break;
                                }
                            }
                        }
                        break;

                    case 2:
                        config con = new config();

                        System.out.print("Enter Your Name: ");
                        String name = sc.next();

                        String regEmail;
                        while (true) {
                            System.out.print("Enter User Email: ");
                            regEmail = sc.next();

                            String qry = "SELECT * FROM tbl_users WHERE Email = ?";
                            java.util.List<java.util.Map<String, Object>> result = con.fetchRecords(qry, regEmail);

                            if (result.isEmpty()) {
                                break;
                            } else {
                                System.out.println("Email already exists, please enter another.");
                            }
                        }

                        System.out.print("Enter contact number: ");
                        String cnumber = sc.next();

                        String role = "";
                        OUTER:
                        while (true) {
                            System.out.print("Enter role (1 - Admin / 2 - Customer): ");
                            String roleInput = sc.next();
                            switch (roleInput) {
                                case "1":
                                    role = "Admin";
                                    break OUTER;
                                case "2":
                                    role = "Customer";
                                    break OUTER;
                                default:
                                    System.out.println("Invalid input! Please enter 1 for Admin or 2 for Customer.");
                                    break;
                            }
                        }

                        System.out.print("Enter password: ");
                        String password = sc.next();
                        String hashedPassword = con.hashPassword(password);

                        String status = "Pending";

                        String insertSQL = "INSERT INTO tbl_users (Name, Email, Cnumber, Role, Status, Password) VALUES (?, ?, ?, ?, ?, ?)";
                        con.addRecord(insertSQL, name, regEmail, cnumber, role, status, hashedPassword);

                        System.out.println("Registration successful! Please wait for Admin approval.");
                        break;

                    case 3:
                        viewUsers();
                        break;

                    case 4:
                        System.out.print("Enter ID: ");
                        int Id = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter Your Name: ");
                        String Uname = sc.next();
                        System.out.print("Enter Your Email: ");
                        String Uemail = sc.next();
                        System.out.print("Enter Contact Number: ");
                        String Unumber = sc.next();
                        System.out.print("Role (Customer/Admin): ");
                        String Urole = sc.next();
                        System.out.print("Enter Your Password: ");
                        String Upass = sc.next();

                        String sqlUpdate = "UPDATE tbl_users SET Name = ?, Email = ?, Cnumber = ?, Role = ?, Password = ? WHERE ID = ?";
                        database.updateRecord(sqlUpdate, Uname, Uemail, Unumber, Urole, Upass, Id);
                        break;

                    case 5:
                        System.out.print("Enter ID to DELETE: ");
                        int deleteId = sc.nextInt();
                        config dbConfig = new config();

                        String sqlDelete = "DELETE FROM tbl_users WHERE ID = ?";
                        dbConfig.deleteRecord(sqlDelete, deleteId);
                        break;

                    case 6:
                        System.out.println("Exiting...");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice.");
                        break;
                }

                if (choice != 6) {
                    System.out.print("Do you want to continue? (Y/N): ");
                    cont = sc.next().charAt(0);
                }

            } while (cont == 'Y' || cont == 'y');
        }
    }
}
