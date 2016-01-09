/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight;

import de.citec.csra.highlight.action.LightAction;
import de.citec.csra.highlight.action.RemoteServerAction;
import de.citec.csra.highlight.com.DefaultRemotes;
import de.citec.csra.highlight.tgt.DefaultTargets;
import de.citec.csra.highlight.tgt.Target;
import de.citec.csra.highlight.tgt.TargetMap;
import de.citec.csra.task.srv.TaskHandler;
import de.citec.csra.util.EnumParser;
import de.citec.csra.util.ScopeParser;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.InitializeException;
import rsb.RSBException;
import rst.communicationpatterns.TaskStateType.TaskState.State;
import static rst.communicationpatterns.TaskStateType.TaskState.State.ACCEPTED;
import static rst.communicationpatterns.TaskStateType.TaskState.State.REJECTED;
import rst.hri.HighlightTargetType.HighlightTarget;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.GAZE;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class Highlighter extends TaskHandler<HighlightTarget, Boolean> {

	private final static Logger LOG = Logger.getLogger(Highlighter.class.getName());

	ExecutorService pool = Executors.newFixedThreadPool(10);
	EnumParser<Target> ep = new EnumParser<>(Target.class);
	ScopeParser sp = new ScopeParser();
	private Set<Callable<?>> actions;
	private final long init = 2000;
	private final long wait = 10000;
	private long duration;

	public Highlighter(String scope) throws InitializeException, RSBException {
		super(scope, HighlightTarget.class, Boolean.class);
		DefaultTargets.load();
		DefaultRemotes.load();
	}

	@Override
	public State initializeTask(HighlightTarget payload) {

		Target tgt = ep.getValue(payload.getTargetId());
		this.duration = payload.getDuration().getTime() / 1000;
		this.actions = getActions(tgt, payload.getModalityList());
		if (this.actions.isEmpty()) {
			LOG.log(Level.WARNING, "No action found for target ''{0}'' and modalities ''{1}'', rejecting.", new Object[]{tgt.name(), payload.getModalityList()});
			return REJECTED;
		} else {
			return ACCEPTED;
		}
	}

	@Override
	public Boolean handleTask(HighlightTarget payload) throws Exception {
		Set<Future<?>> futures = new HashSet<>();
		boolean success = true;
		for (Callable<?> act : this.actions) {
			LOG.log(Level.INFO, "Queuing action ''{0}''.", act.getClass().getSimpleName());
			futures.add(pool.submit(act));
		}
		for (Future<?> f : futures) {
				Object s = f.get(init + duration + wait, TimeUnit.MILLISECONDS);
				if (!f.isDone()) {
					success = false;
				}
				LOG.log(Level.INFO, "Action finished with return value ''{0}'' ({1}).", new Object[]{s, success});
		}
		return success;
	}

	private Set<Callable<?>> getActions(Target tgt, List<Modality> modalities) {
		Set<Callable<?>> acts = new HashSet<>();
		for (Modality m : modalities) {
			try {
				String cfg = TargetMap.get(tgt, m);
				if (cfg != null) {
					switch (m) {
						case GAZE:
						case GESTURE:
						case SPOT_LIGHT:
						case SOUND:
							acts.add(new RemoteServerAction(tgt, m, duration));
							break;
						case AMBIENT_LIGHT:
							acts.add(new LightAction(cfg, duration));
					}
				}
			} catch (Exception ex) {
				LOG.log(Level.WARNING, "Could not initialize action '" + m + "', skipping", ex);
			}
		}
		return acts;
	}
}
