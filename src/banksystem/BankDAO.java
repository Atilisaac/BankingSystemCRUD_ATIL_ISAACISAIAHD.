/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banksystem;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Administrator
 */
public class BankDAO {
 // 1. SAVE NEW ACCOUNT
    public boolean saveNewAccount(String id, String fn, String ln, String em, String ph, String type, double balance) {
        String sqlCust = "INSERT INTO customer (first_name, last_name, email, phone_number) VALUES (?, ?, ?, ?)";
        String sqlAcc = "INSERT INTO account (account_id, customer_id, account_type, balance) VALUES (?, LAST_INSERT_ID(), ?, ?)";

        try (Connection conn = DBConnecton.getConnection()) {
            conn.setAutoCommit(false); 
            try (PreparedStatement p1 = conn.prepareStatement(sqlCust);
                 PreparedStatement p2 = conn.prepareStatement(sqlAcc)) {
                
                p1.setString(1, fn); 
                p1.setString(2, ln);
                p1.setString(3, em); 
                p1.setString(4, ph);
                p1.executeUpdate();

                p2.setString(1, id); 
                p2.setString(2, type); 
                p2.setDouble(3, balance); 
                p2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace(); 
                return false;
            }
        } catch (Exception e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // 2. GET ACCOUNT DETAILS (For Search/Update/Delete)
    public ResultSet getAccountDetails(String id) {
        String sql = "SELECT c.first_name, c.last_name, c.email, c.phone_number, a.balance " +
                     "FROM customer c JOIN account a ON c.customer_id = a.customer_id " +
                     "WHERE a.account_id = ?";
        try {
            Connection conn = DBConnecton.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 3. UPDATE CUSTOMER
    public boolean updateCustomer(String id, String fn, String ln, String em, String ph) {
        String sql = "UPDATE customer c JOIN account a ON c.customer_id = a.customer_id " +
                     "SET c.first_name=?, c.last_name=?, c.email=?, c.phone_number=? WHERE a.account_id=?";
        try (Connection conn = DBConnecton.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fn);
            pstmt.setString(2, ln);
            pstmt.setString(3, em);
            pstmt.setString(4, ph);
            pstmt.setString(5, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // 4. DELETE ACCOUNT
    public boolean deleteAccount(String id) {
        // We delete the account row. (Note: If you have a foreign key set to CASCADE, 
        // it might delete the customer too, otherwise you'd delete account first).
        String sql = "DELETE FROM account WHERE account_id = ?";
        try (Connection conn = DBConnecton.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // 5. EXECUTE TRANSACTION (Deposit/Withdraw)
    public String executeTransaction(String id, String type, double amt, String action) {
    // 1. We now check ID AND Type
    String checkSql = "SELECT balance FROM account WHERE account_id=? AND account_type=?";
    String updateSql = "UPDATE account SET balance=? WHERE account_id=? AND account_type=?";
    
    // 2. SQL to save the history into your new table
    String logSql = "INSERT INTO transaction (account_id, transaction_type, amount) VALUES (?, ?, ?)";

    try (Connection conn = DBConnecton.getConnection()) {
        // --- STEP 1: CHECK BALANCE ---
        PreparedStatement psCheck = conn.prepareStatement(checkSql);
        psCheck.setString(1, id);
        psCheck.setString(2, type);
        ResultSet rs = psCheck.executeQuery();

        if (!rs.next()) return "NOT_FOUND";

        double currentBal = rs.getDouble("balance");
        if (action.equals("Withdraw") && currentBal < amt) return "NO_FUNDS";

        double newBal = action.equals("Deposit") ? currentBal + amt : currentBal - amt;

        // --- STEP 2: UPDATE ACCOUNT BALANCE ---
        PreparedStatement psUpdate = conn.prepareStatement(updateSql);
        psUpdate.setDouble(1, newBal);
        psUpdate.setString(2, id);
        psUpdate.setString(3, type);
        psUpdate.executeUpdate();

        // --- STEP 3: LOG THE TRANSACTION ---
        // This makes the "View All Transactions" table actually show data
        PreparedStatement psLog = conn.prepareStatement(logSql);
        psLog.setString(1, id);
        psLog.setString(2, action); // Saves "Deposit" or "Withdraw"
        psLog.setDouble(3, amt);
        psLog.executeUpdate();

        return "SUCCESS";
        
    } catch (SQLException e) {
        e.printStackTrace();
        return "ERROR";
    }
}
    public ResultSet getAllAccounts() {
    // Added c.customer_id to the selection
    String sql = "SELECT a.account_id, c.customer_id, c.first_name, c.last_name, a.account_type, a.balance " +
                 "FROM customer c JOIN account a ON c.customer_id = a.customer_id";
    try {
        Connection conn = DBConnecton.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    public ResultSet searchAccounts(String keyword) {
    // Searches by Account ID, First Name, or Last Name
    String sql = "SELECT a.account_id, c.customer_id, c.first_name, c.last_name, a.account_type, a.balance " +
                 "FROM customer c JOIN account a ON c.customer_id = a.customer_id " +
                 "WHERE a.account_id LIKE ? OR c.first_name LIKE ? OR c.last_name LIKE ?";
    try {
        Connection conn = DBConnecton.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        String query = "%" + keyword + "%"; 
        pstmt.setString(1, query);
        pstmt.setString(2, query);
        pstmt.setString(3, query);
        return pstmt.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    public ResultSet getAllTransactions() {
    // We select from the 'transaction' table you just showed in your screenshot
    String sql = "SELECT transaction_id, account_id, transaction_type, amount, transaction_date FROM transaction ORDER BY transaction_date DESC";
    try {
        Connection conn = DBConnecton.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    public ResultSet searchTransactionsByDate(String dateStr) {
    // This SQL selects transactions where the date matches the input, regardless of time
    String sql = "SELECT * FROM transaction WHERE DATE(transaction_date) = ? ORDER BY transaction_date DESC";
    try {
        Connection conn = DBConnecton.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, dateStr); 
        return ps.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    public ResultSet getFilteredTransactions(String filterType, String value) {
    String sql = "";
    
    // We choose the SQL query based on what the user picked in the ComboBox
    // Make sure these strings match your ComboBox items exactly!
    if (filterType.equals("Date (YYYY-MM-DD)")) {
        sql = "SELECT * FROM transaction WHERE DATE(transaction_date) = ? ORDER BY transaction_date DESC";
    } else if (filterType.equals("Account ID")) {
        sql = "SELECT * FROM transaction WHERE account_id = ? ORDER BY transaction_date DESC";
    } else if (filterType.equals("Transaction Type")) {
        sql = "SELECT * FROM transaction WHERE transaction_type = ? ORDER BY transaction_date DESC";
    } else {
        // Fallback: search everything
        return getAllTransactions();
    }

    try {
        Connection conn = DBConnecton.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, value);
        return ps.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
   
}

