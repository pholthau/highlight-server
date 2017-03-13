/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.highlight.com.LightConnection.Arg;
import de.citec.csra.init.Remotes;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openbase.bco.dal.lib.layer.unit.ColorableLight;
import org.openbase.bco.dal.remote.unit.DimmerRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rsb.InitializeException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.communicationpatterns.TaskStateType;
import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.state.PowerStateType.PowerState.State;
import static rst.domotic.state.PowerStateType.PowerState.State.ON;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.vision.HSBColorType.HSBColor;

/**
 *
 * @author pholthau
 */
public class LightConnection implements RemoteConnection<Arg> {

	static {
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(TaskStateType.TaskState.getDefaultInstance()));
	}
	private final static Logger LOG = Logger.getLogger(LightConnection.class.getName());

	private UnitConfig unit;
	private State originalState;
	private HSBColor originalColor;

	public enum Arg {
		ACTIVE,
		RESET
	}

	private final HSBColor A_COLOR = HSBColor.newBuilder().setHue(210).setSaturation(100).setBrightness(100).build();

	public LightConnection(String cfg) throws InitializeException {
//		try {
//			List<UnitConfig> units = Remotes.get().getUnitRegistry().getUnitConfigsByLabel(cfg);
//			loop:
//			for (UnitConfig u : units) {
//				switch (u.getType()) {
//					case COLORABLE_LIGHT:
//					case DIMMER:
//						this.unit = u;
//						break loop;
//					default:
//						break;
//
//				}
//			}
//			if (this.unit == null) {
//				throw new IllegalArgumentException("no light with label '" + cfg + "' available");
//			}
//		} catch (InstantiationException | InterruptedException | CouldNotPerformException | IllegalArgumentException ex) {
//			throw new InitializeException(ex);
//		}
		this.unit = UnitConfig.getDefaultInstance();
	}

	@Override
	public void send(Arg argument) throws Exception {
		Arg arg = (Arg) argument;
		switch (unit.getType()) {
			case COLORABLE_LIGHT:
				ColorableLight light = Remotes.get().getColorableLight(unit, 500);
				switch (arg) {
					case ACTIVE:
						originalState = light.getPowerState().getValue();
						originalColor = light.getHSBColor();

						LOG.log(Level.INFO, "Set light color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), A_COLOR.toString().replaceAll("\n", " ")});
						light.setColor(A_COLOR);
					//implies power state on
					case RESET:

						LOG.log(Level.INFO, "Reset light color ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), originalColor.toString().replaceAll("\n", " ")});
						light.setColor(originalColor);
						LOG.log(Level.INFO, "Reset light power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), originalState.toString().replaceAll("\n", " ")});
						light.setPowerState(PowerState.newBuilder().setValue(originalState).build());
				}
				break;
			case DIMMER:
				DimmerRemote dimmer = Remotes.get().getDimmableLight(unit, 500);
				switch (arg) {
					case ACTIVE:
						originalState = dimmer.getPowerState().getValue();
						LOG.log(Level.INFO, "Set dimmer power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), ON.toString().replaceAll("\n", " ")});
						dimmer.setPowerState(PowerState.newBuilder().setValue(ON).build());
					case RESET:
						LOG.log(Level.INFO, "Reset dimmer power ''{0}'' to ''{1}''.", new Object[]{unit.getLabel(), originalState.toString().replaceAll("\n", " ")});
						dimmer.setPowerState(PowerState.newBuilder().setValue(originalState).build());
				}
				break;
			default:
				break;
		}
	}

	@Override
	public String getAddress() {
		try {
			return ScopeGenerator.generateStringRep(unit.getScope());
		} catch (CouldNotPerformException ex) {
			LOG.log(Level.WARNING, "Could not infer scope of unit ''{0}'' ({1}), returning null.", new Object[]{unit.getLabel(), ex});
			return null;
		}
	}
}
