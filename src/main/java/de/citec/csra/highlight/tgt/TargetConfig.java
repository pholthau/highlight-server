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
public class TargetConfig {

	private final Map<Modality, String> descriptions = new EnumMap<>(Modality.class);

	public TargetConfig() {
//		this.tgt = tgt;
	}

	public TargetConfig setDescription(Modality m, String desc) {
		this.descriptions.put(m, desc);
		return this;
	}

//	public Target getName() {
//		return this.tgt;
//	}

	public String getDescription(Modality m) {
		return this.descriptions.get(m);
	}
}
