package com.github.r1j0.bugspot.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.r1j0.bugspot.repository.LogEntries;

public class GoogleBugAnalyzer implements Analyzer {
	private Map<String, Double> hotspots = new HashMap<String, Double>();
	private Pattern ignorePattern = Pattern.compile("\\.txt|\\.xml$", Pattern.CASE_INSENSITIVE);
	
	public Map<String, Double> analyze(List<LogEntries> list) {
		if (list.isEmpty()) {
			return hotspots;
		}
		
		LogEntries lastEntry = list.get(list.size() - 1);

		for (LogEntries logEntries : list) {
			Map<String, String> logPath = logEntries.getLogPath();

			for (Entry<String, String> entrySet : logPath.entrySet()) {
				String fullPath = entrySet.getValue();

				Matcher ignoreMatcher = ignorePattern.matcher(fullPath);

				if (ignoreMatcher.find()) {
					continue;
				}
				
				Double bugSportValue = calculateBugSpot(lastEntry, logEntries, fullPath);
				hotspots.put(fullPath, bugSportValue);
			}
		}
		
		return hotspots;
	}
	
	
	public Map<String, Double> getHotspots() {
		return hotspots;
	}
	
	
	private Double calculateBugSpot(LogEntries lastEntry, LogEntries logEntries, String fullPath) {
		float t = 1 - (((System.currentTimeMillis() - logEntries.getDate().getTime()) / (System.currentTimeMillis() - lastEntry.getDate().getTime())) / 1000);

		Double oldBugSpotValue = hotspots.get(fullPath);
		Double newBugSpotValue;

		if (oldBugSpotValue != null) {
			newBugSpotValue = oldBugSpotValue + (1 / (1 + Math.exp((-12 * t) + 12)));
		} else {
			newBugSpotValue = 1 / (1 + Math.exp((-12 * t) + 12));
		}

		return newBugSpotValue;
	}

}
