package com.example.dao;

import com.example.model.Account;
import com.example.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // ✅ FIXED: Use single connection for rollback to work correctly.
    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_id, balance) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // ✅ Start manual transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, account.getAccountId());
                stmt.setDouble(2, account.getBalance());
                stmt.executeUpdate();
            }

            conn.commit(); // ✅ Commit on success
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // ✅ Rollback on failure
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // ✅ Reset autocommit to true
                conn.close();              // ✅ Properly close connection
            }
        }
    }

    public Account getAccount(int accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getDouble("balance"));
            }
            return null;
        }
    }

    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(new Account(rs.getInt("account_id"), rs.getDouble("balance")));
            }
        }
        return accounts;
    }

    // ✅ FIXED: Single connection used for update, proper rollback
    public void updateAccount(Account account) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, account.getBalance());
                stmt.setInt(2, account.getAccountId());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ✅ FIXED: deleteAccount now uses one connection and handles rollback properly
    public void deleteAccount(int accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, accountId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ✅ FIXED: transferMoney now manages connection, savepoint, rollback, and logs transaction failure
    public void transferMoney(int fromAccountId, int toAccountId, double amount) throws SQLException {
        Connection conn = null;
        Savepoint savepoint = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // ✅ Transaction starts

            // Deduct money from sender
            String deductSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deductSql)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, fromAccountId);
                stmt.executeUpdate();
            }

            // Set savepoint after deduction
            savepoint = conn.setSavepoint("TransferSavepoint");

            // Add money to receiver
            String addSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(addSql)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, toAccountId);
                stmt.executeUpdate();
            }

            // Validate sender balance
            Account fromAccount = getAccount(fromAccountId);
            if (fromAccount.getBalance() < 0) {
                throw new SQLException("Insufficient balance after transfer");
            }

            conn.commit(); // ✅ Commit whole transaction
        } catch (SQLException e) {
            if (conn != null) {
                if (savepoint != null && e.getMessage().contains("Insufficient balance")) {
                    conn.rollback(savepoint); // ✅ Partial rollback to savepoint

                    // Log transfer failure
                    String logSql = "INSERT INTO transfer_log (from_account, to_account, amount, status) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(logSql)) {
                        stmt.setInt(1, fromAccountId);
                        stmt.setInt(2, toAccountId);
                        stmt.setDouble(3, amount);
                        stmt.setString(4, "FAILED");
                        stmt.executeUpdate();
                        conn.commit(); // ✅ Commit log entry
                    } catch (SQLException ex) {
                        conn.rollback(); // ❌ Rollback if log insert fails
                        throw ex;
                    }
                } else {
                    conn.rollback(); // ❌ Rollback full transaction on any other failure
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // ✅ Reset autocommit
                    conn.close();             // ✅ Always close connection
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
