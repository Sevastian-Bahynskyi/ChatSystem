package com.example.chatsystem.model.DatabaseManagers;

import java.sql.*;

public class ChatterDBManager{
    static final String DB_URL = "jdbc:postgresql://localhost/sep2?user=postgres&password=password&currentSchema=sep2";
    static final String QUERY = "SELECT VIAid, username, password, isModerator FROM sep2.Chatter";

    public static void update(String VIAid, String username, String password, boolean isModerator) {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement("UPDATE Chatter set username=?,password=?,isModerator=? WHERE VIAid=?")) {
                System.out.println("Updating record to the table...");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setBoolean(3, isModerator);
                ps.setString(4, VIAid);
                ps.executeUpdate();
                conn.commit();
            }
            System.out.println("The table has been updated...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        read();
    }
    public static void insert(String VIAid, String username, String password, boolean isModerator) {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL)){
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO Chatter(VIAid, username,password,isModerator) VALUES(?,?,?,?)")){
                System.out.println("Inserting record to the table...");
                ps.setString(1,VIAid);
                ps.setString(2,username);
                ps.setString(3,password);
                ps.setBoolean(4,isModerator);
                ps.executeUpdate();
                conn.commit();
            }
            System.out.println("Inserted record to the table...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        read();
    }
    public static void delete(String VIAid) {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try( PreparedStatement ps = conn.prepareStatement("DELETE FROM Chatter WHERE VIAid = ?")) {
                System.out.println("Deleting records from the table...");
                ps.setString(1, VIAid);
                ps.executeUpdate();
                conn.commit();
            }
            System.out.println("Deleted records from the table...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        read();
    }
    public static void read() {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QUERY);
        ) {
            while(rs.next()){
                //Display values
                System.out.print("VIAid: " + rs.getString("VIAid"));
                System.out.print(", username: " + rs.getString("username"));
                System.out.print(", password: " + rs.getString("password"));
                System.out.println(", isModerator: " + rs.getBoolean("isModerator"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        read();
        insert("222222","Insert","insert",false);
        update("222222","NotInsert","insert",true);
        delete("222222");
    }
}
