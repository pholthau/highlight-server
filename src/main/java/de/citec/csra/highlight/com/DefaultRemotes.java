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
		RemoteServerConfig<PanTiltAngle> spot = new RemoteServerConfig();
		spot.setActiveRemote("/home/living/movinghead");
		spot.setStartMethod("setPanTilt");
		spot.setZero(PanTiltAngle.getDefaultInstance());
		spot.setParser(new PanTiltAngleParser());
		
		RemoteServerConfig<PanTiltAngle> gaze = new RemoteServerConfig();
		gaze.setActiveRemote("/meka/gaze");
		gaze.setStartMethod("setPanTilt");
		gaze.setZero(PanTiltAngle.getDefaultInstance());
		spot.setParser(new PanTiltAngleParser());
		
		RemoteServerConfig<String> gesture = new RemoteServerConfig();
		gesture.setActiveRemote("/meka/gesture");
		gesture.setStartMethod("setPose");

		
		RemoteServerConfig<String> sound = new RemoteServerConfig();
		sound.setActiveRemote("/home/audio/control/radio/");
		sound.setStartMethod("play");
		sound.setZero("");
		sound.setStopMethod("stop");

		RemoteMap.register(SPOT_LIGHT, spot);
		RemoteMap.register(GAZE, gaze);
		RemoteMap.register(GESTURE, gesture);
		RemoteMap.register(SOUND, sound);
	}
}
