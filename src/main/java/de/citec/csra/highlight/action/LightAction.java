/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.util.Remotes;
import de.citec.dal.remote.unit.AmbientLightRemote;
import de.citec.dal.remote.unit.DimmerRemote;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.InitializeException;
import rst.homeautomation.state.PowerStateType.PowerState;
import static rst.homeautomation.state.PowerStateType.PowerState.State.ON;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class LightAction implements Callable<Boolean> {

	private final static Logger log = Logger.getLogger(LightAction.class.getName());
	List<UnitConfig> units;
	private final long duration;

	public LightAction(String cfg, long duration) throws InitializeException {
		this.duration = duration;
		try {
			this.units = Remotes.get().getDevices().getUnitConfigsByLabel(cfg);
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
	public Boolean call() throws Exception {
		Map<UnitConfig, PowerState.State> states = new HashMap<>();
		for (UnitConfig unit : units) {
			log.log(Level.INFO, "Switching unit ''{0}'' to ''ON''.", unit.getLabel());
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
					return false;
			}
		}
		
		log.log(Level.INFO, "Sleeping {0}ms.", duration);
		Thread.sleep(duration);

		for (UnitConfig unit : units) {
			log.log(Level.INFO, "Switching unit ''{0}'' to ''OFF''.", unit.getLabel());
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
					return false;
			}
		}
		return true;
	}

}
