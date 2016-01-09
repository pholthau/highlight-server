/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.util.StringParser;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class RemoteConfig<T> {
	
	private T zero;
	private StringParser<T> parser;

	public T getZero() {
		return zero;
	}

	public void setZero(T zero) {
		this.zero = zero;
	}

	public StringParser<T> getParser() {
		return parser;
	}

	public void setParser(StringParser<T> parser) {
		this.parser = parser;
	}
	
}
