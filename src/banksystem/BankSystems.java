/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banksystem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
/**
 *
 * @author Administrator
 */
public class BankSystems {
   private BankDAO dao = new BankDAO();

    // Logic for the Management Window (ADD)
    // 'id' here refers to the Account ID provided by the user
    public void addCustomerAndAccount(String id, String fn, String ln, String em, String ph, String ty, String balStr) {
        // Basic check for empty fields
        if (id.isEmpty() || fn.isEmpty() || balStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all required fields!");
            return;
        }
        
        // VALIDATION: Check for exactly 4 digits for the Account ID
        if (!id.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(null, "Account ID must be exactly 4 digits (e.g., 1001)!");
            return;
        }

        try {
            double bal = Double.parseDouble(balStr);
            
            // DAO handles the Auto-Increment of customer_id behind the scenes
            if (dao.saveNewAccount(id, fn, ln, em, ph, ty, bal)) {
                JOptionPane.showMessageDialog(null, "Success! Account " + id + " has been created.");
            } else {
                // This usually triggers if the Account ID is a duplicate or AI is off
                JOptionPane.showMessageDialog(null, "Database Error! Check if Account ID " + id + " is already taken or if Database AI is disabled.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid balance format!");
        }
    }

    // Logic for the Transaction Window (Deposit/Withdraw)
    public void performTransaction(String id, String type, String amtStr, String action) {
        try {
            double amt = Double.parseDouble(amtStr);
            String result = dao.executeTransaction(id, type, amt, action);

            switch (result) {
                case "SUCCESS" -> JOptionPane.showMessageDialog(null, action + " successful!");
                case "NO_FUNDS" -> JOptionPane.showMessageDialog(null, "Insufficient Balance!");
                case "NOT_FOUND" -> JOptionPane.showMessageDialog(null, "Account ID not found!");
                default -> JOptionPane.showMessageDialog(null, "Transaction Failed!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid numeric amount!");
        }
    }

    // Logic for fetching info (Used for Search/Delete/Update)
    public void loadToFields(String id, JTextField fn, JTextField ln, JTextField em, JTextField ph, JTextField bal) {
        try {
            java.sql.ResultSet rs = dao.getAccountDetails(id);
            if (rs != null && rs.next()) {
                fn.setText(rs.getString("first_name"));
                ln.setText(rs.getString("last_name"));
                em.setText(rs.getString("email"));
                // Matches your database column name 'phone_number'
                ph.setText(rs.getString("phone_number")); 
                bal.setText(String.valueOf(rs.getDouble("balance")));
            } else {
                JOptionPane.showMessageDialog(null, "No record found for ID: " + id);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean updateCustomerInfo(String id, String fn, String ln, String em, String ph) {
        return dao.updateCustomer(id, fn, ln, em, ph);
    }

    public boolean deleteAccount(String id) {
        return dao.deleteAccount(id);
    }
    public void populateTable(javax.swing.JTable table) {
    try {
        // 1. Get the data from DAO
        java.sql.ResultSet rs = dao.getAllAccounts();
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setRowCount(0); 

        while (rs != null && rs.next()) {
            // 2. We must pull 6 items to match your 6 table columns
            Object[] row = {
                rs.getString("account_id"),     // Column 1
                rs.getInt("customer_id"),       // Column 2 (The AI ID)
                rs.getString("first_name"),     // Column 3
                rs.getString("last_name"),      // Column 4
                rs.getString("account_type"),   // Column 5
                rs.getDouble("balance")         // Column 6
            };
            model.addRow(row);
        }
    } catch (java.sql.SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Table Error: " + e.getMessage());
    }
}
    public void searchAndPopulate(javax.swing.JTable table, String keyword) {
    try {
        java.sql.ResultSet rs = dao.searchAccounts(keyword);
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setRowCount(0); 

        while (rs != null && rs.next()) {
            Object[] row = {
                rs.getString("account_id"),
                rs.getInt("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("account_type"),
                rs.getDouble("balance")
            };
            model.addRow(row);
        }
    } catch (java.sql.SQLException e) {
        e.printStackTrace();
    }
}
    public void populateTransactionTable(javax.swing.JTable table) {
    try {
        // Use the new DAO method we discussed
        java.sql.ResultSet rs = dao.getAllTransactions(); 
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setRowCount(0); 

        while (rs != null && rs.next()) {
            // These names MUST match your MySQL column names exactly
            Object[] row = {
                rs.getInt("transaction_id"),      // Trans ID
                rs.getString("account_id"),       // Account ID
                rs.getString("transaction_type"), // Type (Deposit/Withdraw)
                rs.getDouble("amount"),           // Amount
                rs.getTimestamp("transaction_date") // Date&Time
            };
            model.addRow(row);
        }
    } catch (java.sql.SQLException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "Transaction Table Error: " + e.getMessage());
    }
}
    public void filterTransactionTableByDate(javax.swing.JTable table, String dateStr) {
    try {
        java.sql.ResultSet rs = dao.searchTransactionsByDate(dateStr);
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setRowCount(0); 

        while (rs != null && rs.next()) {
            Object[] row = {
                rs.getInt("transaction_id"),
                rs.getString("account_id"),
                rs.getString("transaction_type"),
                rs.getDouble("amount"),
                rs.getTimestamp("transaction_date")
            };
            model.addRow(row);
        }
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(null, "Search Error: " + e.getMessage());
    }
}
    public void applyTransactionFilter(javax.swing.JTable table, String filterType, String value) {
    try {
        // Fetch results from DAO
        java.sql.ResultSet rs = dao.getFilteredTransactions(filterType, value);
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
        
        // Clear existing rows
        model.setRowCount(0);

        while (rs != null && rs.next()) {
            Object[] row = {
                rs.getInt("transaction_id"),      // Trans ID
                rs.getString("account_id"),       // Account ID
                rs.getString("transaction_type"), // Type
                rs.getDouble("amount"),           // Amount
                rs.getTimestamp("transaction_date") // Date&Time
            };
            model.addRow(row);
        }
    } catch (java.sql.SQLException e) {
        javax.swing.JOptionPane.showMessageDialog(null, "Filter Error: " + e.getMessage());
        e.printStackTrace();
    }
}
}
