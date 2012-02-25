package com.github.r1j0.bugspot.repository;

import java.util.Date;
import java.util.Map;

public class LogEntriesImpl implements LogEntries {

    private final long revision;
    private final String author;
    private final String message;
    private final Map<String, String> logPath;
    private final Date date;


    public LogEntriesImpl(long revision, String author, Date date, String message, Map<String, String> logPath) {
	this.revision = revision;
	this.author = author;
	this.date = date;
	this.message = message;
	this.logPath = logPath;
    }


    public long getRevision() {
	return revision;
    }


    public String getAuthor() {
	return author;
    }


    public Date getDate() {
	return date;
    }


    public String getMessage() {
	return message;
    }


    public Map<String, String> getLogPath() {
	return logPath;
    }

}
