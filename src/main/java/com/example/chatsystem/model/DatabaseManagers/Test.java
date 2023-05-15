package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Room;

public class Test
{
    public static void main(String[] args)
    {
        RoomDBManager roomDBManager = new RoomDBManager();
        Room room = roomDBManager.createRoom("newroom", "codeafe");
        roomDBManager.updateRoom(room.getId(), new Room("newroom2", "newcode2"));
//        roomDBManager.deleteRoom(room.getId());
    }
}
