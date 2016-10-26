/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.util.Remotes;
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
import org.openbase.bco.dal.lib.layer.unit.ColorableLight;
import org.openbase.bco.dal.remote.unit.ColorableLightRemote;
import org.openbase.bco.dal.remote.unit.DimmerRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import rsb.InitializeException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.communicationpatterns.TaskStateType;
import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.state.PowerStateType.PowerState.State;
import static rst.domotic.state.PowerStateType.PowerState.State.ON;
import static rst.domotic.state.PowerStateType.PowerState.State.UNKNOWN;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.vision.HSBColorType.HSBColor;

/**
 * TODO ServiceRemote instead of Units
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
	private final HSBColor HIGHLIGHT = HSBColor.newBuilder().setHue(210).setSaturation(100).setBrightness(100).build();

	private PowerState build(State state) {
		return PowerState.newBuilder().setValue(state).build();
	}

	public LightAction(String cfg, long duration, long interrupt_wait) throws InitializeException {
		this.duration = duration;
		this.interrupt_wait = interrupt_wait;
		try {
			this.units = Remotes.get().getUnitRegistry().getUnitConfigsByLabel(cfg);
			for (UnitConfig unit : units) {
				switch (unit.getType()) {
					case COLORABLE_LIGHT:
						Remotes.get().getColorableLight(unit);
						break;
					case DIMMER:
						Remotes.get().getDimmableLight(unit);
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
		Map<UnitConfig, HSBColor> colors = new HashMap<>();

		boolean success = true;
		for (UnitConfig unit : units) {
			switch (unit.getType()) {
				case COLORABLE_LIGHT:
					ColorableLight light = Remotes.get().getColorableLight(unit);
					states.put(unit, light.getPowerState().getValue());
					colors.put(unit, light.getHSBColor());
					log.log(Level.INFO, "Set unit power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), "ON"});
					light.setPowerState(build(ON));
					log.log(Level.INFO, "Set unit color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), HIGHLIGHT.toString().replaceAll("\n", " ")});
					light.setColor(HIGHLIGHT);
					break;
				case DIMMER:
					DimmerRemote dimmer = Remotes.get().getDimmableLight(unit);
					states.put(unit, dimmer.getPowerState().getValue());
					dimmer.setPowerState(build(ON));
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
					case COLORABLE_LIGHT:
						ColorableLightRemote light = Remotes.get().getColorableLight(unit);

						log.log(Level.INFO, "Reset unit color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), colors.get(unit).toString().replaceAll("\n", " ")});
						Future<?> color = light.setColor(colors.get(unit));
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
						
						log.log(Level.INFO, "Reset unit power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), states.get(unit)});
						Future<?> power = light.setPowerState(build(states.get(unit)));
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
						light.requestData();
						break;

					case DIMMER:
						DimmerRemote dimmer = Remotes.get().getDimmableLight(unit);
						Future<?> dpower = dimmer.setPowerState(build(states.get(unit)));

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

						dimmer.requestData();
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
