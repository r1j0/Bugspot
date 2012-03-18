package com.github.r1j0.bugspot.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.github.r1j0.bugspot.repository.LogEntries;

public class FilterChain implements Filter {
	
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
		for(Filter f: filters) {
			logEntries = f.filter(logEntries, properties);
		}
		
		return logEntries;
	}

}
