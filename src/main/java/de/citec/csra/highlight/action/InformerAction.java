/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.highlight.com.Finalizeable;
import de.citec.csra.highlight.com.InformerConfig;
import de.citec.csra.highlight.com.Preparable;
import de.citec.csra.highlight.com.RemoteMap;
import de.citec.csra.highlight.com.Resetable;
import de.citec.csra.highlight.tgt.Target;
import de.citec.csra.highlight.tgt.TargetMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.Informer;
import rsb.Listener;
import rsb.util.QueueAdapter;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class InformerAction<T, R> implements Callable<R> {

	private final static Logger log = Logger.getLogger(InformerAction.class.getName());
	private final String target;
	private final InformerConfig<T> remoteConf;
	private final long duration;

	public InformerAction(Target tgt, Modality m, long duration) {
		target = TargetMap.get(tgt).getDescription(m);
		remoteConf = (InformerConfig<T>) RemoteMap.get(m);
		this.duration = duration;
	}

	@Override
	public R call() throws Exception {

		Informer i = remoteConf.getInformer();
		Listener l = remoteConf.getListener();

		if (remoteConf instanceof Preparable) {
			Preparable<Informer, ?> bc = (Preparable<Informer, ?>) remoteConf;
			Informer pInf = bc.getPrepareInterface();
			
			Object pArg = bc.getPrepareArgument();
			log.log(Level.INFO, "Sending to ''{0}'' with argument ''{1}'' for preparation.", new Object[]{pInf.getScope(), pArg != null ? pArg.toString().replaceAll("\n", " ") : pArg});
			pInf.send(pArg);
			Thread.sleep(100);
		}
		
		QueueAdapter<R> q = new QueueAdapter();
		l.addHandler(q, true);

		Object arg = target;
		if (remoteConf.getParser() != null) {
			arg = remoteConf.getParser().getValue(target);
		}

		log.log(Level.INFO, "Sending to ''{0}'' with argument ''{1}'' as a target.", new Object[]{i.getScope(), arg.toString().replaceAll("\n", " ")});
		q.getQueue().clear();
		i.send(arg);

		log.log(Level.INFO, "Sleeping {0}ms.", duration);
		Thread.sleep(duration);
		R ret = q.getQueue().poll();
				
		if (remoteConf instanceof Resetable) {
			Resetable<Informer, ?> rc = (Resetable<Informer, ?>) remoteConf;
			Object rArg = rc.getResetArgument();
			Informer rInf = rc.getResetInterface();
			log.log(Level.INFO, "Sending to ''{0}'' with argument ''{1}'' for reset.", new Object[]{rInf.getScope(), rArg != null ? rArg.toString().replaceAll("\n", " ") : rArg});
			rInf.send(rArg);
		}
		
		if (remoteConf instanceof Finalizeable) {
			Thread.sleep(100);
			Finalizeable<Informer, ?> rc = (Finalizeable<Informer, ?>) remoteConf;
			Object fArg = rc.getFinalizeArgument();
			Informer fInf = rc.getFinalizeInterface();
			log.log(Level.INFO, "Sending to ''{0}'' with argument ''{1}'' for finalization.", new Object[]{fInf.getScope(), fArg != null ? fArg.toString().replaceAll("\n", " ") : fArg});
			fInf.send(fArg);
		}
		
		return ret;
	}

}
