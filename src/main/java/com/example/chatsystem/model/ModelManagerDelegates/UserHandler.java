package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.UserInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UserHandler
{
    private ModelManager m;

    public UserHandler(ModelManager m)
    {
        this.m = m;
    }
    public void login(String viaID, String username, String password)
    {
        m.user = m.server.login(username, password);
        m.doFirstLoad();
    }

    public void register(String viaID, String username, String password, String imageUrl)
    {
        m.user = m.server.register(viaID, username, password, imageUrl);
        m.doFirstLoad();
    }

    public void receiveUsersInRoom(ArrayList<UserInterface> users)
    {
        m.support.firePropertyChange("update user list", null, users);
    }

    public UserInterface getUser()
    {
        return m.user;
    }


    public void banUser(UserInterface user)
    {
        m.server.banUser(m.room.getId(), user);
    }

    public void makeModerator(UserInterface user)
    {
        m.server.makeModerator(user, m.room.getId());
    }

    public boolean isModerator(int channelId) throws RemoteException
    {
        return m.server.isModerator(m.user.getViaId(), channelId);
    }

    public boolean isModeratorInRoom(int roomId)
    {
        try
        {
            return m.server.isModeratorInRoom(m.user.getViaId(), roomId);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<UserInterface> getUserList()
    {
        return m.users;
    }
}
