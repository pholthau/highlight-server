/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.com;

import java.util.EnumMap;
import java.util.Map;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class RemoteMap {

	private static final Map<Modality, RemoteConfig> configs = new EnumMap<>(Modality.class);

	public static void register(Modality m, RemoteConfig server) {
		configs.put(m, server);
	}

	public static RemoteConfig get(Modality m) {
		return configs.get(m);
	}

}
