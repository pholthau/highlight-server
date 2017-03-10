/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight;

import de.citec.csra.allocation.cli.ExecutableResource;
import static de.citec.csra.allocation.cli.ExecutableResource.Completion.RETAIN;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import static rst.communicationpatterns.ResourceAllocationType.ResourceAllocation.Initiator.SYSTEM;
import static rst.communicationpatterns.ResourceAllocationType.ResourceAllocation.Policy.MAXIMUM;
import static rst.communicationpatterns.ResourceAllocationType.ResourceAllocation.Priority.NORMAL;
import de.citec.csra.highlight.cfg.Highlightable;
import rsb.RSBException;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class HighlightExecutable extends ExecutableResource {

	private final static long OVERHEAD = 100;
	private final static Logger LOG = Logger.getLogger(HighlightExecutable.class.getName());
	private final Highlightable cfg;

	public HighlightExecutable(Highlightable cfg, long duration) throws RSBException {
		super("exec[" + cfg.toString() + "]",
				MAXIMUM,
				NORMAL,
				SYSTEM,
				0,
				duration + OVERHEAD,
				RETAIN,
				cfg.getInterfaces().toArray(new String[cfg.getInterfaces().size()]));
		this.cfg = cfg;
	}

	@Override
	public Object execute() throws InterruptedException, ExecutionException {
		try {
			this.cfg.highlight(getRemote().getRemainingTime() - OVERHEAD);
			return null;
		} catch (Exception ex) {
			throw new ExecutionException(ex);
		}
	}

	@Override
	public String toString() {
		return "exec[" + cfg.toString() + "]";
	}
	
}
