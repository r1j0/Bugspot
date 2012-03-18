	package com.github.r1j0.bugspot.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.r1j0.bugspot.repository.LogEntries;

public class FilterChain implements Filter {
	private static Log log = LogFactory.getLog(FilterChain.class);
	
	public List<Filter> filters = new ArrayList<Filter>();
	
	
	public FilterChain add(Filter filter) {
		filters.add(filter);
		return this;
	}
	
	public FilterChain remove(Filter filter) {
		filters.remove(filter);
		return this;
	}
	
	public FilterChain remove(int index) {
		filters.remove(index);
		return this;
	}
	
	public List<LogEntries> filter(List<LogEntries> logEntries, Properties properties) {
		List <LogEntries> filtered = new ArrayList<LogEntries>();
		
		log.info("starting with " + logEntries.size() + " commits");
		for(Filter f: filters) {
			filtered = f.filter(logEntries, properties);
			log.info(f.getClass().getName() + " filtered " + (logEntries.size() - filtered.size()) + " messages");
			logEntries = filtered;
		}
		
		return logEntries;
	}

}
