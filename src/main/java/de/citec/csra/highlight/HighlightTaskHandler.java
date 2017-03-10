/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight;

import de.citec.csra.highlight.cfg.TargetObject;
import de.citec.csra.allocation.cli.ExecutableResource;
import de.citec.csra.highlight.cfg.Defaults;
import de.citec.csra.rst.parse.EnumParser;
import de.citec.csra.rst.parse.HighlightTargetParser;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rst.hri.HighlightTargetType.HighlightTarget;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;
import de.citec.csra.highlight.cfg.Highlightable;
import de.citec.csra.task.srv.AbstractTaskHandler;
import de.citec.csra.task.srv.ExecutableResourceTask;
import rsb.RSBException;
import de.citec.csra.task.srv.LocalTask;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author pholthau
 */
public class HighlightTaskHandler extends AbstractTaskHandler {

	private final static Logger LOG = Logger.getLogger(HighlightTaskHandler.class.getName());

	@Override
	public LocalTask newLocalTask(Object description) throws IllegalArgumentException {
		try {
			HighlightTarget hlt;
			if (description instanceof HighlightTarget) {
				hlt = (HighlightTarget) description;
			} else if (description instanceof String) {
				hlt = new HighlightTargetParser().getValue((String) description);
			} else {
				throw new IllegalArgumentException("unreadable description: " + description);
			}
			Set<ExecutableResource> execs = getActions(hlt);
			return new ExecutableResourceTask(execs, true);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private Set<ExecutableResource> getActions(HighlightTarget hlt) {
		Set<ExecutableResource> acts = new HashSet<>();
		TargetObject tgt = new EnumParser<>(TargetObject.class).getValue(hlt.getTargetId().toUpperCase());
		List<Modality> modalities = hlt.getModalityList();
		modalities.forEach((m) -> {
			Highlightable cfg = Defaults.get(tgt, m);
			try {
				acts.add(new HighlightExecutable(cfg, hlt.getDuration().getTime()));
			} catch (RSBException ex) {
				Logger.getLogger(HighlightTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		return acts;
	}
}
