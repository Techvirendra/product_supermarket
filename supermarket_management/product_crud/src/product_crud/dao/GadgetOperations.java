package product_crud.dao;

import java.sql.*;
import java.util.Scanner;

import User.dto.UserOperation;

public class GadgetOperations {
	private Scanner sc;
	private Connection connection;

	public GadgetOperations(Connection connection, Scanner sc) {
		this.connection = connection;
		this.sc = sc;
	}

	public void fetchGadgetsAndAskForPurchase(int userId) {
		try {
			while (true) {
				showGadgets();
				System.out.println("Do you want to purchase a gadget? (yes/no)");
				String userInput = sc.nextLine();

				if ("yes".equals(userInput)) {
					System.out.println("Enter the id of the product: ");
					int gadgetChoice = sc.nextInt();
					sc.nextLine();
					purchaseGadget(userId, gadgetChoice);
				} else if ("no".equals(userInput)) {
					System.out.println("Going back to the user main menu...");
					return;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void performGadgetCrud() {
		GadgetCrud gadgetCrud = new GadgetCrud(connection, sc);
		gadgetCrud.performGadgetCrud();
	}

	public void showGadgets() throws SQLException {
		try (Statement stmt = connection.createStatement();
				ResultSet res = stmt.executeQuery("SELECT * FROM gadgets")) {

			System.out.println("Available Gadgets:");
			while (res.next()) {
				int id = res.getInt("id");
				String name = res.getString("name");
				int price = res.getInt("price");
				System.out.println(id + "." + name + " - $" + price);
			}
		}
	}

	private void purchaseGadget(int userId, int gadgetID) throws SQLException {
		if (isGadgetAvailable(gadgetID)) {
			int walletAmount = getUserWallet(userId);
			int gadgetPrice = getGadgetPrice(gadgetID);
			if (walletAmount >= gadgetPrice) {
				updateWallet(userId, walletAmount - gadgetPrice);

				System.out.println("Gadget purchased successfully!");

				System.out.println("Wallet Amount: $" + (walletAmount - gadgetPrice));
				return;
			} else {
				System.out.println("Insufficient funds. Do you want to add funds? (yes/no)");
				String addFundsAnswer = sc.nextLine();

				if ("yes".equalsIgnoreCase(addFundsAnswer)) {
					UserOperation operation = new UserOperation();
					operation.addWalletBalance(userId,walletAmount, connection); 
				} else if ("no".equalsIgnoreCase(addFundsAnswer)) {
					System.out.println("Going back to the user main menu...");
					return;
				}
			}
		} else {
			System.out.println("Invalid gadget ID. Purchase failed.");
		}
	}

	private boolean isGadgetAvailable(int gadgetID) throws SQLException {
		try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM gadgets WHERE id = ?")) {
			pstmt.setInt(1, gadgetID);
			ResultSet res = pstmt.executeQuery();
			return res.next();
		}
	}

	private int getUserWallet(int userId) throws SQLException {
		try (PreparedStatement pstmt = connection.prepareStatement("SELECT wallet_amount FROM users WHERE id = ?")) {
			pstmt.setInt(1, userId);
			ResultSet res = pstmt.executeQuery();

			if (res.next()) {
				return res.getInt("wallet_amount");
			} else {
				return 0;
			}
		}
	}

	private int getGadgetPrice(int gadgetID) throws SQLException {
		try (PreparedStatement pstmt = connection.prepareStatement("SELECT price FROM gadgets WHERE id = ?")) {
			pstmt.setInt(1, gadgetID);
			ResultSet res = pstmt.executeQuery();

			if (res.next()) {
				return res.getInt("price");
			} else {
				return 0;
			}
		}
	}

	private void updateWallet(int userId, int newWalletAmount) throws SQLException {
		try (PreparedStatement pstmt = connection.prepareStatement("UPDATE users SET wallet_amount = ? WHERE id = ?")) {
			pstmt.setInt(1, newWalletAmount);
			pstmt.setInt(2, userId);
			pstmt.executeUpdate();
		}
	}

}
