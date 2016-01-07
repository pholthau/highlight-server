/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.highlight.Target;
import java.util.concurrent.Callable;
import rsb.Factory;
import rsb.InitializeException;
import rsb.patterns.RemoteServer;
import rst.communicationpatterns.TaskStateType.TaskState.State;
import static rst.communicationpatterns.TaskStateType.TaskState.State.COMPLETED;
import static rst.communicationpatterns.TaskStateType.TaskState.State.FAILED;
import rst.spatial.PanTiltAngleType.PanTiltAngle;
import rst.spatial.PanTiltAngleType.PanTiltAngle.Builder;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class SpotAction implements Callable<State> {

	final RemoteServer srv;
	final PanTiltAngle arg;
	final PanTiltAngle zero = PanTiltAngle.getDefaultInstance();
	final long duration;

	public SpotAction(Target tgt, long duration, long init, long wait) throws InitializeException {
		this.srv = Factory.getInstance().createRemoteServer("/home/living/movinghead/");
		Builder b = PanTiltAngle.newBuilder();

		switch (tgt) {
			case Flobi:
				b.setPan(120);
				b.setTilt(120);
				break;
			case TV:
				b.setPan(120);
				b.setTilt(120);
				break;
			case Meka:
				b.setPan(120);
				b.setTilt(120);
				break;
			case Zen:
				b.setPan(120);
				b.setTilt(120);
				break;
		}

		this.arg = b.build();
		this.duration = duration;
	}

	@Override
	public State call() throws Exception {
		try{
			this.srv.call("setPanTilt", this.arg);
			Thread.sleep(duration);
			this.srv.call("setPanTilt", this.zero);
		} catch(Exception ex){
			return FAILED;
		}
		return COMPLETED;
	}

}
