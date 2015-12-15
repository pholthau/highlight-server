/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight;

import de.citec.csra.task.srv.TaskServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.hri.HighlightTargetType;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class HighlightService {

	private static String scope = "/home/highlight";
	private final static Logger LOG = Logger.getLogger(HighlightService.class.getName());

	static {
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(HighlightTargetType.HighlightTarget.getDefaultInstance()));
	}

	public static void main(String[] args) throws InitializeException, RSBException, InterruptedException, ParseException {

		Options opts = new Options();
		opts.addOption("scope", true, "RSB scope to listen to.\nDefault: '" + scope + "'");
		opts.addOption("help", false, "Print this help and exit");

		String footer = null;
//		String footer = "\nThe following sub-scopes are registered automatically:\n"
//				+ "\n.../preset for color presets:\n" + Arrays.toString(ColorConfig.values())
//				+ "\n.../color for color values:\n" + "HSV (comma separated)"
//				+ "\n.../power for power states:\n" + Arrays.toString(PowerState.State.values())
//				+ "\n.../history for history commands:\n" + Arrays.toString(ColorHistory.values());

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(opts, args);
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("csra-highlight-service [OPTION...]", "where OPTION includes:", opts, footer);
			System.exit(0);
		}

		String s = cmd.getOptionValue("scope");
		if (s != null) {
			scope = s;
		}
		scope = scope.replaceAll("/$", "");

		TaskServer server = new TaskServer(new Highlighter(scope));
		server.activate();
		LOG.log(Level.INFO, "Activated higlight service at scope ''{0}''", scope);

		server.waitForShutdown();
		server.deactivate();
	}
}
