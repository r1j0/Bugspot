package com.github.r1j0.bugspot.analyzer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.r1j0.bugspot.repository.LogEntries;

public class GoogleBugAnalyzer implements Analyzer {
	/**
	 * Hotspot value for each file that occurred in a bugfix commit
	 */
	private Map<String, Double> hotspots = new HashMap<String, Double>();
	
	private Pattern ignorePattern = Pattern.compile("\\.txt|\\.xml$", Pattern.CASE_INSENSITIVE);
	/**
	 * delta between first and last commit
	 */
	private Double scale = 0.; 
	
	public Map<String, Double> analyze(List<LogEntries> list) {
		if (list.isEmpty()) {
			return hotspots;
		}
		
		scale = Double.valueOf(System.currentTimeMillis()) -  list.get(0).getDate().getTime();
		
		for (LogEntries logEntries : list) {
			Map<String, String> logPath = logEntries.getLogPath();

			for (Entry<String, String> entrySet : logPath.entrySet()) {
				String fullPath = entrySet.getValue();
				Matcher ignoreMatcher = ignorePattern.matcher(fullPath);
				
				if (ignoreMatcher.find()) {
					continue;
				}
				Double weight = hotspots.get(fullPath);
				
				if (weight == null) {
					weight = 0.;
				}
				
				Double bugSpotValue = weight(logEntries.getDate().getTime(), weight, fullPath);
				hotspots.put(fullPath, bugSpotValue);
			}
		}
		
		return hotspots;
	}
	
	
	public Map<String, Double> getHotspots() {
		return hotspots;
	}
	
	
	/**
	 * Weight function
	 * 
	 * @param lastEntry
	 * @param logEntries
	 * @param fullPath
	 * @return
	 */
	private Double weight(Long currentCommitDate, Double weight, String fullPath) {
		Double t = 1 - ((Double.valueOf(System.currentTimeMillis()) - currentCommitDate) / scale);
		weight += 1 / (1 + Math.exp((-12 * t) + 12));
		return weight;
	}

}
