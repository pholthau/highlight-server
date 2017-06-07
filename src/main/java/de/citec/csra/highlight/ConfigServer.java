/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight;

import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.Event;
import rsb.Factory;
import rsb.RSBException;
import rsb.patterns.LocalServer;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class ConfigServer {

	private final static Logger LOG = Logger.getLogger(ConfigServer.class.getName());
	private static String token;

	public static String getToken() {
		return token;
	}

	private final String scope;

	public ConfigServer(String scope) {
		this.scope = scope;
	}

	public void execute() throws RSBException {
		LocalServer server = Factory.getInstance().createLocalServer(scope);
		server.addMethod("setToken", (e) -> {
			return setToken(e);
		});
		server.activate();
		LOG.log(Level.INFO, "Config server listening at ''{0}''.", server.getScope());
	}

	public Event setToken(Event e) {
		if (e.getData() instanceof String) {
			String tk = (String) e.getData();
			if(tk.length() != 0){
				token = tk;
			} else {
				token = null;
			}
			LOG.log(Level.INFO, "Updated permission token to ''{0}''.", token);
			return new Event(Boolean.class, true);
		} else if(e.getData() instanceof Void){
			token = null;
			LOG.log(Level.INFO, "Updated permission token to ''{0}''.", token);
			return new Event(Boolean.class, true);
		} else {
			LOG.log(Level.WARNING, "Invalid permission token ''{0}'' of class ''{1}''.", new Object[]{e.getData(), e.getClass()});
			return new Event(Boolean.class, false);
		}
	}
}
