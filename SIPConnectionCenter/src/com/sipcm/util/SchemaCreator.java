/**
 * 
 */
package com.sipcm.util;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import javax.persistence.Entity;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;

/**
 * @author Jack
 * 
 */
public class SchemaCreator {
	public static final String DEFAULT_BASE_PACKAGE = "com.sipcm";
	public static final String DEFAULT_DIALECT = "org.hibernate.dialect.MySQL5InnoDBDialect";
	public static final String DEFAULT_SEPARATER = ";";
	public static final int CREATE = 1;
	public static final int DROP = 2;
	public static final String OPTION_HELP = "h";
	public static final String OPTION_HELP_LONG = "help";
	public static final String OPTION_DIALECT = "d";
	public static final String OPTION_DIALECT_LONG = "dialect";
	public static final String OPTION_BASE_PACKAGE = "b";
	public static final String OPTION_BASE_PACKAGE_LONG = "basepackage";
	public static final String OPTION_OUTPUT = "o";
	public static final String OPTION_OUTPUT_LONG = "output";
	public static final String OPTION_DROP = "r";
	public static final String OPTION_DROP_LONG = "drop";
	public static final String OPTION_SEPARATER = "s";
	public static final String OPTION_SEPARATER_LONG = "separater";

	private static String[] generateSchemaScript(String basePackage,
			String dialectClassName, int operation) {
		Configuration config = new Configuration();
		Collection<Class<?>> clazzes = ClassUtils.findClasses(basePackage,
				Entity.class);

		Collection<Package> packages = ClassUtils.findPackages(basePackage,
				TypeDefs.class);
		for (Package p : packages) {
			config.addPackage(p.getName());
		}

		for (Class<?> clazz : clazzes) {
			config.addAnnotatedClass(clazz);
		}
		Dialect dialect;
		try {
			Class<?> clazz = Class.forName(dialectClassName);
			dialect = (Dialect) clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Invalid dialect class name. Class name: "
							+ dialectClassName, e);
		}
		switch (operation) {
		case CREATE:
			return config.generateSchemaCreationScript(dialect);
		case DROP:
			return config.generateDropSchemaScript(dialect);
		default:
			throw new IllegalArgumentException(
					"Invalid operation option. operation code: " + operation);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Option oHelp = new Option(OPTION_HELP, OPTION_HELP_LONG, false,
				"print this message");
		Option oDialect = new Option(OPTION_DIALECT, OPTION_DIALECT_LONG, true,
				"hibernate dialect class name (default: " + DEFAULT_DIALECT
						+ ")");
		Option oBasePackage = new Option(OPTION_BASE_PACKAGE,
				OPTION_BASE_PACKAGE_LONG, true, "base package name (default: "
						+ DEFAULT_BASE_PACKAGE + ")");
		Option oOutput = new Option(OPTION_OUTPUT, OPTION_OUTPUT_LONG, true,
				"output file name (default: output to console)");
		Option oDrop = new Option(OPTION_DROP, OPTION_DROP_LONG, false,
				"generate drop script");
		Option oSeparater = new Option(OPTION_SEPARATER, OPTION_SEPARATER_LONG,
				true, "line separator (default: \"" + DEFAULT_SEPARATER + "\")");
		Options options = new Options();
		options.addOption(oHelp).addOption(oDialect).addOption(oBasePackage)
				.addOption(oOutput).addOption(oDrop).addOption(oSeparater);
		CommandLineParser parser = new GnuParser();
		String dialect;
		String basePackage;
		String separater;
		String output;
		int operation;
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption(OPTION_HELP)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("SchemaCreator", options);
				System.exit(0);
			}
			if (line.hasOption(OPTION_DIALECT)) {
				dialect = line.getOptionValue(OPTION_DIALECT);
			} else {
				dialect = DEFAULT_DIALECT;
			}
			if (line.hasOption(OPTION_BASE_PACKAGE)) {
				basePackage = line.getOptionValue(OPTION_BASE_PACKAGE);
			} else {
				basePackage = DEFAULT_BASE_PACKAGE;
			}
			if (line.hasOption(OPTION_OUTPUT)) {
				output = line.getOptionValue(OPTION_OUTPUT);
			} else {
				output = null;
			}
			if (line.hasOption(OPTION_DROP)) {
				operation = DROP;
			} else {
				operation = CREATE;
			}
			if (line.hasOption(OPTION_SEPARATER)) {
				separater = line.getOptionValue(OPTION_SEPARATER);
			} else {
				separater = DEFAULT_SEPARATER;
			}
			String[] strs = generateSchemaScript(basePackage, dialect,
					operation);
			PrintStream ps = null;
			try {
				if (output != null) {
					File file = new File(output);
					ps = new PrintStream(file);
				} else {
					ps = System.out;
				}
				for (String s : strs) {
					StringBuilder sb = new StringBuilder(s);
					if (separater != null) {
						sb.append(separater);
					}
					ps.println(sb.toString());
				}
			} catch (Exception e) {
				System.err.println("Cannot output generated script. Reason: "
						+ e.getMessage());
			} finally {
				if (ps != null) {
					ps.close();
				}
			}
		} catch (ParseException e) {
			System.err.println("Parsing failed. Reason: " + e.getMessage());
		}
	}
}
