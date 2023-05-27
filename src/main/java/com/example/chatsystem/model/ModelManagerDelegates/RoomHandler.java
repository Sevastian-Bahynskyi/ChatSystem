package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RoomHandler
{
    private ModelManager m;
    public RoomHandler(ModelManager m)
    {
        this.m = m;
    }
    public void addRoom(String name, String code, String imageURL)
    {
        try
        {
            m.server.createRoom(m.user, name, code);
            m.user = new Moderator(m.user);
            System.out.println(m.user);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }
    public void joinRoom(Room room)
    {
        m.server.addChatterToRoom(m.user, room);
    }

    public int getRoomId()
    {
        return m.room.getId();
    }

    public void receiveNewRoom(Room room)
    {
        if(m.rooms.contains(room))
            m.support.firePropertyChange("reload room", null, room);
        else
            m.support.firePropertyChange("room added", null, room);
    }

    public void editRoom(String roomName, String roomCode, String imageUrl)
    {
        m.server.editRoom(m.room.getId(), roomName, roomCode, imageUrl);
    }

    public ArrayList<Channel> getChannelsInRoom(int roomId)
    {
        if(roomId == -1)
            return m.channels;
        else {
            try
            {
                m.room = m.server.getRoom(roomId);
                m.users = m.server.getUserListInRoom(m.room.getId());
                if(m.server.isModerator(m.user.getViaId(), m.room.getId()))
                    m.user = new Moderator(m.user);
                else
                    m.user = new Chatter(m.user);

                if(!m.users.contains(m.user))
                {
                    m.support.firePropertyChange("join a room", null, List.of(m.room, m.user));
                    return null;
                }

                // user list is empty all the time

            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            return m.server.getChannelsInTheRoom(roomId);
        }
    }

    public void leaveRoom()
    {
        m.server.leaveRoom(m.user.getViaId(), m.room.getId());
    }
}
