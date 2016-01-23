/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight;

import de.citec.csra.highlight.action.InformerAction;
import de.citec.csra.highlight.action.LightAction;
import de.citec.csra.highlight.action.RemoteServerAction;
import de.citec.csra.highlight.com.DefaultRemotes;
import de.citec.csra.highlight.tgt.DefaultTargets;
import de.citec.csra.highlight.tgt.Target;
import de.citec.csra.highlight.tgt.TargetMap;
import de.citec.csra.task.srv.AsyncTaskHandler;
import de.citec.csra.util.EnumParser;
import de.citec.csra.util.HighlightTargetParser;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.InitializeException;
import rsb.RSBException;
import rst.communicationpatterns.TaskStateType.TaskState;
import rst.communicationpatterns.TaskStateType.TaskState.State;
import static rst.communicationpatterns.TaskStateType.TaskState.State.ACCEPTED;
import static rst.communicationpatterns.TaskStateType.TaskState.State.COMPLETED;
import static rst.communicationpatterns.TaskStateType.TaskState.State.FAILED;
import static rst.communicationpatterns.TaskStateType.TaskState.State.REJECTED;
import rst.hri.HighlightTargetType.HighlightTarget;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;
import static rst.hri.HighlightTargetType.HighlightTarget.Modality.GAZE;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class AsyncHighlighter extends AsyncTaskHandler<HighlightTarget, Boolean> {

	private final static Logger LOG = Logger.getLogger(AsyncHighlighter.class.getName());

//	ExecutorService actionPool = Executors.newFixedThreadPool(10);
	ExecutorService taskPool = Executors.newFixedThreadPool(10);
	private final EnumParser<Target> enums = new EnumParser<>(Target.class);
	private Map<Modality, Callable<?>> incoming;
	private final Map<Modality, ExecutorService> running = new EnumMap<>(Modality.class);
	private long duration;
	private TaskState task;
	private final long INTERRUPT_WAIT = 2000;
	private final long RESPONSE_WAIT = 2000;

	public AsyncHighlighter(String scope) throws InitializeException, RSBException {
		super(scope, HighlightTarget.class, Boolean.class, new HighlightTargetParser());
		DefaultTargets.load();
		DefaultRemotes.load();
	}

	@Override
	public synchronized State initializeTask(TaskState task, HighlightTarget payload) {
		try {
			Target tgt = this.enums.getValue(payload.getTargetId().toUpperCase());
			this.duration = payload.getDuration().getTime() / 1000;
			this.incoming = getActions(tgt, payload.getModalityList());
			this.task = task;

			if (this.incoming.isEmpty()) {
				LOG.log(Level.WARNING, "No action found for target ''{0}'' and modalities ''{1}'', rejecting.", new Object[]{payload.getTargetId(), payload.getModalityList()});
				return REJECTED;
			}
		} catch (IllegalArgumentException ex) {
			LOG.log(Level.WARNING, "Invalid target ''{0}'', rejecting. Available targets: {1}", new Object[]{payload.getTargetId(), Arrays.toString(Target.values())});
			return REJECTED;
		}
		LOG.log(Level.INFO, "Action found for target ''{0}'' and modalities ''{1}'', accepting.", new Object[]{payload.getTargetId(), payload.getModalityList()});
		return ACCEPTED;
	}

	private class TaskHandler implements Callable<Void> {

		private final TaskState task;
		private final Map<Modality, Future<?>> futures;
		private final long timeout;

		public TaskHandler(TaskState task, Map<Modality, Future<?>> futures, long timeout) {
			this.task = task;
			this.futures = futures;
			this.timeout = timeout;
		}

		@Override
		public Void call() throws InterruptedException {
			boolean successAll = true;
			long remaining = RESPONSE_WAIT + timeout + INTERRUPT_WAIT;
			for (Map.Entry<Modality, Future<?>> entry : futures.entrySet()) {
				Object s = null;
				boolean success = true;
				Future<?> f = entry.getValue();
				Modality m = entry.getKey();
				try {
					if (remaining < 0) {
						remaining = 1;
					}
					long now = System.currentTimeMillis();
					s = f.get(remaining, TimeUnit.MILLISECONDS);
					remaining -= System.currentTimeMillis() - now;
				} catch (CancellationException ex) {
					LOG.log(Level.WARNING, "Cancelled ''{0}''.", m.name());
					success = false;
				} catch (TimeoutException ex) {
					LOG.log(Level.WARNING, "Timeout at ''{0}''.", m.name());
					success = false;
				} catch (ExecutionException ex) {
					LOG.log(Level.WARNING, "Execution failed at ''{0}''.", m.name());
					success = false;
				}
				
				success &= f.isDone();
				success &= !f.isCancelled();
				
				if (success) {
					AsyncHighlighter.this.running.remove(entry.getKey());
				}
				LOG.log(Level.INFO, "Action ''{0}'' finished with return value ''{1}'' ({2}).", new Object[]{m.name(), s, success});
				successAll &= success;
			}

			if (task != null) {
				AsyncHighlighter.super.updateTask(task, successAll ? COMPLETED : FAILED, successAll);
			} else {
				AsyncHighlighter.super.respond(successAll);
			}
			if (successAll) {

			}
			return null;
		}
	}

	@Override
	public void executeTask() throws Exception {
		LOG.log(Level.FINE, "Currently running ''{0}''", this.running.keySet());
		for (Modality m : this.incoming.keySet()) {
			if (this.running.containsKey(m)) {
				LOG.log(Level.INFO, "Cancelling current action for modality ''{0}''.", m);
				ExecutorService e = this.running.get(m);
				try {
					e.shutdownNow();
					e.awaitTermination(INTERRUPT_WAIT, TimeUnit.MILLISECONDS);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				this.running.remove(m);
			}

		}

		Map<Modality, Future<?>> futures = new EnumMap<>(Modality.class);
		for (Map.Entry<Modality, Callable<?>> act : this.incoming.entrySet()) {
			LOG.log(Level.INFO, "Queuing action {0} -> ''{1}''", new Object[]{act.getKey(), act.getValue().getClass().getSimpleName()});
			if (!this.running.containsKey(act.getKey())) {
				this.running.put(act.getKey(), Executors.newSingleThreadExecutor());
			}
			ExecutorService s = this.running.get(act.getKey());
			Future<?> f = s.submit(act.getValue());
			futures.put(act.getKey(), f);
			this.running.put(act.getKey(), s);
		}
		this.taskPool.submit(new TaskHandler(task, futures, duration));
	}

	private Map<Modality, Callable<?>> getActions(Target tgt, List<Modality> modalities) {
		Map<Modality, Callable<?>> acts = new ConcurrentHashMap<>();
		for (Modality m : modalities) {
			try {
				String cfg = TargetMap.get(tgt, m);
				if (cfg != null) {
					switch (m) {
						case GAZE:
						case GESTURE:
						case SOUND:
							acts.put(m, new InformerAction(tgt, m, duration));
							break;
						case SPOT_LIGHT:
							acts.put(m, new RemoteServerAction(tgt, m, duration));
							break;
						case AMBIENT_LIGHT:
							acts.put(m, new LightAction(cfg, duration, INTERRUPT_WAIT));
					}
				}
			} catch (Exception ex) {
				LOG.log(Level.WARNING, "Could not initialize action '" + m + "', skipping", ex);
			}
		}
		return acts;
	}

}
