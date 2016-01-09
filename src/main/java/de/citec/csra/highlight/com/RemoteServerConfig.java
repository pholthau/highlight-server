/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

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
	private String stopMethod;
	private String startMethod;

	public RemoteServer getRemote() {
		return remote;
	}
	
	public void setActiveRemote(String scope) throws RSBException {
		RemoteServer s = Factory.getInstance().createRemoteServer(scope);
		s.activate();
		setRemote(s);
	}

	public void setRemote(RemoteServer remote) {
		this.remote = remote;
	}

	public String getStopMethod() {
		return stopMethod;
	}

	public void setStopMethod(String method) {
		this.stopMethod = method;
	}

	public String getStartMethod() {
		return startMethod;
	}

	public void setStartMethod(String method) {
		this.startMethod = method;
	}

}
