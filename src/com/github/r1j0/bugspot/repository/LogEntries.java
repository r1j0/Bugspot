package com.github.r1j0.bugspot.repository;

import java.util.Date;
import java.util.Map;

public interface LogEntries {

	long getRevision();


	String getAuthor();


	Date getDate();


	String getMessage();


	Map<String, String> getLogPath();

}
