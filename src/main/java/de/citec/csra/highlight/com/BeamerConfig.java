/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.util.SphericalDirectionFloatParser;
import rsb.RSBException;
import rst.geometry.SphericalDirectionFloatType.SphericalDirectionFloat;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class BeamerConfig extends RemoteServerConfig<SphericalDirectionFloat> implements Preparable<String, Boolean>, Resetable<String, SphericalDirectionFloat>, Finalizeable<String, Boolean> {

	public BeamerConfig() throws RSBException {
		super("/home/living/movinghead", "setPanTilt", new SphericalDirectionFloatParser());
	}

	@Override
	public String getPrepareInterface() {
		return "setShutterAndLampState";
	}

	@Override
	public Boolean getPrepareArgument() {
		return true;
	}

	@Override
	public String getResetInterface() {
		return "setPanTilt";
	}

	@Override
	public SphericalDirectionFloat getResetArgument() {
		return SphericalDirectionFloat.newBuilder().setAzimuth(180).setElevation(40).build();
	}

	@Override
	public String getFinalizeInterface() {
		return "setShutterState";
	}

	@Override
	public Boolean getFinalizeArgument() {
		return false;
	}

	@Override
	public long getSleepDuration() {
		return 0;
	}

}
