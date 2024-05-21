package product_crud.dao;

import java.sql.*;
import java.util.Scanner;

public class GadgetCrud {
    private Scanner sc;
    private Connection connection;

    public GadgetCrud(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    public void performGadgetCrud() {
        try {
            while (true) {
                System.out.println("Gadget CRUD Options:");
                System.out.println("1. Create Gadget");
                System.out.println("2. Update Gadget");
                System.out.println("3. Delete Gadget");
                System.out.println("4. Show Gadgets");
                System.out.println("5. Back to Main Menu");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        createGadget();
                        break;
                    case 2:
                        updateGadget();
                        break;
                    case 3:
                        deleteGadget();
                        break;
                    case 4:
                        showGadgets();
                        break;
                    case 5:
                        System.out.println("Main Menu :- ");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createGadget() throws SQLException {
        System.out.println("Enter Gadget Name:");
        String name = sc.nextLine();

        System.out.println("Enter Gadget Price:");
        int price = sc.nextInt();
        sc.nextLine(); 

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO gadgets (name, price) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Gadget created successfully.");
            } else {
                System.out.println("Gadget creation failed. Please try again.");
            }
        }
    }

    private void updateGadget() throws SQLException {
        showGadgets();
        System.out.println("Enter the id of the gadget to update:");
        int gadgetID = sc.nextInt();
        sc.nextLine();

        if (isGadgetAvailable(gadgetID)) {
            System.out.println("Enter updated Gadget Name:");
            String updatedName = sc.nextLine();

            System.out.println("Enter updated Gadget Price:");
            int updatedPrice = sc.nextInt();
            sc.nextLine(); 

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE gadgets SET name = ?, price = ? WHERE id = ?")) {
                pstmt.setString(1, updatedName);
                pstmt.setInt(2, updatedPrice);
                pstmt.setInt(3, gadgetID);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Gadget updated successfully.");
                } else {
                    System.out.println("Gadget update failed. Please try again.");
                }
            }
        } else {
            System.out.println("Invalid gadget ID. Update failed.");
        }
    }

    private void deleteGadget() throws SQLException {
        showGadgets();
        System.out.println("Enter the id of the gadget to delete:");
        int gadgetID = sc.nextInt();
        sc.nextLine();

        if (isGadgetAvailable(gadgetID)) {
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "DELETE FROM gadgets WHERE id = ?")) {
                pstmt.setInt(1, gadgetID);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Gadget deleted successfully.");
                } else {
                    System.out.println("Gadget deletion failed. Please try again.");
                }
            }
        } else {
            System.out.println("Invalid gadget ID. Deletion failed.");
        }
    }

    private void showGadgets() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet res = stmt.executeQuery("SELECT * FROM gadgets")) {

            System.out.println("Available Gadgets:");
            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                int price = res.getInt("price");
                System.out.println(id + ". " + name + " - $" + price);
            }
        }
    }

    private boolean isGadgetAvailable(int gadgetID) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM gadgets WHERE id = ?")) {
            pstmt.setInt(1, gadgetID);
            ResultSet res = pstmt.executeQuery();
            return res.next();
        }
    }
}
