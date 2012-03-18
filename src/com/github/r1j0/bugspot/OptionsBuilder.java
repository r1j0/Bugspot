package com.github.r1j0.bugspot;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class OptionsBuilder {

	public static Options build() {
		Options options = new Options();
		addPropertiesOption(options);
		addUsernameOption(options);
		addPasswordOption(options);
		addRepositoryTypeOption(options);
		addUrlOption(options);
		addRevisionRangeOption(options);
		addHelpOption(options);
		addVersionOption(options);
		return options;
	}

	private static void addPropertiesOption(Options options) {
		OptionBuilder.withDescription("config properties file");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("FILE");
		OptionBuilder.isRequired();
		options.addOption(OptionBuilder.create("c"));
	}

	private static void addUrlOption(Options options) {
		OptionBuilder.withArgName("url");
		OptionBuilder.withDescription("repository url");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("URL");
		options.addOption(OptionBuilder.create("url"));
	}


	private static void addUsernameOption(Options options) {
		OptionBuilder.withArgName("u");
		OptionBuilder.withLongOpt("username");
		OptionBuilder.withDescription("repository username");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("USERNAME");
		options.addOption(OptionBuilder.create("u"));
	}


	private static void addPasswordOption(Options options) {
		OptionBuilder.withArgName("p");
		OptionBuilder.withLongOpt("password");
		OptionBuilder.withDescription("repository password");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("PASSWORD");
		options.addOption(OptionBuilder.create("p"));
	}


	private static void addRepositoryTypeOption(Options options) {
		OptionBuilder.withArgName("t");
		OptionBuilder.withLongOpt("type");
		OptionBuilder.withDescription("repository type, e.g. svn or git");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("TYPE");
		options.addOption(OptionBuilder.create("t"));
	}
	
	private static void addRevisionRangeOption(Options options) {
		OptionBuilder.withArgName("r");
		OptionBuilder.withLongOpt("revision");
		OptionBuilder.withDescription("from revision[:to revision]");
		OptionBuilder.hasArgs(2);
		OptionBuilder.withValueSeparator(':');
		OptionBuilder.withArgName("FROM_REV[:TO_REV]");
		OptionBuilder.isRequired();
		options.addOption(OptionBuilder.create("r"));
	}


	private static void addVersionOption(Options options) {
		options.addOption(new Option("version", "print the version information and exit"));
	}


	private static void addHelpOption(Options options) {
		options.addOption(new Option("h", "help", false, "print this message"));
	}
}
