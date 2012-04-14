package com.github.r1j0.bugspot.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.r1j0.bugspot.repository.LogEntries;

/**
 * Bug prediction as supposed by google
 * <link>http://google-engtools.blogspot.de/2011/12/bug-prediction-at-google.html</link>
 */
public class GoogleBugAnalyzer implements Analyzer {
	private static final Log log = LogFactory.getLog(GoogleBugAnalyzer.class);
	
	/**
	 * Hotspot value for each file that occurred in a bugfix commit
	 */
	private final Map<String, Double> hotspots = new HashMap<String, Double>();
	
	private Pattern include = Pattern.compile(".*", Pattern.CASE_INSENSITIVE);
	private Pattern exclude = Pattern.compile("\\.txt|\\.xml$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * duration between now and first commit
	 */
	private Double scale = 0.;
	
	public Map<String, Double> analyze(List<LogEntries> list, Long first) {
		log.info("Analyzing " + list.size() + " bug fixing commits");
		
		if (list.isEmpty()) {
			return hotspots;
		}
		
		scale = Double.valueOf(System.currentTimeMillis()) -  first;
		
		for (LogEntries logEntries : list) {
			Map<String, String> logPath = logEntries.getLogPath();
			Double weight = weight(logEntries.getDate().getTime());
			
			for (Entry<String, String> entrySet : logPath.entrySet()) {
				String fullPath = entrySet.getValue();
				Matcher excludeMatcher = exclude.matcher(fullPath);
				Matcher includeMatcher = include.matcher(fullPath);
				
				if (!includeMatcher.find() || excludeMatcher.find()) {
					continue;
				}
				
				Double hotspot = hotspots.get(fullPath);
				
				if (hotspot == null) {
					hotspot = 0.;
				}
				
				hotspots.put(fullPath, hotspot + weight);
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
	 * @param commitDate date of a bug fixing commit
	 * @return weight
	 */
	private Double weight(Long commitDate) {
		Double t = 1 - ((Double.valueOf(System.currentTimeMillis()) - commitDate) / scale);
		return 1 / (1 + Math.exp((-12 * t) + 12));
	}


	public void setInclude(Pattern include) {
		this.include = include;
	}


	public void setExclude(Pattern exclude) {
		this.exclude = exclude;
	}
}
