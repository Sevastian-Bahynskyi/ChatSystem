package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Data;
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
  };

  public void addModerator(String viaID, String username, String password){
    try(Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("insert into Chatter(VIAid, username, password, isModerator) values (?, ?, ?, true");
      ps.setString(1, viaID);
      ps.setString(2, username);
      ps.setString(3, password);
      ResultSet rs= ps.executeQuery();
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
  }

  public void makeModerator(String viaID, String room_id){
    try(Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("Update chatter set ismoderator = true where viaid= ?");
      ps.setString(1, viaID);
      PreparedStatement ps1 = connection.prepareStatement("Insert into ModeratorRoomList(room_id, moderator_id) values (? , ?)");
      ps1.setString(1, viaID);
      ps1.setString(2, room_id);
      ResultSet rs = ps.executeQuery();
      ResultSet rs1 = ps1.executeQuery();
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
  }

  public void deleteModeratorByID(String viaID){
    try(Connection connection = getConnection())
    {
      PreparedStatement psUpdate = connection.prepareStatement("UPDATE Chatter SET ismoderator = false where viaid = ?");
      PreparedStatement psDelete = connection.prepareStatement("delete from moderatorroomlist where moderator_id = ?");
      psUpdate.setString(1, viaID);
      psDelete.setString(1, viaID);
      ResultSet rs1 = psUpdate.executeQuery();
      ResultSet rs2 = psDelete.executeQuery();
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
  }


  public UserInterface getModeratorByID(String moderatorId)
  {
    UserInterface tempMod = null;
    try (Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement(
          "SELECT viaid, username, password from chatter where viaid = ?");
      ResultSet rs = ps.executeQuery();
      tempMod = new Moderator(rs.getString(1), rs.getString(2), rs.getString(3));
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
    return tempMod;
  }

  public Collection<Moderator> getListOfAllModeratorsOfRoom(int roomId){
    ArrayList<Moderator> temp = new ArrayList<>();
    UserInterface tempMod = null;
    try(Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("SELECT viaid, username, password, ismoderator from chatter join moderatorroomlist m on chatter.viaid = m.moderator_id where room_id = ? and ismoderator = true");
      ps.setInt(1, roomId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()){
        String id = rs.getString("viaid");
        String username = rs.getString("username");
        String password = rs.getString("password");
        temp.add(new Moderator(id, username, password));
      }
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
    return temp;
  }

  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2","postgres","password");
  }
}
