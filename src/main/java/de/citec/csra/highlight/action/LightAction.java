/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.util.Remotes;
import de.citec.csra.highlight.Target;
import de.citec.dal.remote.unit.AmbientLightRemote;
import de.citec.dal.remote.unit.DimmerRemote;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import rsb.InitializeException;
import rst.communicationpatterns.TaskStateType.TaskState.State;
import static rst.communicationpatterns.TaskStateType.TaskState.State.COMPLETED;
import rst.homeautomation.state.PowerStateType.PowerState;
import static rst.homeautomation.state.PowerStateType.PowerState.State.ON;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class LightAction implements Callable<State> {

	private final Map<Target, String> labels = new HashMap<>();
	List<UnitConfig> units;
	private final long duration;

	public LightAction(Target tgt, long duration) throws InitializeException {
		this.duration = duration;
		this.labels.put(Target.TV, "Llamp6");
		this.labels.put(Target.Flobi, "Hallway_0");
		this.labels.put(Target.Meka, "Living_0");
		this.labels.put(Target.Zen, "Slamp2");

		try {
			this.units = Remotes.get().getDevices().getUnitConfigsByLabel(labels.get(tgt));
			for (UnitConfig unit : units) {
				switch (unit.getType()) {
					case AMBIENT_LIGHT:
						Remotes.get().getAmbientLight(unit);
						break;
					case DIMMER:
						Remotes.get().getDimmer(unit);
						break;
					default:
						throw new IllegalArgumentException("unsupported type '" + unit.getLabel() + "'");
				}
			}
		} catch (InstantiationException | InterruptedException | CouldNotPerformException | IllegalArgumentException ex) {
			throw new InitializeException(ex);
		}

	}

	@Override
	public State call() throws Exception {
		Map<UnitConfig, PowerState.State> states = new HashMap<>();
		for (UnitConfig unit : units) {
			switch (unit.getType()) {
				case AMBIENT_LIGHT:
					AmbientLightRemote light = Remotes.get().getAmbientLight(unit);
					states.put(unit, light.getPower().getValue());
					light.setPower(ON);
					break;
				case DIMMER:
					DimmerRemote dimmer = Remotes.get().getDimmer(unit);
					states.put(unit, dimmer.getPower().getValue());
					dimmer.setPower(ON);
					break;
				default:
					throw new IllegalArgumentException("unsupported type '" + unit.getLabel() + "'");
			}
		}
		Thread.sleep(duration);
		
		for (UnitConfig unit : units) {
			switch (unit.getType()) {
				case AMBIENT_LIGHT:
					AmbientLightRemote light = Remotes.get().getAmbientLight(unit);
					light.setPower(states.get(unit));
					break;
				case DIMMER:
					DimmerRemote dimmer = Remotes.get().getDimmer(unit);
					dimmer.setPower(states.get(unit));
					break;
				default:
					throw new IllegalArgumentException("unsupported type '" + unit.getLabel() + "'");
			}
		}
		return COMPLETED;
	}

}
