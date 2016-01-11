/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.util.StringParser;
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
	
	public InformerConfig(Informer informer, StringParser<T> parser) throws RSBException{
		setInformer(informer);
		setParser(parser);
	}
	
	public InformerConfig(String scope, StringParser<T> parser) throws RSBException{
		setActiveInformer(scope);
		setParser(parser);
	}

	public Informer getInformer() {
		return informer;
	}
	
	public final void setActiveInformer(String scope) throws RSBException {
		Informer i = Factory.getInstance().createInformer(scope);
		i.activate();
		setInformer(i);
	}

	public final void setInformer(Informer informer) {
		this.informer = informer;
	}

	public Listener getListener() {
		return listener;
	}
	
	public final void setActiveListener(String scope) throws RSBException {
		Listener l = Factory.getInstance().createListener(scope);
		l.activate();
		setListener(l);
	}

	public final void setListener(Listener listener) {
		this.listener = listener;
	}

}
