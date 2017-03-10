/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import rsb.Factory;
import rsb.Informer;
import rsb.RSBException;

/**
 *
 * @author pholthau
 */
public class InformerConnection<T> implements RemoteConnection<T> {

	private final Informer<T> out;

	public InformerConnection(String scope) throws RSBException {
		this.out = Factory.getInstance().createInformer(scope);
		this.out.activate();
	}

	@Override
	public void send(T argument) throws RSBException {
		this.out.publish(argument);
	}

	@Override
	public String getAddress() {
		return this.out.getScope().toString();
	}

}
