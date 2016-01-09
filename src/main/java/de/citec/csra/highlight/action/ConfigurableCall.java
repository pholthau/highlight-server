/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import java.util.concurrent.Callable;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public abstract class ConfigurableCall<C, T> implements Callable<T> {

	private final C config;

	public C getConfig() {
		return config;
	}
	
	public ConfigurableCall(C config) {
		this.config = config;
	}

}
