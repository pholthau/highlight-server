/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.tgt;

import static rst.hri.HighlightTargetType.HighlightTarget.Modality.AMBIENT_LIGHT;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.GAZE;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.GESTURE;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.SOUND;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.SPOT_LIGHT;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class DefaultTargets {

	public static void load() {

		TargetConfig tv = new TargetConfig();
		tv.setDescription(AMBIENT_LIGHT, "Llamp6").
				setDescription(GAZE, "-20,1").
				setDescription(GESTURE, "all pointing_screen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "335,70");

		TargetConfig entrance = new TargetConfig();
		entrance.setDescription(AMBIENT_LIGHT, "Hallway_0").
				setDescription(GAZE, "70,1").
				setDescription(GESTURE, "all pointing_kitchen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "84,33");

		TargetConfig surface = new TargetConfig();
		surface.setDescription(AMBIENT_LIGHT, "MediaHue").
				setDescription(GAZE, "-55,-15").
				setDescription(GESTURE, "all pointing_screen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "220,90");

		TargetConfig zen = new TargetConfig();
		zen.setDescription(AMBIENT_LIGHT, "SLampRight2").
				setDescription(GAZE, "-40,-5").
				setDescription(GESTURE, "all pointing_screen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "280,63");

		TargetConfig plant = new TargetConfig();
		plant.setDescription(AMBIENT_LIGHT, "SLampRight2").
				setDescription(GAZE, "-35,-10").
				setDescription(GESTURE, "all pointing_screen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "294,74");

		TargetConfig flobi = new TargetConfig();
		flobi.setDescription(AMBIENT_LIGHT, "Hallway_0").
				setDescription(GAZE, "70,1").
				setDescription(GESTURE, "all pointing_kitchen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "84,33");

		TargetConfig meka = new TargetConfig();
		meka.setDescription(AMBIENT_LIGHT, "Table_0").
				setDescription(GAZE, null).
				setDescription(GESTURE, "all welcoming").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "140,60");

		TargetConfig cupboard1 = new TargetConfig();
		cupboard1.setDescription(AMBIENT_LIGHT, "503").
				setDescription(GAZE, "70,1").
				setDescription(GESTURE, "all pointing_kitchen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "117,31");

		TargetConfig cupboard2 = new TargetConfig();
		cupboard2.setDescription(AMBIENT_LIGHT, "505").
				setDescription(GAZE, "70,1").
				setDescription(GESTURE, "all pointing_kitchen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "109,28");

		TargetConfig drawer1 = new TargetConfig();
		drawer1.setDescription(AMBIENT_LIGHT, "502").
				setDescription(GAZE, "70,-20").
				setDescription(GESTURE, "all pointing_kitchen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "114,45");

		TargetConfig drawer2 = new TargetConfig();
		drawer2.setDescription(AMBIENT_LIGHT, "507").
				setDescription(GAZE, "70,-20").
				setDescription(GESTURE, "all pointing_kitchen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "114,45");
		
		TargetConfig water = new TargetConfig();
		drawer2.setDescription(AMBIENT_LIGHT, "CeilingLamp_1").
				setDescription(GAZE, "70,-15").
				setDescription(GESTURE, "all pointing_kitchen").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "118,42");

		TargetMap.register(Target.TV, tv);
		TargetMap.register(Target.ENTRANCE, entrance);
		TargetMap.register(Target.SURFACE, surface);
		TargetMap.register(Target.PLANT, plant);
		TargetMap.register(Target.FLOBI, flobi);
		TargetMap.register(Target.MEKA, meka);
		TargetMap.register(Target.ZEN, zen);
		TargetMap.register(Target.CUPBOARD1, cupboard1);
		TargetMap.register(Target.CUPBOARD2, cupboard2);
		TargetMap.register(Target.DRAWER1, drawer1);
		TargetMap.register(Target.DRAWER2, drawer2);
		TargetMap.register(Target.WATER, water);
	}

}
