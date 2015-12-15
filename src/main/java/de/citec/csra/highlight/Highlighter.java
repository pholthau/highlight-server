/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight;

import de.citec.csra.util.UnitParser;
import de.citec.csra.highlight.action.GazeAction;
import de.citec.csra.highlight.action.GestureAction;
import de.citec.csra.highlight.action.LightAction;
import de.citec.csra.highlight.action.SoundAction;
import de.citec.csra.highlight.action.SpotAction;
import de.citec.csra.task.srv.TaskHandler;
import de.citec.csra.util.EnumParser;
import de.citec.csra.util.ScopeParser;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.InitializeException;
import rst.communicationpatterns.TaskStateType.TaskState.State;
import static rst.communicationpatterns.TaskStateType.TaskState.State.ACCEPTED;
import static rst.communicationpatterns.TaskStateType.TaskState.State.COMPLETED;
import static rst.communicationpatterns.TaskStateType.TaskState.State.FAILED;
import static rst.communicationpatterns.TaskStateType.TaskState.State.REJECTED;
import rst.hri.HighlightTargetType.HighlightTarget;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class Highlighter extends TaskHandler<HighlightTarget> {

	private final static Logger LOG = Logger.getLogger(Highlighter.class.getName());
	

	ExecutorService pool = Executors.newFixedThreadPool(10);
	EnumParser<Target> ep = new EnumParser<>(Target.class);
	ScopeParser sp = new ScopeParser();
	UnitParser up = new UnitParser();
	private Set<Callable<State>> actions;
	private final long init = 2000;
	private final long wait = 10000;
	private long duration;

	public Highlighter(String scope) throws InitializeException {
		super(scope, HighlightTarget.class);
	}

	@Override
	public State initializeTask(HighlightTarget payload) {

		Target tgt = ep.getValue(payload.getTargetId());
		this.duration = payload.getDuration().getTime();
		this.actions = getActions(tgt, payload.getModalityList());
		if (this.actions.isEmpty()) {
			return REJECTED;
		} else {
			return ACCEPTED;
		}
	}

	@Override
	public State handleTask(HighlightTarget payload) {
		try {
			Set<Future<State>> futures = new HashSet<>();
			boolean success = true;
			System.out.println(this.actions);
			for (Callable<State> act : this.actions) {
				futures.add(pool.submit(act));
			}
			for (Future<State> f : futures) {
				State s = f.get(init + duration + wait, TimeUnit.MILLISECONDS);
				if(!f.isDone() || s == null){
					success = false;
				}
			}
			if (success) {
				return COMPLETED;
			} else {
				return FAILED;
			}
		} catch (InterruptedException ex) {
			LOG.log(Level.SEVERE, "Action execution failed", ex);
		} catch (ExecutionException | TimeoutException ex) {
			LOG.log(Level.SEVERE, "Action execution failed", ex);
		}
		return FAILED;
	}

	private Set<Callable<State>> getActions(Target tgt, List<Modality> modalities) {
		Set<Callable<State>> acts = new HashSet<>();
		for (Modality m : modalities) {
			try {
				switch (m) {
					case GAZE:
						acts.add(new GazeAction(tgt, duration, init, wait));
						break;
					case GESTURE:
						acts.add(new GestureAction(tgt, duration, init, wait));
						break;
					case LIGHT:
						acts.add(new LightAction(tgt, duration));
						break;
					case SOUND:
						acts.add(new SoundAction(tgt, duration, init, wait));
						break;
					case SPOT:
						acts.add(new SpotAction(tgt, duration, init, wait));
				}
			} catch (InitializeException ex) {
				LOG.log(Level.WARNING, "Could not initialize action '" + m + "', skipping", ex);
			}
		}
		return acts;
	}
}
