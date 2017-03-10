/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.cfg;

import de.citec.csra.highlight.com.LightConnection;
import static de.citec.csra.highlight.com.LightConnection.Arg.ACTIVE;
import static de.citec.csra.highlight.com.LightConnection.Arg.RESET;
import rsb.InitializeException;

/**
 *
 * @author pholthau
 */
public class LightConfiguration extends HighlightTarget {

	public LightConfiguration(String name) throws InitializeException {
		LightConnection li = new LightConnection(name);
		super.setExecution(li, ACTIVE);
		super.setReset(li, RESET, 100);
	}
}
