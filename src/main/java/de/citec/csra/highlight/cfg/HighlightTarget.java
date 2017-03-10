/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.cfg;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.citec.csra.highlight.com.RemoteConnection;

/**
 *
 * @author pholthau
 */
public class HighlightTarget implements Highlightable, Configurable {

	private final static Logger LOG = Logger.getLogger(HighlightTarget.class.getName());

	private RemoteConnection exec;
	private Object execArg;

	private RemoteConnection prepare;
	private Object prepareArg;
	private long prepareDelay;

	private RemoteConnection reset;
	private Object resetArg;
	private long resetDelay;

	private RemoteConnection shutdown;
	private Object shutdownArg;
	private long shutdownDelay;

	@Override
	public <T> HighlightTarget setExecution(RemoteConnection<T> ri, T argument) {
		this.exec = ri;
		this.execArg = argument;
		return this;
	}
	
	@Override
	public <P> HighlightTarget setPrepare(RemoteConnection<P> ri, P argument, long delay) {
		this.prepare = ri;
		this.prepareArg = argument;
		this.prepareDelay = delay;
		return this;
	}

	@Override
	public <R> HighlightTarget setReset(RemoteConnection<R> ri, R argument, long delay) {
		this.reset = ri;
		this.resetArg = argument;
		this.resetDelay = delay;
		return this;
	}

	@Override
	public <S> HighlightTarget setShutdown(RemoteConnection<S> ri, S argument, long delay) {
		this.shutdown = ri;
		this.shutdownArg = argument;
		this.shutdownDelay = delay;
		return this;
	}

	@Override
	public void highlight(long duration) throws Exception {
		prepare();
		execute(duration - (this.prepareDelay + this.resetDelay + this.shutdownDelay));
		reset();
		shutdown();
	}

	private void prepare() throws Exception {
		if (this.prepare != null) {
			LOG.log(Level.INFO, "Sending to ''{0}'' with argument ''{1}'' for preparation.", new Object[]{prepare.getAddress(), prepareArg != null ? prepareArg.toString().replaceAll("\n", " ") : prepareArg});
			this.prepare.send(this.prepareArg);
			Thread.sleep(this.prepareDelay);
		}
	}

	private void execute(long duration) throws Exception {
		if (this.exec != null) {
			LOG.log(Level.INFO, "Sending to ''{0}'' with argument ''{1}'' as a target.", new Object[]{exec.getAddress(), execArg != null ? execArg.toString().replaceAll("\n", " ") : execArg});
			this.exec.send(this.execArg);
			Thread.sleep(duration);
		}
	}

	private void reset() throws Exception {
		if (this.reset != null) {
			LOG.log(Level.INFO, "Sending to ''{0}'' with argument ''{1}'' for reset.", new Object[]{reset.getAddress(), resetArg != null ? resetArg.toString().replaceAll("\n", " ") : resetArg});
			this.reset.send(this.resetArg);
			Thread.sleep(this.resetDelay);
		}
	}

	private void shutdown() throws Exception {
		if (this.shutdown != null) {
			LOG.log(Level.INFO, "Sending to ''{0}'' with argument ''{1}'' for shutdown.", new Object[]{shutdown.getAddress(), shutdownArg != null ? shutdownArg.toString().replaceAll("\n", " ") : shutdownArg});
			this.shutdown.send(this.shutdownArg);
			Thread.sleep(this.shutdownDelay);
		}
	}

	@Override
	public Set<String> getInterfaces() {
		Set<String> ifs = new HashSet<>();
		if (this.exec != null) {
			ifs.add(this.exec.getAddress());
		}
		if (this.prepare != null) {
			ifs.add(this.prepare.getAddress());
		}
		if (this.reset != null) {
			ifs.add(this.reset.getAddress());
		}
		if (this.shutdown != null) {
			ifs.add(this.shutdown.getAddress());
		}
		return ifs;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + (this.exec != null ? this.exec.getAddress() : "null") + "]";
	}
}
