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
		args = new String[] { "-h" };
//		args = new String[] { "-url", "http://svn.apache.org/repos/asf/hadoop/common/trunk/hadoop-common-project", "-u", "anonymous", "-p", "anonymous" };
		/**
		 * <code>
		 * args = new String[] { "-url", "http://svn.apache.org/repos/asf/hadoop/common/trunk/hadoop-common-project", "-u", "anonymous", "-p", "anonymous" };
		 * </code>
		 */
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

		checkConditions(line, commandLineOptions);
		final Map<String, String> options = parseCommandLineOptions(line);

		Repository repository = RepositoryFactory.getInstance(options.get("type"), options.get("url"), options.get("username"), options.get("password"));
		List<LogEntries> logEntries = repository.checkout(1200180, 1241260);

		Computate computate = new Computate(logEntries);
		computate.compute();
		Map<String, Double> hotspots = computate.getHotspots();

		for (Entry<String, Double> entrySet : hotspots.entrySet()) {
			System.out.println("PATH: " + entrySet.getKey() + " VALUE: " + entrySet.getValue());
		}
	}


	private static Map<String, String> parseCommandLineOptions(final CommandLine line) {
		final Map<String, String> options = new HashMap<String, String>();

		options.put("url", line.getOptionValue("url"));
		options.put("username", "anonymous");
		options.put("password", "anonymous");
		options.put("type", "svn");

		if (line.hasOption("u")) {
			options.put("username", line.getOptionValue("u"));
		}

		if (line.hasOption("p")) {
			options.put("password", line.getOptionValue("p"));
		}

		if (line.hasOption("t")) {
			options.put("type", line.getOptionValue("t"));
		}

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
		formatter.printHelp(NAME, options);
	}
}
