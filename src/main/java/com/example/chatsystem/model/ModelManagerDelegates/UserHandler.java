package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.ModelManager;
import com.example.chatsystem.model.UserInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UserHandler extends ModelManager
{
    @Override
    public void login(String viaID, String username, String password)
    {
        user = server.login(username, password);
        doFirstLoad();
    }

    @Override
    public void register(String viaID, String username, String password, String imageUrl)
    {
        user = server.register(viaID, username, password, imageUrl);
        doFirstLoad();
    }

    public void receiveUsersInRoom(ArrayList<UserInterface> users)
    {
        support.firePropertyChange("update user list", null, users);
    }

    @Override
    public UserInterface getUser()
    {
        return user;
    }


    @Override
    public void banUser(UserInterface user)
    {
        server.banUser(room.getId(), user);
    }

    @Override
    public void makeModerator(UserInterface user)
    {
        server.makeModerator(user, room.getId());
    }

    @Override
    public boolean isModerator(int channelId) throws RemoteException
    {
        return server.isModerator(user.getViaId(), channelId);
    }

    @Override
    public boolean isModeratorInRoom(int roomId)
    {
        try
        {
            return server.isModeratorInRoom(user.getViaId(), roomId);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<UserInterface> getUserList()
    {
        return users;
    }
}
