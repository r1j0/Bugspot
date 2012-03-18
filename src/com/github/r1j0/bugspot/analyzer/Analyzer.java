package com.github.r1j0.bugspot.analyzer;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.github.r1j0.bugspot.repository.LogEntries;

public interface Analyzer {

	/**
	 * Analyze a list of bug fix commits
	 * 
	 * @param list of log entries
	 * @param time of first commit
	 * @return List of buggy files with their respective score
	 */
	public Map<String, Double> analyze(List<LogEntries> list, Long firstCommit);
	
	/**
	 * include files when analyzing 
	 * 
	 * @param include include pattern
	 */
	public void setInclude(Pattern include);
	
	/**
	 * ignore files when analyzing 
	 * 
	 * @param exclude exclude pattern
	 */
	public void setExclude(Pattern exclude);
}
