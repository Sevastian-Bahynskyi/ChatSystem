package com.example.chatsystem.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLog
{
    private final CurrentTime currentTime;
    private final File logFile;

    private static FileLog instance;

    private FileLog() {
        logFile = new File("FileLog.txt");
        currentTime = CurrentTime.getInstance();
    }

    public static synchronized FileLog getInstance()
    {
        if(instance == null)
            return  instance = new FileLog();
        return instance;
    }


    public void log(String message) throws IOException {
        try (FileWriter fileWriter = new FileWriter(logFile, true);
             PrintWriter writer = new PrintWriter(fileWriter)) {
            String logLine = currentTime.getFormattedTime() + " - " + message;
            writer.println(logLine);
        }
    }
}
