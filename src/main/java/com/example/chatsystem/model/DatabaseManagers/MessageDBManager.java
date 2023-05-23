package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.UserInterface;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class MessageDBManager
{
    public MessageDBManager()
    {
    }

    public Message createMessage(Message message)
    {
        Message mes = null;
        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Message (channel_id, timestamp, chatter_id, message) VALUES(?, ?, ?, ?)");
            ps.setInt(1, message.getChannelId());
            ps.setTimestamp(2, message.getTimeStamp());
            ps.setString(3, message.getUserId());
            ps.setString(4, message.getMessage());
            ps.executeUpdate();
            mes = getLastMessage(message.getChannelId());
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return mes;
    }

    public Message readMessage(int id)
    {
        Message message = new Message();
        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Message WHERE id = ?");

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            UserInterface chatter = null;

            if (rs.next())
            {
                chatter = Data.getInstance().getChatterDBManager().read(rs.getString("chatter_id"));
                // message should be id,Message,timestamp,chatter_id,channel_id
                message = new Message(rs.getInt("id"), rs.getString("message"),
                        rs.getTimestamp("timestamp"), chatter, rs.getInt("channel_id"));
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return message;
    }

    public Collection<Message> getAllMessagesForAChannel(int channelId)
    {
        System.out.println(LocalDateTime.now());
        ArrayList<Message> messageArrayList = new ArrayList<>();
        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Message WHERE channel_id = ? ORDER BY id ASC");

            ps.setInt(1, channelId);

            ResultSet rs = ps.executeQuery();
            UserInterface tempChatter = null;

            while (rs.next())
            {
                tempChatter = Data.getInstance().getChatterDBManager().read(rs.getString("chatter_id"));


                Message message = new Message(rs.getInt("id"), rs.getString("message"),
                        rs.getTimestamp("timestamp"), tempChatter, rs.getInt("channel_id"));

                messageArrayList.add(message);
            }
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        System.out.println(LocalDateTime.now());

        return messageArrayList;
    }


    public Message getLastMessage(int channelId)
    {
        Message message = null;
        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Message WHERE channel_id = ? ORDER BY id DESC LIMIT 1");

            ps.setInt(1, channelId);

            ResultSet rs = ps.executeQuery();
            UserInterface tempChatter = null;
            if (rs.next())
            {
                tempChatter = Data.getInstance().getChatterDBManager().read(rs.getString("chatter_id"));

                message = new Message(rs.getInt("id"), rs.getString("message"),
                        rs.getTimestamp("timestamp"), tempChatter, rs.getInt("channel_id"));
            }
        } catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }
        return message;
    }

    public void updateMessage(int id, Message newMessage)
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("UPDATE Message SET message=? WHERE id=?");

            ps.setString(1, newMessage.getMessage());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void deleteMessage(int id)
    {
        try (Connection connection = getConnection())
        {
            String temp = "deleted message";

            PreparedStatement ps = connection.prepareStatement("UPDATE Message SET message=? WHERE id=?");

            ps.setString(1, temp);
            ps.setInt(2, id);

            ps.executeUpdate();
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }


    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres", "password");
    }
}
