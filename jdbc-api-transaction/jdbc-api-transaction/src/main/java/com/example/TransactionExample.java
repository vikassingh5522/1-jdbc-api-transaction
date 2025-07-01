package com.example;
import java.sql.*;
public class TransactionExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "root";
        String password = "Vikas@9156";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Disable auto-commit
            connection.setAutoCommit(false);

            try (PreparedStatement stmt1 = connection.prepareStatement(
                    "UPDATE accounts SET balance = balance - ? WHERE account_id = ?");
                 PreparedStatement stmt2 = connection.prepareStatement(
                         "UPDATE accounts SET balance = blance + ? WHERE account_id = ?")) {

                // First operation: Deduct from sender's account
                stmt1.setDouble(1, 1000.00);
                stmt1.setInt(2, 1);
                stmt1.executeUpdate();
                System.out.println("Deducted from account 1");

                // Second operation: Add to receiver's account
                stmt2.setDouble(1, 100.00);
                stmt2.setInt(2, 2);
                stmt2.executeUpdate();
                System.out.println("Credited into account 2");

                // Commit the transaction
                connection.commit();
                System.out.println("Transaction committed successfully.");

            } catch (SQLException e) {
                // Rollback on error
                connection.rollback();
                System.out.println("Transaction rolled back due to error: " + e.getMessage());
            } finally {
                // Restore auto-commit
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}