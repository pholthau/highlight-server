/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.highlight.cfg.Configurable.Stage;
import static de.citec.csra.rst.util.StringRepresentation.shortString;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openbase.bco.dal.remote.unit.ColorableLightRemote;
import org.openbase.bco.dal.remote.unit.DimmerRemote;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.state.PowerStateType.PowerState.State;
import static rst.domotic.state.PowerStateType.PowerState.State.OFF;
import static rst.domotic.state.PowerStateType.PowerState.State.ON;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import static rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType.COLORABLE_LIGHT;
import static rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType.DIMMER;
import rst.vision.HSBColorType.HSBColor;

/**
 *
 * @author pholthau
 */
public class LightConnection implements RemoteConnection<Stage> {

	private final static Logger LOG = Logger.getLogger(LightConnection.class.getName());
	private final static long HA_TIMEOUT = 2000;

	private final String cfg;
	private UnitConfig unit;
	private State originalState;
	private HSBColor originalColor;

	private final HSBColor A_COLOR = HSBColor.newBuilder().setHue(210).setSaturation(100).setBrightness(100).build();
	private final HSBColor B_COLOR = HSBColor.newBuilder().setHue(180).setSaturation(100).setBrightness(100).build();

	private boolean init() throws InterruptedException {
		try {
			Registries.getUnitRegistry().waitForData(HA_TIMEOUT, MILLISECONDS);
			List<UnitConfig> units = new LinkedList<>();
			units.addAll(Registries.getUnitRegistry().getUnitConfigsByLabelAndUnitType(cfg, COLORABLE_LIGHT));
			units.addAll(Registries.getUnitRegistry().getUnitConfigsByLabelAndUnitType(cfg, DIMMER));
			switch (units.size()) {
				default:
					LOG.log(Level.WARNING, "Multiple units matching label ''{0}'', using ''{1}''.", new Object[]{cfg, units.get(0).getLabel()});
				case 1:
					unit = units.get(0);
					return true;
				case 0:
					LOG.log(Level.WARNING, "No units found with label ''{0}''.", cfg);
					return false;
			}
		} catch (CouldNotPerformException ex) {
			LOG.log(Level.WARNING, "Location registry not available.", ex);
			return false;
		}
	}

	public LightConnection(String cfg) throws InterruptedException {
		this.cfg = cfg;
		init();
	}

	@Override
	public void send(Stage argument) throws Exception {

		if (unit == null) {
			boolean ready = init();
			if (!ready) {
				return;
			}
		}

		switch (unit.getType()) {
			case COLORABLE_LIGHT:
				ColorableLightRemote light = Units.getFutureUnit(unit, true, ColorableLightRemote.class).get(HA_TIMEOUT, MILLISECONDS);
				switch (argument) {
					case INIT:
						break;
					case PREPARE:
						originalState = light.getPowerState().getValue();
						LOG.log(Level.INFO, "Storing light power ''{0}'' as ''{1}''.", new Object[]{unit.getLabel(), shortString(originalState)});
						originalColor = light.getHSBColor();
						LOG.log(Level.INFO, "Storing light color ''{0}'' as ''{1}''.", new Object[]{unit.getLabel(), shortString(originalColor)});
						LOG.log(Level.INFO, "Set light color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), shortString(A_COLOR)});
						light.setColor(B_COLOR).get(HA_TIMEOUT, TimeUnit.MILLISECONDS);
						break;
					case EXEC:
						LOG.log(Level.INFO, "Set light color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), shortString(A_COLOR)});
						light.setColor(A_COLOR).get(HA_TIMEOUT, TimeUnit.MILLISECONDS);
						//implies power state on
						break;
					case RESET:
						LOG.log(Level.INFO, "Reset light color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), shortString(originalColor)});
						light.setColor(originalColor).get(HA_TIMEOUT, TimeUnit.MILLISECONDS);
						LOG.log(Level.INFO, "Reset light power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), shortString(originalState)});
						light.setPowerState(PowerState.newBuilder().setValue(originalState).build()).get(HA_TIMEOUT, TimeUnit.MILLISECONDS);
						break;
				}
				break;
			case DIMMER:
				DimmerRemote dimmer = Units.getFutureUnit(unit, true, DimmerRemote.class).get(HA_TIMEOUT, MILLISECONDS);
				switch (argument) {
					case INIT:
						break;
					case PREPARE:
						originalState = dimmer.getPowerState().getValue();
						LOG.log(Level.INFO, "Storing dimmer power ''{0}'' as ''{1}''.", new Object[]{unit.getLabel(), shortString(originalState)});
						LOG.log(Level.INFO, "Set dimmer power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), shortString(OFF)});
						dimmer.setPowerState(PowerState.newBuilder().setValue(OFF).build());
						break;
					case EXEC:
						LOG.log(Level.INFO, "Set dimmer power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), shortString(ON)});
						dimmer.setPowerState(PowerState.newBuilder().setValue(ON).build());
						break;
					case RESET:
						LOG.log(Level.INFO, "Reset dimmer power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), shortString(originalState)});
						dimmer.setPowerState(PowerState.newBuilder().setValue(originalState).build()).get(HA_TIMEOUT, TimeUnit.MILLISECONDS);
						break;
				}
				break;
			default:
				break;
		}
	}

	@Override
	public String getAddress() {
		try {
			if (unit == null) {
				boolean ready = init();
				if (!ready) {
					return null;
				}
			}
			return ScopeGenerator.generateStringRep(unit.getScope());
		} catch (CouldNotPerformException | InterruptedException ex) {
			LOG.log(Level.WARNING, "Could not infer scope of unit ''{0}'' ({1}), returning null.", new Object[]{unit.getLabel(), ex});
			return null;
		}
	}
}
