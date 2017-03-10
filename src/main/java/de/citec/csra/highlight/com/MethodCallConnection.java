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
 * @author pholthau
 */
public class MethodCallConnection<T> implements RemoteConnection<T> {

	private final RemoteServer out;
	private final String method;

	public MethodCallConnection(String scope, String method) throws RSBException {
		this.method = method;
		this.out = Factory.getInstance().createRemoteServer(scope);
		this.out.activate();
	}

	@Override
	public void send(T argument) throws Exception {
		this.out.call(this.method, argument);
	}

	@Override
	public String getAddress() {
		return this.out.getScope().toString();
	}
}
