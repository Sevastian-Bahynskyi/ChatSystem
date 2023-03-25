package com.example.chatsystem.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrentTime
{
    private static final DateTimeFormatter EUROPEAN_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");

    private static CurrentTime instance;

    private CurrentTime()
    {
    }

    public static CurrentTime getInstance()
    {
        if (instance == null)
            return instance = new CurrentTime();
        return instance;
    }

    /**
     * Formats current time according to standard European format.
     *
     * @return a string containing the formatted time.
     */
    public String getFormattedTime()
    {
        LocalDateTime time = LocalDateTime.now();
        return time.format(EUROPEAN_TIME_FORMATTER);
    }

    /**
     * Formats current date according to ISO format. Works for sorting.
     *
     * @return a string containing the formatted date.
     */
    public String getFormattedIsoDate()
    {
        LocalDateTime time = LocalDateTime.now();
        return time.format(ISO_DATE_FORMATTER);
    }
}