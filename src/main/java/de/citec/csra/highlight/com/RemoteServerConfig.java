/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.util.StringParser;
import rsb.Factory;
import rsb.RSBException;
import rsb.patterns.RemoteServer;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class RemoteServerConfig<T> extends RemoteConfig<T> {

	private RemoteServer remote;
	private String executeMethod;
	
//	public RemoteServerConfig(){}
	
	public RemoteServerConfig(RemoteServer remote, String executeMethod, StringParser<T> parser) throws RSBException{
		setRemote(remote);
		setExecuteMethod(executeMethod);
		setParser(parser);
	}
	
	public RemoteServerConfig(String scope, String executeMethod, StringParser<T> parser) throws RSBException{
		setActiveRemote(scope);
		setExecuteMethod(executeMethod);
		setParser(parser);
	}

	public RemoteServer getRemote() {
		return remote;
	}
	
	public final void setActiveRemote(String scope) throws RSBException {
		RemoteServer s = Factory.getInstance().createRemoteServer(scope);
		s.activate();
		setRemote(s);
	}

	public final void setRemote(RemoteServer remote) {
		this.remote = remote;
	}

	public String getExecuteMethod() {
		return executeMethod;
	}

	public final void setExecuteMethod(String method) {
		this.executeMethod = method;
	}
}
