package com.github.r1j0.bugspot.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.r1j0.bugspot.repository.LogEntries;

public class MessageFilter implements Filter {
	private static Log log = LogFactory.getLog(MessageFilter.class);

	public List<LogEntries> filter(List<LogEntries> logEntries, Properties properties) {
		List<LogEntries> filtered = new ArrayList<LogEntries>();
		
		for (LogEntries logEntry: logEntries) {
			Matcher m = Pattern.compile(properties.getProperty("filter.MessageFilter.commit")).matcher(logEntry.getMessage());
			
			if (m.find()) {
				filtered.add(logEntry);
				log.trace("HIT:" + logEntry.getMessage());
			} else {
				log.trace("MISS:" + logEntry.getMessage());
			}
			
		}
		
		return filtered;
	}

}
