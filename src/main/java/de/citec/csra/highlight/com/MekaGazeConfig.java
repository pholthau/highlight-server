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
public class MekaGazeConfig extends InformerConfig<PanTiltAngle> implements Preparable<Boolean>, Finalizeable<Boolean> {

	
	//		informer "/meka/robotgazetools/set/pause" (pause/resume as payload)
//		listener "/meka/robotgazetools/get/pause" (true/false as payload)
	
	public MekaGazeConfig() throws RSBException {
		super("/meka/robotgaze/set/gaze", new PanTiltAngleParser());
	}

	@Override
	public String getPrepareInterface(){
		return "/meka/robotgaze/set/pause";
	}
	
	@Override
	public Boolean getPrepareArgument(){
		return true;
	}

	@Override
	public String getFinalizeInterface() {
		return "/meka/robotgazetools/set/pause";
	}

	@Override
	public Boolean getFinalizeArgument() {
		return false;
	}

}
