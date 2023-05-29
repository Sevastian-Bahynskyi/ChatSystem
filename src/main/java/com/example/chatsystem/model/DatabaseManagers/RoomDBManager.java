package com.example.chatsystem.model.DatabaseManagers;


import com.example.chatsystem.StartGui;
import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Room;
import com.example.chatsystem.model.UserInterface;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RoomDBManager
{
    public RoomDBManager()
    {

    }

    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres", "password");
    }

    public Room createRoom(UserInterface user, String name, String code)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Room (name, code) VALUES (?, ?) RETURNING id");
            ps.setString(1, name);
            ps.setString(2, code);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            int roomId = 0;
            if(rs.next())
            {
                roomId = rs.getInt("id");
            }
            ps = connection.prepareStatement("INSERT INTO moderatorroomlist (room_id, moderator_id) VALUES(?, ?)");
            ps.setInt(1, roomId);
            ps.setString(2, user.getViaId());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return getLastRoom();
    }

    public Room getLastRoom()
    {
        Room room = null;
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Room ORDER BY id DESC LIMIT 1");
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                room = new Room(rs.getInt("id"), rs.getString("name"), rs.getString("code"));
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return room;
    }


    public List<Room> getRooms()
    {
        ArrayList<Room> rooms = new ArrayList<>();
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Room ORDER BY id DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                Room room = new Room(rs.getInt("id"), rs.getString("name"), rs.getString("code"));
                rooms.add(room);
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return rooms;
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

    public void deleteRoom(int id)
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

    public Room getRoomByChannel(int channel_id)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT room_id FROM channel JOIN Room R ON R.id = Channel.room_id WHERE Channel.id = ?");

            ps.setInt(1, channel_id);
            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
                return readRoom(rs.getInt("room_id"));
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return null;
    }


    public void addChatterToRoom(UserInterface user, Room room)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO chatterroomlist (chatter_id, room_id) values(?, ?)");

            ps.setString(1, user.getViaId());
            ps.setInt(2, room.getId());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }
}

