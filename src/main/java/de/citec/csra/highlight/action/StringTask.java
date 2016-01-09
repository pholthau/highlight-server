/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.arbitration.task.cli.RemoteTask;
import rsb.InitializeException;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class StringTask extends RemoteTask<String, String> {

	public StringTask(String scope, String cfg, long duration, long init, long wait) throws InitializeException {
		super(scope, String.class, String.class);
		String payload = cfg + ":" + duration;
		configure(payload, init, wait);
	}

}