package com.github.r1j0.bugspot.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class SvnLogEntryHandler implements ISVNLogEntryHandler {
	private List<LogEntries> entries = new ArrayList<LogEntries>(); 
	
	public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
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
			logPath
		);
		
		entries.add(log);
	}
	
	public List<LogEntries> getEntries() {
		return entries;
	}

}
