package main.java.com.example.chatsystem.viewmodel.Seva;

import com.example.chatsystem.model.Room;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest
{
    Room room;

    @BeforeEach void setUp()
    {
        room = new Room(56, "room56", "aertgea5hgq5h");
    }

    @Test void get_code_test()
    {
        assertEquals("aertgea5hgq5h", room.getCode());
    }

    @Test void get_name_test()
    {
        assertEquals("room56", room.getName());
    }

    @Test void get_id_test()
    {
        assertEquals(56, room.getId());
    }

    @Test void set_name_test()
    {
        room.setName("room56Changed");
        assertEquals("room56Changed", room.getName());
    }

    @Test void set_code_test()
    {
        room.setCode("2457tu4sr6");
        assertEquals("2457tu4sr6", room.getCode());
    }


    @Test void equals_test()
    {
        Room tempRoom = new Room(56, "room56Changed", "2457tu4sr6");
        assertEquals(tempRoom, room);
    }

    @Test void validate_name_works() throws IllegalArgumentException
    {
        try
        {
            new Room(1, "1", "qoignio5g43");
        }
        catch (Exception e)
        {
            assertEquals(e.getClass(), IllegalArgumentException.class);
        }
    }


    @Test void validate_code_works()
    {
        try
        {
            new Room(1, "room1", "tobigcodethatdoesn'tfittodefinedbounds");
        }
        catch (Exception e)
        {
            assertEquals(e.getClass(), IllegalArgumentException.class);
        }
    }
}
