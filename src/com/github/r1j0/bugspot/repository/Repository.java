package com.github.r1j0.bugspot.repository;

import java.util.List;
import java.util.regex.Pattern;

public interface Repository {

	public static final char TYPE_ADDED = 'A';
	public static final char TYPE_DELETED = 'D';
	public static final char TYPE_MODIFIED = 'M';
	public static final char TYPE_REPLACED = 'R';

	
	/**
	 * checkout from startRevision to HEAD
	 * 
	 * @param startRevision
	 * @return log entries
	 */
	List<LogEntries> checkout(long startRevision);

	/**
	 * checkout from startRevision to endRevision
	 * 
	 * @param startRevision
	 * @param endRevision
	 * @return log entries
	 */
	List<LogEntries> checkout(long startRevision, long endRevision);
	
	/**
	 * checkout from startRevision to endRevision
	 * 
	 * @param startRevision
	 * @param endRevision
	 * @param commitPattern 
	 * @return log entries
	 */
	List<LogEntries> checkout(long startRevision, long endRevision, Pattern commitPattern);
}
