package com.github.r1j0.bugspot.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnRepository implements Repository {

	private static final int HEAD_REVISION = -1;

	private final String url;
	private final String username;
	private final String password;


	public SvnRepository(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;

		setup();
	}


	public List<LogEntries> checkout(long startRevision) {
		return checkout(startRevision, HEAD_REVISION);
	}


	@SuppressWarnings("unchecked")
	public List<LogEntries> checkout(long startRevision, long endRevision) {
		// TODO Auto-generated method stub
				SVNRepository repository = null;

				try {
					repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
				} catch (SVNException svne) {
					System.err.println("Error for repository with location: " + url + ". Message: " + svne.getMessage());
					System.exit(1);
				}

				ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
				repository.setAuthenticationManager(authManager);
				Collection<SVNLogEntry> svnLogEntries = null;
				SvnLogEntryHandler handler = new SvnLogEntryHandler();
				long num;
				
				try {
					num = repository.log(new String[] { "" }, startRevision, endRevision, true, true, handler);
				} catch (SVNException svne) {
					System.out.println("Error retrieving log information for repository: " + url + ". Message: " + svne.getMessage());
					System.exit(1);
				}

//				List<LogEntries> logEntries = new ArrayList<LogEntries>();
//
//				for (Iterator<SVNLogEntry> entries = svnLogEntries.iterator(); entries.hasNext();) {
//					SVNLogEntry logEntry = entries.next();
//					Map<String, String> logPath = new HashMap<String, String>();
//
//					if (logEntry.getChangedPaths().size() > 0) {
//						Set<SVNLogEntryPath> changedPathsSet = logEntry.getChangedPaths().keySet();
//
//						for (Iterator<SVNLogEntryPath> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
//							SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
//							logPath.put(Character.toString(entryPath.getType()), entryPath.getPath());
//						}
//					}
//
//					logEntries.add(new LogEntriesImpl(logEntry.getRevision(), logEntry.getAuthor(), logEntry.getDate(), logEntry.getMessage(), logPath));
//				}
				List<LogEntries> logEntries = handler.getEntries();
				return logEntries;
	}


	private void setup() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}
}
