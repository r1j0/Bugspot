package com.github.r1j0.bugspot.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.r1j0.bugspot.repository.LogEntries;

public class MessageFilter implements Filter {

	public List<LogEntries> filter(List<LogEntries> logEntries, Properties properties) {
		List<LogEntries> filtered = new ArrayList<LogEntries>();
		
		for (LogEntries log: logEntries) {
			Matcher m = Pattern.compile(properties.getProperty("filter.MessageFilter.commit")).matcher(log.getMessage());
			
			if (m.find()) {
				filtered.add(log);
			}
		}
		System.out.println(filtered.size() + " messages left");
		return filtered;
	}

}
