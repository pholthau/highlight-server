/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.tgt;

import java.util.EnumMap;
import java.util.Map;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class TargetMap {

	private static final Map<Target, TargetConfig> configs = new EnumMap<>(Target.class);

	public static void register(Target tgt, TargetConfig conf) {
		configs.put(tgt, conf);
	}

	public static void add(Target tgt, Modality m, String desc) {
		if (configs.get(tgt) == null) {
			configs.put(tgt, new TargetConfig());
		}
		configs.get(tgt).setDescription(m, desc);
	}

	public static TargetConfig get(Target tgt) {
		return configs.get(tgt);
	}

	public static String get(Target tgt, Modality m) {
		if (configs.get(tgt) == null) {
			return null;
		} else {
			return configs.get(tgt).getDescription(m);
		}
	}
}
