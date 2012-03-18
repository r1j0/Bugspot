package com.github.r1j0.bugspot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.r1j0.bugspot.analyzer.Analyzer;
import com.github.r1j0.bugspot.analyzer.GoogleBugAnalyzer;
import com.github.r1j0.bugspot.filter.Filter;
import com.github.r1j0.bugspot.filter.FilterChain;
import com.github.r1j0.bugspot.repository.LogEntries;
import com.github.r1j0.bugspot.repository.Repository;
import com.github.r1j0.bugspot.repository.RepositoryFactory;

public class Bugspot {
	private static Log log = LogFactory.getLog(Bugspot.class);
	private static Options options;
	public static Properties properties = new Properties();
	
	public static void main(String[] args) {
		options = OptionsBuilder.build();
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;
		
		try {
			line = parser.parse(options, args);
			parseCommandLineOptions(line);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			printHelp(options);
			System.exit(1);
		}
		
		Repository repository = RepositoryFactory.getInstance(
			properties.getProperty("repository.type"), 
			properties.getProperty("repository.url"), 
			properties.getProperty("repository.username"), 
			properties.getProperty("repository.password")
		);
		
		// TODO: cache logEntries
		List<LogEntries> logEntries = repository.checkout(
			Long.valueOf(properties.getProperty("repository.from_rev")), 
			Long.valueOf(properties.getProperty("repository.to_rev"))
		);
		
		// save time of first commit in range
		Long firstCommit = logEntries.get(0).getDate().getTime();
		FilterChain chain = new FilterChain();
		String[] filters = properties.getProperty("filters").split(",");
		
		for (String filter: filters) {
			try {
				Filter f = (Filter)Class.forName(filter.trim()).newInstance();
				chain.add(f);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		
		logEntries = chain.filter(logEntries, properties);
		Analyzer a = new GoogleBugAnalyzer();
		a.setInclude(Pattern.compile(properties.getProperty("analyzer.include")));
		a.setExclude(Pattern.compile(properties.getProperty("analyzer.exclude")));
		Map <String, Double> hotspots = a.analyze(logEntries, firstCommit);
		ArrayList <Map.Entry<String, Double> > sortedHotspots = new ArrayList <Map.Entry <String, Double > >( hotspots.entrySet() );
		
		Collections.sort( sortedHotspots , new Comparator<Map.Entry<String, Double>>() {
			public int compare( Map.Entry<String, Double> e1, Map.Entry<String, Double> e2 ) {
				Double first = e1.getValue();
				Double second = e2.getValue();
				return second.compareTo(first);
			}
		});
		
		log.debug("hotspot size: " +sortedHotspots.size());
		for (Entry<String, Double> entrySet : sortedHotspots.subList(0, Math.min(sortedHotspots.size(), 10))) {
			System.out.println("PATH: " + entrySet.getKey() + " VALUE: " + entrySet.getValue());
		}
	}


	private static void parseCommandLineOptions(final CommandLine line) {
		if (line.hasOption("h")) {
			printHelp(options);
			System.exit(0);
		}
		
		if (line.hasOption("version")) {
			log.info("Version is and will be infinite.");
			System.exit(0);
		}
		
		String propertiesFile = line.getOptionValue("c", "bugspot.properties");
		
		try {
			properties.load(new FileInputStream(propertiesFile));
		} catch (FileNotFoundException e) {
			log.fatal("File not found: " + propertiesFile);
			System.exit(1);
		} catch (IOException e1) {
			log.fatal("unable to read " + propertiesFile);
			System.exit(1);
		}
		
		if (line.hasOption("url")) {
			properties.setProperty("repository.url", line.getOptionValue("url"));
		}
		
		if (line.hasOption("type")) {
			properties.setProperty("repository.type", line.getOptionValue("t"));
		}
		
		if (line.hasOption("username")) {
			properties.setProperty("repository.username", line.getOptionValue("u"));
		}
		
		if (line.hasOption("password")) {
			properties.setProperty("repository.password", line.getOptionValue("p"));
		}
		
		if (line.hasOption("password")) {
			properties.setProperty("repository.password", line.getOptionValue("p"));
		}

		String[] values = line.getOptionValues("r");
		properties.setProperty("repository.from_rev", values.length > 0 ? values[0] : "1");
		String toRev = values.length > 1 ? values[1] : "-1";
		properties.setProperty("repository.to_rev", toRev.toUpperCase().equals("HEAD") ? "-1" : toRev);
	}
	
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(Bugspot.class.getSimpleName(), options, true);
	}
}
