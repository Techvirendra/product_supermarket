package UserMain.controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import User.dto.UserOperation;
import product_crud.dao.GadgetOperations;

public class UserMain {
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/supermarket?createDatabaseIfNotExist=true", "root", "admin");
			createTables(con);
			showOptions(con);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	private static void createTables(Connection connection) throws SQLException {
		String ct = "CREATE TABLE IF NOT EXISTS users (" + "id INT PRIMARY KEY,"+ "username VARCHAR(255) NOT NULL,"
                + "password VARCHAR(255) NOT NULL,"+ "wallet_amount INT NOT NULL)";
	}

	private static void showOptions(Connection connection) throws SQLException {
		Scanner sc = new Scanner(System.in);

		while (true) {
			System.out.println("Options:");
			System.out.println("1. User");
			System.out.println("2. Gadgets");
			System.out.println("3. Exit");

			int choice = sc.nextInt();
			sc.nextLine();

			switch (choice) {
			case 1:
				UserOperation userOperation = new UserOperation();
				userOperation.showUserOptions(connection);
				break;

			case 2:
				GadgetOperations crud = new GadgetOperations(connection, sc);
				crud.performGadgetCrud();
				break;

			case 3:
				System.out.println("Exiting! Thanks for using my Application..");
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}
}
