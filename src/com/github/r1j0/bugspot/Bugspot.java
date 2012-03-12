package com.github.r1j0.bugspot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.github.r1j0.bugspot.analyzer.Analyzer;
import com.github.r1j0.bugspot.analyzer.GoogleBugAnalyzer;
import com.github.r1j0.bugspot.repository.LogEntries;
import com.github.r1j0.bugspot.repository.Repository;
import com.github.r1j0.bugspot.repository.RepositoryFactory;

public class Bugspot {
	private static final String NAME = Bugspot.class.getSimpleName();
	private static Options options;
	
	public static void main(String[] args) {
		options = OptionsBuilder.build();
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;
		
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage() + "\n");
			printHelp(options);
			System.exit(1);
		}
		
		final Map<String, String> options = parseCommandLineOptions(line);
		
		Repository repository = RepositoryFactory.getInstance(options.get("type"), options.get("url"), options.get("username"), options.get("password"));
		List<LogEntries> logEntries = repository.checkout(
			Long.valueOf(options.get("from_rev")), 
			Long.valueOf(options.get("to_rev")),
			Pattern.compile(options.get("commit_pattern"))
		);
		
		Analyzer a = new GoogleBugAnalyzer();
		Map <String, Double> hotspots = a.analyze(logEntries);
		ArrayList <Map.Entry<String, Double> > as = new ArrayList <Map.Entry <String, Double > >( hotspots.entrySet() );
		
		Collections.sort( as , new Comparator<Map.Entry<String, Double>>() {
			public int compare( Map.Entry<String, Double> e1, Map.Entry<String, Double> e2 ) {
				Double first = e1.getValue();
				Double second = e2.getValue();
				// sort desc
				return second.compareTo(first);
			}
		});
		
		for (Entry<String, Double> entrySet : as.subList(0, 10)) {
			System.out.println("PATH: " + entrySet.getKey() + " VALUE: " + entrySet.getValue());
		}
	}


	private static Map<String, String> parseCommandLineOptions(final CommandLine line) {
		if (line.hasOption("h")) {
			printHelp(options);
			System.exit(0);
		}
		
		if (line.hasOption("version")) {
			System.out.println("Version is and will be infinite.");
			System.exit(0);
		}
		
		final Map<String, String> options = new HashMap<String, String>();
		options.put("url", line.getOptionValue("url"));
		options.put("type", line.getOptionValue("t", "svn"));
		options.put("username", line.getOptionValue("u", "anonymous"));
		options.put("password", line.getOptionValue("p", ""));
		options.put("commit_pattern", line.getOptionValue("c", "(bug|fix(es)?|close(s|d))").replace("\\", ""));
		String[] values = line.getOptionValues("r");
		options.put("from_rev", values.length > 0 ? values[0] : "1");
		options.put("to_rev", values.length > 1 ? values[1] : "HEAD");
		return options;
	}
	
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(NAME, options, true);
	}
}
