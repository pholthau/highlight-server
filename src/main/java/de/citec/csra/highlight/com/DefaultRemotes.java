/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import de.citec.csra.util.PanTiltAngleParser;
import rsb.RSBException;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.*;
import rst.spatial.PanTiltAngleType.PanTiltAngle;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class DefaultRemotes {

	public static void load() throws RSBException {
		RemoteServerConfig<PanTiltAngle> spot = new BeamerConfig();
		RemoteServerConfig<PanTiltAngle> gaze = new RemoteServerConfig("/meka/gaze", "setPanTilt", new PanTiltAngleParser());
		RemoteServerConfig<String> gesture = new RemoteServerConfig("/meka/gesture", "setPose", null);
		RemoteServerConfig<String> sound = new RemoteServerConfig("/home/audio/control/radio/", "play", null);

		RemoteMap.register(SPOT_LIGHT, spot);
		RemoteMap.register(GAZE, gaze);
		RemoteMap.register(GESTURE, gesture);
		RemoteMap.register(SOUND, sound);
	}
}
