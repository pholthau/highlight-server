/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.util.Remotes;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dc.bco.dal.remote.unit.AmbientLightRemote;
import org.dc.bco.dal.remote.unit.DimmerRemote;
import org.dc.jul.exception.CouldNotPerformException;
import rsb.InitializeException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.communicationpatterns.TaskStateType;
import rst.homeautomation.state.PowerStateType.PowerState;
import static rst.homeautomation.state.PowerStateType.PowerState.State.ON;
import static rst.homeautomation.state.PowerStateType.PowerState.State.UNKNOWN;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.vision.HSVColorType.HSVColor;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class LightAction implements Callable<Boolean> {

	static {
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(TaskStateType.TaskState.getDefaultInstance()));
	}
	private final static Logger log = Logger.getLogger(LightAction.class.getName());

	List<UnitConfig> units;
	private final long duration;
	private final long interrupt_wait;

	public LightAction(String cfg, long duration, long interrupt_wait) throws InitializeException {
		this.duration = duration;
		this.interrupt_wait = interrupt_wait;
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
		Map<UnitConfig, HSVColor> colors = new HashMap<>();

		boolean success = true;
		for (UnitConfig unit : units) {
			switch (unit.getType()) {
				case AMBIENT_LIGHT:
					AmbientLightRemote light = Remotes.get().getAmbientLight(unit);
					states.put(unit, light.getPower().getValue());
					colors.put(unit, light.getColor());
					log.log(Level.INFO, "Set unit power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), "ON"});
					light.setPower(ON);
					log.log(Level.INFO, "Set unit color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), Color.BLUE});
					light.setColor(Color.BLUE);
					break;
				case DIMMER:
					DimmerRemote dimmer = Remotes.get().getDimmer(unit);
					states.put(unit, dimmer.getPower().getValue());
					dimmer.setPower(ON);
					break;
				default:
					success = false;
			}
		}
		try {
			log.log(Level.INFO, "Sleeping {0}ms.", duration);
			Thread.sleep(duration);
		} catch (InterruptedException ex) {
			log.log(Level.WARNING, "Sleeping interrupted, reseting NOW.");
			success = false;
		}
		long remaining = interrupt_wait;
		for (UnitConfig unit : units) {
			if (!UNKNOWN.equals(states.get(unit))) {
				switch (unit.getType()) {
					case AMBIENT_LIGHT:
						AmbientLightRemote light = Remotes.get().getAmbientLight(unit);

						log.log(Level.INFO, "Reset unit color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), colors.get(unit).toString().replaceAll("\n", " ")});
						Future<?> color = light.callMethodAsync("setColor", colors.get(unit));
						if (remaining > 0) {
							long now = System.currentTimeMillis();
							try {
								color.get(remaining, TimeUnit.MILLISECONDS);
							} catch (ExecutionException | TimeoutException ex) {
								log.log(Level.WARNING, "Sleeping cancelled, aborting NOW.", ex);
								success = false;
							}
							remaining -= System.currentTimeMillis() - now;
						} else {
							log.log(Level.WARNING, "Could not wait for method ''{0}'' at unit ''{1}'', no time left ({2}ms)", new Object[]{"setColor", unit.getLabel(), remaining});
							success = false;
						}
						PowerState ps1 = PowerState.newBuilder().setValue(states.get(unit)).build();
						log.log(Level.INFO, "Reset unit power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), states.get(unit)});
						Future<?> power = light.callMethodAsync("setPower", ps1);
						if (remaining > 0) {
							long now = System.currentTimeMillis();
							try {
								power.get(remaining, TimeUnit.MILLISECONDS);
							} catch (ExecutionException | TimeoutException ex) {
								log.log(Level.WARNING, "Sleeping cancelled, aborting NOW.", ex);
								success = false;
							}
							remaining -= System.currentTimeMillis() - now;
						} else {
							log.log(Level.WARNING, "Could not wait for method ''{0}'' at unit ''{1}'', no time left ({2}ms)", new Object[]{"setPower", unit.getLabel(), remaining});
							success = false;
						}
						light.requestStatus();
						break;

					case DIMMER:
						DimmerRemote dimmer = Remotes.get().getDimmer(unit);
						PowerState ps2 = PowerState.newBuilder().setValue(states.get(unit)).build();
						Future<?> dpower = dimmer.callMethodAsync("setPower", ps2);

						if (remaining > 0) {
							long now = System.currentTimeMillis();
							try {
								dpower.get(remaining, TimeUnit.MILLISECONDS);
							} catch (ExecutionException | TimeoutException ex) {
								log.log(Level.WARNING, "Sleeping cancelled, aborting NOW.", ex);
								success = false;
							}
							remaining -= System.currentTimeMillis() - now;
						} else {
							log.log(Level.WARNING, "Could not wait for method ''{0}'' at unit ''{1}'', no time left ({2}ms)", new Object[]{"setPower", unit.getLabel(), remaining});
							success = false;
						}
						
						dimmer.requestStatus();
						break;

					default:
						success = false;
				}
			} else {
				log.log(Level.INFO, "Skipping unit ''{0}'', previous state unknown.", unit.getLabel());
				success = false;
			}
		}
		return success;
	}

}
