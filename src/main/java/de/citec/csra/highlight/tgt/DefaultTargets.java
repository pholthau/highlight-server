/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.tgt;

import static rst.hri.HighlightTargetType.HighlightTarget.Modality.*;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class DefaultTargets {
	
	public static void load(){
		
		TargetConfig tv = new TargetConfig();
		tv.setDescription(AMBIENT_LIGHT, "Llamp6").
				setDescription(GAZE, "120,120").
				setDescription(GESTURE, "120,120").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "120,120");
		
		TargetConfig flobi = new TargetConfig();
		flobi.setDescription(AMBIENT_LIGHT, "Hallway_0").
				setDescription(GAZE, "120,120").
				setDescription(GESTURE, "120,120").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "120,120");
		
		TargetConfig meka = new TargetConfig();
		meka.setDescription(AMBIENT_LIGHT, "Hallway_0").
				setDescription(GAZE, "120,120").
				setDescription(GESTURE, "120,120").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "120,120");
		
		TargetConfig zen = new TargetConfig();
		zen.setDescription(AMBIENT_LIGHT, "Slamp2").
				setDescription(GAZE, "120,120").
				setDescription(GESTURE, "120,120").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "120,120");
		
		TargetConfig cupboard1 = new TargetConfig();
		cupboard1.setDescription(AMBIENT_LIGHT, "503").
				setDescription(GAZE, "120,120").
				setDescription(GESTURE, "120,120").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "120,120");
		
		TargetConfig cupboard2 = new TargetConfig();
		cupboard2.setDescription(AMBIENT_LIGHT, "505").
				setDescription(GAZE, "120,120").
				setDescription(GESTURE, "120,120").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "120,120");
		
		TargetConfig drawer1 = new TargetConfig();
		drawer1.setDescription(AMBIENT_LIGHT, "502").
				setDescription(GAZE, "120,120").
				setDescription(GESTURE, "120,120").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "120,120");
		
		TargetConfig drawer2 = new TargetConfig();
		drawer2.setDescription(AMBIENT_LIGHT, "507").
				setDescription(GAZE, "120,120").
				setDescription(GESTURE, "120,120").
				setDescription(SOUND, "Waikiki.ogg").
				setDescription(SPOT_LIGHT, "120,120");
		
		TargetMap.register(Target.TV, tv);
		TargetMap.register(Target.Flobi, flobi);
		TargetMap.register(Target.Meka, meka);
		TargetMap.register(Target.Zen, zen);
		TargetMap.register(Target.cupboard1, cupboard1);
		TargetMap.register(Target.cupboard2, cupboard2);
		TargetMap.register(Target.drawer1, drawer1);
		TargetMap.register(Target.drawer2, drawer2);
	}
	
}
