package com.github.r1j0.bugspot.repository;

import java.util.List;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.SVNException;
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
	
	
	public List<LogEntries> checkout(long startRevision, long endRevision) {
		return checkout(startRevision, HEAD_REVISION, null);
	}
	
	
	public List<LogEntries> checkout(long startRevision, long endRevision, Pattern commitPattern) {
		SVNRepository repository = null;
		
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		} catch (SVNException svne) {
			System.err.println("Error for repository with location: " + url + ". Message: " + svne.getMessage());
			return null;
		}
		
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
		repository.setAuthenticationManager(authManager);
		SvnLogEntryHandler handler = new SvnLogEntryHandler(commitPattern);
		
		try {
			repository.log(new String[] { "" }, startRevision, endRevision, true, true, handler);
		} catch (SVNException svne) {
			System.out.println("Error retrieving log information for repository: " + url + ". Message: " + svne.getMessage());
			return null;
		}
		
		return handler.getEntries();
	}
	
	
	private void setup() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}
}
