package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Moderator;
import com.example.chatsystem.model.UserInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class ModeratorDBManager
{
    public ModeratorDBManager() throws SQLException
    {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public void makeModerator(String viaID, int room_id)
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement ps1 = connection.prepareStatement("Insert into ModeratorRoomList(room_id, moderator_id) values (? , ?)");
            ps1.setInt(1, room_id);
            ps1.setString(2, viaID);
            ResultSet rs1 = ps1.executeQuery();
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public boolean isModeratorInRoom(String viaID, int room_id)
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement ps1 = connection.prepareStatement("SELECT COUNT(*) FROM ModeratorRoomList WHERE room_id=? AND moderator_id=?");
            ps1.setInt(1, room_id);
            ps1.setString(2, viaID);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // User is a moderator if count is greater than 0
            }

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public void deleteModeratorByID(String viaID)
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement psDelete = connection.prepareStatement("delete from moderatorroomlist where moderator_id = ?");
            psDelete.setString(1, viaID);
            ResultSet rs2 = psDelete.executeQuery();
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }


    public UserInterface getModeratorByIDInRoom(String moderatorId, int roomId)
    {
        UserInterface tempMod = null;
        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT viaid, username, password from chatter join moderatorroomlist m on chatter.viaid = m.moderator_id where moderator_id = ? AND m.room_id = ?");
            ps.setString(1, moderatorId);
            ps.setInt(2, roomId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                tempMod = new Moderator(rs.getString(1), rs.getString(2), rs.getString(3));
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return tempMod;
    }

    public Collection<Moderator> getListOfAllModeratorsOfRoom(int roomId)
    {
        ArrayList<Moderator> temp = new ArrayList<>();
        UserInterface tempMod = null;
        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT viaid, username, password from chatter join moderatorroomlist m on chatter.viaid = m.moderator_id where room_id = ?");
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                String id = rs.getString("viaid");
                String username = rs.getString("username");
                String password = rs.getString("password");
                temp.add(new Moderator(id, username, password));
            }
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        return temp;
    }

    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres", "password");
    }
}
