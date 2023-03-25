package com.example.chatsystem.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class DefaultLog {
    private final CurrentTime currentTime;
    private final File logDirectory;

    private static DefaultLog instance;

    private DefaultLog() {
        logDirectory = new File("FileLog.txt");
        currentTime = CurrentTime.getInstance();
    }

    public static synchronized DefaultLog getInstance()
    {
        if(instance == null)
            return  instance = new DefaultLog();
        return instance;
    }

    private File getLogFile() {
        return logDirectory;
    }

    public void log(String message) throws IOException {
        try (FileWriter fileWriter = new FileWriter(getLogFile(), true);
             PrintWriter writer = new PrintWriter(fileWriter)) {
            String logLine = currentTime.getFormattedTime() + " - " + message;
            writer.println(logLine);
        }
    }
}
