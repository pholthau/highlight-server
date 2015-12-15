/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.arbitration.task.cli.RemoteTask;
import de.citec.csra.highlight.Target;
import rsb.InitializeException;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class SoundAction extends RemoteTask<String, String> {

	public SoundAction(Target tgt, long duration, long init, long wait) throws InitializeException {
		super("/sound/action", String.class, String.class);
		String payload = tgt.name() + ":" + duration;
		configure(payload, init, wait);
	}

}

