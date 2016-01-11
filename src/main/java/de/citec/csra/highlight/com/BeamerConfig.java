/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.util.PanTiltAngleParser;
import rsb.RSBException;
import rst.spatial.PanTiltAngleType.PanTiltAngle;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class BeamerConfig extends RemoteServerConfig<PanTiltAngle> implements Preparable<Boolean>, Resetable<PanTiltAngle> {

	public BeamerConfig() throws RSBException {
		super("/home/living/movinghead","setPanTilt", new PanTiltAngleParser());
	}

	@Override
	public String getPrepareInterface(){
		return "setShutterOpen";
	}
	
	@Override
	public Boolean getPrepareArgument(){
		return true;
	}

	@Override
	public String getResetInterface() {
		return "setPanTilt";
	}

	@Override
	public PanTiltAngle getResetArgument() {
		return PanTiltAngle.getDefaultInstance();
	}

}
