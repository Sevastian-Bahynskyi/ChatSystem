package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.UserInterface;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatterDBManager
{
    static final String DB_URL = "jdbc:postgresql://localhost/sep2?user=postgres&password=password&currentSchema=sep2";
    static final String QUERY = "SELECT VIAid, username, password FROM sep2.Chatter";

    public void update(String VIAid, String username, String password) {
        // Open a connection
        try(Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement("UPDATE Chatter set username=?,password=? WHERE VIAid=?")) {
                System.out.println("Updating record to the table...");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, VIAid);
                ps.executeUpdate();
                conn.commit();
            }
            System.out.println("The table has been updated...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insert(String VIAid, String username, String password) {
        // Open a connection
        try(Connection conn = getConnection()){
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO Chatter(VIAid, username,password) VALUES(?,?,?)")){
                System.out.println("Inserting record to the table...");
                ps.setString(1,VIAid);
                ps.setString(2,username);
                ps.setString(3,password);
                ps.executeUpdate();
                conn.commit();
            }
            System.out.println("Inserted record to the table...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void delete(String VIAid) {
        // Open a connection
        try(Connection conn = getConnection()) {
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
    }
    public UserInterface read(String id) {
        UserInterface userInterface = null;
        // Open a connection
        try(Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM chatter WHERE VIAid = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                userInterface = new Chatter(rs.getString(1), rs.getString(2), rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInterface;
    }

    public Collection<UserInterface> readAll()
    {
        List<UserInterface> userList = new ArrayList<>();
        try(Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM chatter");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                userList.add(new Chatter(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public Collection<UserInterface> readAllByRoomID(int roomID)
    {
        List<UserInterface> userList = new ArrayList<>();
        try(Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT chatter_id FROM chatterroomlist where room_id = ?");
            ps.setInt(1, roomID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                userList.add(new Chatter(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2","postgres","password");
    }

}
