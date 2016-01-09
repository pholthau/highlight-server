/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import rsb.Factory;
import rsb.Informer;
import rsb.Listener;
import rsb.RSBException;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class InformerConfig<T> extends RemoteConfig<T> {

	private Informer informer;
	private Listener listener;

	public Informer getInformer() {
		return informer;
	}
	
	public void setActiveInformer(String scope) throws RSBException {
		Informer i = Factory.getInstance().createInformer(scope);
		i.activate();
		setInformer(i);
	}

	public void setInformer(Informer informer) {
		this.informer = informer;
	}

	public Listener getListener() {
		return listener;
	}
	
	public void setActiveListener(String scope) throws RSBException {
		Listener l = Factory.getInstance().createListener(scope);
		l.activate();
		setListener(l);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

}
