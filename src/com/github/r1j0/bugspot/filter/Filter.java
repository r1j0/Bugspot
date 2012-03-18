package com.github.r1j0.bugspot.filter;

import java.util.List;
import java.util.Properties;

import com.github.r1j0.bugspot.repository.LogEntries;

public interface Filter {
	/**
	 * filter given list of log entries
	 * 
	 * @param logEntries list of log entries
	 * @param additional filter options
	 * @return filtered log entries
	 */
	public List<LogEntries> filter(List<LogEntries> logEntries, Properties properties);
}
