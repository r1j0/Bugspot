package com.github.r1j0.bugspot.analyzer;

import java.util.List;
import java.util.Map;
import com.github.r1j0.bugspot.repository.LogEntries;

public interface Analyzer {

	/**
	 * Analyze a list of bug fix commits
	 * 
	 * @return List of buggy files with their respective score
	 */
	public Map<String, Double> analyze(List<LogEntries> list);
}
