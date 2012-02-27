package com.github.r1j0.bugspot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.github.r1j0.bugspot.repository.LogEntries;
import com.github.r1j0.bugspot.repository.Repository;
import com.github.r1j0.bugspot.repository.RepositoryFactory;

public class Bugspot {

	private static final String NAME = Bugspot.class.getSimpleName();

	public static void main(String[] args) {
		CommandLineParser parser = new PosixParser();
		Options commandLineOptions = OptionsBuilder.build();
		CommandLine line = null;

		try {
			line = parser.parse(commandLineOptions, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage() + "\n");
			printHelp(commandLineOptions);
			System.exit(1);
		}

		//checkConditions(line, commandLineOptions);
		final Map<String, String> options = parseCommandLineOptions(line);

		Repository repository = RepositoryFactory.getInstance(options.get("type"), options.get("url"), options.get("username"), options.get("password")); 
		List<LogEntries> logEntries = repository.checkout(Long.valueOf(options.get("from_rev")), Long.valueOf(options.get("to_rev")));

//		Computate computate = new Computate(logEntries);
//		computate.compute();
//		Map<String, Double> hotspots = computate.getHotspots();
//
//		for (Entry<String, Double> entrySet : hotspots.entrySet()) {
//			System.out.println("PATH: " + entrySet.getKey() + " VALUE: " + entrySet.getValue());
//		}
	}


	private static Map<String, String> parseCommandLineOptions(final CommandLine line) {
		final Map<String, String> options = new HashMap<String, String>();
		options.put("url", line.getOptionValue("url"));
		options.put("type", line.getOptionValue("t", "svn"));
		options.put("username", line.getOptionValue("u", "anonymous"));
		options.put("password", line.getOptionValue("p", ""));
		String[] values = line.getOptionValues("r");
		options.put("from_rev", values.length > 0 ? values[0] : "1");
		options.put("to_rev", values.length > 1 ? values[1] : "HEAD");
		return options;
	}


	private static void checkConditions(CommandLine line, Options options) {
		if (line.hasOption("h")) {
			printHelp(options);
			System.exit(0);
		}

		if (line.hasOption("version")) {
			System.out.println("Version is and will be infinite.");
			System.exit(0);
		}

		if (!line.hasOption("url")) {
			System.out.println("No repository url has been given.");
			System.exit(0);
		}
	}


	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(NAME, options, true);
	}
}
