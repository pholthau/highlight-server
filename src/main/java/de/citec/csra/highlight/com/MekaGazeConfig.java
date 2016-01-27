/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.util.SphericalDirectionFloatParser;
import rsb.Factory;
import rsb.Informer;
import rsb.RSBException;
import rst.geometry.SphericalDirectionFloatType.SphericalDirectionFloat;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class MekaGazeConfig extends InformerConfig<SphericalDirectionFloat> implements Preparable<Informer, Boolean>, Resetable<Informer, SphericalDirectionFloat>, Finalizeable<Informer, Boolean> {

	final Informer pause;

	public MekaGazeConfig() throws RSBException {
		super("/meka/robotgaze/set/gaze", new SphericalDirectionFloatParser());
		this.pause = Factory.getInstance().createInformer("/meka/robotgaze/set/pause");
		this.pause.activate();
	}

	@Override
	public Informer getPrepareInterface() {
		return this.pause;
	}

	@Override
	public Boolean getPrepareArgument() {
		return true;
	}

	@Override
	public Informer getFinalizeInterface() {
		return this.pause;
	}

	@Override
	public Boolean getFinalizeArgument() {
		return false;
	}

	@Override
	public Informer getResetInterface() {
		return getInformer();
	}

	@Override
	public SphericalDirectionFloat getResetArgument() {
		return SphericalDirectionFloat.newBuilder().setAzimuth(0).setElevation(0).build();
	}

	@Override
	public long getSleepDuration() {
		return 1500;
	}

}
