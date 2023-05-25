package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RoomHandler extends ModelManager
{
    public void addRoom(String name, String code, String imageURL)
    {
        try
        {
            server.createRoom(user, name, code);
            user = new Moderator(user);
            System.out.println(user);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }
    public void joinRoom(Room room)
    {
        server.addChatterToRoom(user, room);
    }

    public int getRoomId()
    {
        return room.getId();
    }

    public void receiveNewRoom(Room room)
    {
        if(rooms.contains(room))
            support.firePropertyChange("reload room", null, room);
        else
            support.firePropertyChange("room added", null, room);
    }

    public void editRoom(String roomName, String roomCode, String imageUrl)
    {
        server.editRoom(room.getId(), roomName, roomCode, imageUrl);
    }

    public ArrayList<Channel> getChannelsInRoom(int roomId)
    {
        if(roomId == -1)
            return channels;
        else {
            try
            {
                this.room = server.getRoom(roomId);
                users = server.getUserListInRoom(room.getId());
                if(server.isModerator(user.getViaId(), room.getId()))
                    user = new Moderator(user);
                else
                    user = new Chatter(user);

                if(!users.contains(user))
                {
                    support.firePropertyChange("join a room", null, List.of(room, user));
                    return null;
                }

                // user list is empty all the time

            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            return server.getChannelsInTheRoom(roomId);
        }
    }

    public void leaveRoom()
    {
        server.leaveRoom(user.getViaId(), room.getId());
    }
}
