package com.github.r1j0.bugspot.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

public class SvnLogEntryHandler implements ISVNLogEntryHandler {
	
	private Pattern 
		message = null, 
		exclude = null,
		include = null;
	
	private List<LogEntries> entries = new ArrayList<LogEntries>(); 
	
	public SvnLogEntryHandler() {
		this(Pattern.compile("(fix(es)?)", Pattern.CASE_INSENSITIVE));
	}
	
	public SvnLogEntryHandler(Pattern message) {
		this(message, Pattern.compile("^$"));
	}
	
	public SvnLogEntryHandler(Pattern message, Pattern exclude) {
		this(message, exclude, Pattern.compile(".*"));
	}
	
	public SvnLogEntryHandler(Pattern message, Pattern exclude, Pattern include) {
		this.message = message;
		this.exclude = exclude;
		this.include = include;
	}
	
	public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
		// TODO Auto-generated method stub
		
		if (message.matcher(logEntry.getMessage()).matches()) {
			System.out.println(logEntry.getDate() + ": " + logEntry.getMessage());
		}
	}
	
	public List<LogEntries> getEntries() {
		return entries;
	}

}
