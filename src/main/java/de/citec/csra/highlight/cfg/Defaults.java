/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.cfg;

import static de.citec.csra.highlight.cfg.TargetObject.*;
import de.citec.csra.highlight.com.InformerConnection;
import de.citec.csra.highlight.com.MethodCallConnection;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.InitializeException;
import rsb.RSBException;
import rst.geometry.SphericalDirectionFloatType.SphericalDirectionFloat;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.*;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class Defaults {

	private static final Map<TargetObject, Map<Modality, Highlightable>> CONFIGS = new EnumMap<>(TargetObject.class);

	public static void register(TargetObject tgt, Modality m, Highlightable conf) {
		if (!CONFIGS.containsKey(tgt)) {
			CONFIGS.put(tgt, new EnumMap<>(Modality.class));
		}
		CONFIGS.get(tgt).put(m, conf);
	}

	public static Highlightable get(TargetObject tgt, Modality m) {
		if (!CONFIGS.containsKey(tgt)) {
			CONFIGS.put(tgt, new EnumMap<>(Modality.class));
		}
		return CONFIGS.get(tgt).get(m);
	}

	private static SphericalDirectionFloat direction(long x, long y) {
		return SphericalDirectionFloat.newBuilder().setAzimuth(x).setElevation(y).build();
	}

	public static void loadDefaults() {

		InformerConnection gesture = null;
		try {
			gesture = new InformerConnection<>("/meka/posture_execution/");
		} catch (RSBException ex) {
			Logger.getLogger(Defaults.class.getName()).log(Level.SEVERE, null, ex);
		}
		MethodCallConnection sound = null;
		try {
			sound = new MethodCallConnection("/home/audio/control/radio/", "play");
		} catch (RSBException ex) {
			Logger.getLogger(Defaults.class.getName()).log(Level.SEVERE, null, ex);
		}

		for (Modality m : Modality.values()) {
			for (TargetObject t : TargetObject.values()) {
				String alt = "";
				String gst = "";
				String snd = "";
				SphericalDirectionFloat gaze = SphericalDirectionFloat.getDefaultInstance();
				SphericalDirectionFloat spot = SphericalDirectionFloat.getDefaultInstance();
				switch (t) {
					case ENTRANCE:
						alt = "Hallway_0";
						gaze = direction(70, 1);
						gst = "all pointing_kitchen";
						snd = "Waikiki.ogg";
						spot = direction(84, 33);
						break;
					case SURFACE:
						alt = "CeilingWindowLamp";
						gaze = direction(-55, -15);
						gst = "all pointing_screen";
						snd = "Waikiki.ogg";
						spot = direction(220, 90);
						break;
					case ZEN:
						alt = "SLampRight2";
						gaze = direction(-40, -5);
						gst = "all pointing_screen";
						snd = "Waikiki.ogg";
						spot = direction(280, 63);
						break;
					case PLANT:
						alt = "SLampRight2";
						gaze = direction(-35, -10);
						gst = "all pointing_screen";
						snd = "Waikiki.ogg";
						spot = direction(294, 74);
						break;
					case FLOBI:
						alt = "Hallway_0";
						gaze = direction(70, 1);
						gst = "all pointing_kitchen";
						snd = "Waikiki.ogg";
						spot = direction(84, 33);
						break;
					case MEKA:
						alt = "Table_0";
						gaze = null;
						gst = "all welcoming";
						snd = "Waikiki.ogg";
						spot = direction(140, 60);
						break;
					case TV:
						alt = "LLamp6";
						gaze = direction(-20, 1);
						gst = "all pointing_screen";
						snd = "Waikiki.ogg";
						spot = direction(335, 70);
						break;
					case WATER:
						alt = "CeilingLamp 1";
						gaze = direction(70, -10);
						gst = "all pointing_kitchen";
						snd = "Waikiki.ogg";
						spot = direction(118, 42);
						break;
					case CUPBOARD1:
						alt = "503";
						gaze = direction(70, 1);
						gst = "all pointing_kitchen";
						snd = "Waikiki.ogg";
						spot = direction(117, 31);
						break;
					case CUPBOARD2:
						alt = "505";
						gaze = direction(70, 1);
						gst = "all pointing_kitchen";
						snd = "Waikiki.ogg";
						spot = direction(109, 28);
						break;
					case DRAWER1:
						alt = "502";
						gaze = direction(70, -20);
						gst = "all pointing_kitchen";
						snd = "Waikiki.ogg";
						spot = direction(114, 45);
						break;
					case DRAWER2:
						alt = "507";
						gaze = direction(70, -20);
						gst = "all pointing_kitchen";
						snd = "Waikiki.ogg";
						spot = direction(114, 45);
						break;

				}
				switch (m) {
					case AMBIENT_LIGHT:
						try {
							register(t, m, new LightConfiguration(alt));
						} catch (InitializeException ex) {
							Logger.getLogger(Defaults.class.getName()).log(Level.SEVERE, null, ex);
						}
						break;
					case GAZE:
						try {
							register(t, m, new MekaGazeConfiguration(gaze));
						} catch (RSBException ex) {
							Logger.getLogger(Defaults.class.getName()).log(Level.SEVERE, null, ex);
						}
						break;
					case GESTURE:
						if (gesture != null) {
							register(t, m, new HighlightTarget().setExecution(gesture, gst));
						}
						break;
					case SOUND:
						if (sound != null) {
							register(t, m, new HighlightTarget().setExecution(sound, snd));
						}
						break;
					case SPOT_LIGHT:
						try {
							register(t, m, new BeamerConfiguration(spot));
						} catch (RSBException ex) {
							Logger.getLogger(Defaults.class.getName()).log(Level.SEVERE, null, ex);
						}
						break;
				}
			}
		}
	}
}
