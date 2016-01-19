/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import rsb.RSBException;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.*;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class DefaultRemotes {

	public static void load() throws RSBException {
		InformerConfig<String> gesture = new InformerConfig("/meka/posture_execution/", "/meka/posture_execution/", null);
		RemoteServerConfig<String> sound = new RemoteServerConfig("/home/audio/control/radio/", "play", null);

		RemoteMap.register(SPOT_LIGHT, new BeamerConfig());
		RemoteMap.register(GAZE, new MekaGazeConfig());
		RemoteMap.register(GESTURE, gesture);
		RemoteMap.register(SOUND, sound);
	}
}
