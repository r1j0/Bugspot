package com.github.r1j0.bugspot.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class SvnLogEntryHandler implements ISVNLogEntryHandler {
	
	private Pattern 
		message = null, 
		exclude = null,
		include = null;
	
	private List<LogEntries> entries = new ArrayList<LogEntries>(); 
	
	public SvnLogEntryHandler() {
		this(Pattern.compile("fix(es|ed)|crash|bug?", Pattern.CASE_INSENSITIVE));
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
		System.out.println("processing rev " + logEntry.getRevision());
		System.out.println("--------------------------");
		
		if (message.matcher(logEntry.getMessage()).find()) {
			Map<String, String> logPath = new HashMap<String, String>();
			
			if (logEntry.getChangedPaths().size() > 0) {
				Set<SVNLogEntryPath> changedPathsSet = logEntry.getChangedPaths().keySet();
				
				for (Iterator<SVNLogEntryPath> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
					logPath.put(Character.toString(entryPath.getType()), entryPath.getPath());
				}
			}
			
			LogEntries log = new LogEntriesImpl(
				logEntry.getRevision(), 
				logEntry.getAuthor(),
				logEntry.getDate(), 
				logEntry.getMessage(),
				logPath);
			
			entries.add(log);
		}
	}
	
	public List<LogEntries> getEntries() {
		return entries;
	}

}
