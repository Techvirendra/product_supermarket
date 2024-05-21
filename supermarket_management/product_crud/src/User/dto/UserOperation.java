package User.dto;

import java.sql.*;
import java.util.Scanner;

import product_crud.dao.GadgetOperations;

public class UserOperation {
	private Scanner sc = new Scanner(System.in);

	public void showUserOptions(Connection connection) throws SQLException {
		while (true) {
			System.out.println("User Options:");
			System.out.println("1. Register");
			System.out.println("2. Login");
			System.out.println("3. Back to Main Menu");

			int choice = sc.nextInt();
			sc.nextLine();

			switch (choice) {
			case 1:
				registerUser(connection);
				break;
			case 2:
				loginUser(connection);
				break;
			case 3:
				System.out.println("Main menu :- ");
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	public void addWalletBalance(int userId,int walletAmount, Connection connection) throws SQLException {
	    try {
	        connection.setAutoCommit(false);

	        System.out.println("Enter wallet Amount:");
	        int wallet = sc.nextInt();
	        sc.nextLine();

	        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE users SET wallet_amount = ? WHERE id = ?")) {
	            pstmt.setInt(1, wallet+walletAmount);
	            pstmt.setInt(2, userId);

	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                System.out.println("You have updated your wallet amount successfully");
	                connection.commit();
	            } else {
	                System.out.println("Insufficient funds. Purchase failed.");
	                connection.rollback();
	            }
	        }
	    } catch (SQLException e) {
	        connection.rollback();
	        e.printStackTrace();
	    } finally {
	        connection.setAutoCommit(true);
	    }
	}


	private void registerUser(Connection connection) throws SQLException {
		System.out.println("Enter email:");
		String email = sc.nextLine();

		System.out.println("Enter password:");
		String password = sc.nextLine();

		try (PreparedStatement pstmt = connection
				.prepareStatement("INSERT INTO users (email, password, wallet_amount) VALUES (?, ?, ?)")) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);

			System.out.println("Enter wallet Amount:");
			int wallet = sc.nextInt();
			pstmt.setInt(3, wallet);

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("User registered successfully.");
			} else {
				System.out.println("User registration failed. Please try again.");
			}
		}
	}

	private void loginUser(Connection connection) throws SQLException {
		System.out.println("Enter email:");
		String email = sc.nextLine();
		System.out.println("Enter password:");
		String password = sc.nextLine();

		try (PreparedStatement pstmt = connection
				.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);

			ResultSet res = pstmt.executeQuery();

			if (res.next()) {
				int userId = res.getInt("id");
				int walletAmount = res.getInt("wallet_amount");

				System.out.println("Login successful!");
				System.out.println("Wallet Amount: $" + walletAmount);

				GadgetOperations gadgetOperation = new GadgetOperations(connection, sc);
				gadgetOperation.fetchGadgetsAndAskForPurchase(userId);
			} else {
				System.out.println("Login failed. Incorrect username or password.");
			}
		}
	}
}
