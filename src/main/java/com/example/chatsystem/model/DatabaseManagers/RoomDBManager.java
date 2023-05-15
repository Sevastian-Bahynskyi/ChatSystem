package com.example.chatsystem.model.DatabaseManagers;


import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Room;
import com.example.chatsystem.model.UserInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.Collection;

public class RoomDBManager
{
    public RoomDBManager()
    {

    }

    private Connection getConnection() throws SQLException
    {
        Driver driver = new org.postgresql.Driver();
        DriverManager.registerDriver(driver);
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2",
                "postgres","password");
    }

    public Room createRoom(String name, String code)
    {
        Room room = null;
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Room (name, code) VALUES (?, ?)");
//            ps.setInt(1, room.getId());
            ps.setString(1, name);
            ps.setString(2, code);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                room = new Room(rs.getInt("id"), rs.getString("name"), rs.getString("code"));
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return room;
    }
    public Room readRoom(int id) // can I change the name to findRoom
    {
        Room room = null;

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM room WHERE id = ?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();


            while (rs.next())
            {
                if(rs.getInt("id") == id)
                    room = new Room(id, rs.getString("name"), rs.getString("code"));
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return room;
    }

    public void updateRoom(int id, Room room)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("UPDATE room SET name=?, code=? WHERE id=?");

            ps.setString(1, room.getName());
            ps.setString(2, room.getCode());
            ps.setInt(3, id);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void deleteMessage(int id)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM room WHERE id=?");

            ps.setInt(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

}

