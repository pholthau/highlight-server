/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.highlight.action;

import de.citec.csra.highlight.com.Finalizeable;
import de.citec.csra.highlight.com.Preparable;
import de.citec.csra.highlight.com.RemoteMap;
import de.citec.csra.highlight.com.RemoteServerConfig;
import de.citec.csra.highlight.com.Resetable;
import de.citec.csra.highlight.tgt.Target;
import de.citec.csra.highlight.tgt.TargetMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.patterns.RemoteServer;
import rst.hri.HighlightTargetType.HighlightTarget.Modality;

/**
 *
 * @author Patrick Holthaus
 * (<a href=mailto:patrick.holthaus@uni-bielefeld.de>patrick.holthaus@uni-bielefeld.de</a>)
 */
public class RemoteServerAction<T, R> implements Callable<R> {

	private final static Logger log = Logger.getLogger(RemoteServerAction.class.getName());
	private final String target;
	private final RemoteServerConfig<T> remoteConf;
	private final long duration;

	public RemoteServerAction(Target tgt, Modality m, long duration) {
		target = TargetMap.get(tgt).getDescription(m);
		remoteConf = (RemoteServerConfig<T>) RemoteMap.get(m);
		this.duration = duration;
	}

	@Override
	public R call() throws Exception {

		RemoteServer r = remoteConf.getRemote();
		String method = remoteConf.getExecuteMethod();

		if (remoteConf instanceof Preparable) {
			Preparable<String, ?> bc = (Preparable<String, ?>) remoteConf;
			String pMeth = bc.getPrepareInterface();
			Object pArg = bc.getPrepareArgument();
			log.log(Level.INFO, "Calling remote server ''{0}'' at method ''{1}'' with argument ''{2}'' for preparation.", new Object[]{r.getScope(), pMeth, pArg != null ? pArg.toString().replaceAll("\n", " ") : pArg});
			r.call(pMeth, pArg);
		}

		Object arg = target;
		if (remoteConf.getParser() != null) {
			arg = remoteConf.getParser().getValue(target);
		}
		log.log(Level.INFO, "Calling remote server ''{0}'' at method ''{1}'' with argument ''{2}'' as a target.", new Object[]{r.getScope(), method, arg.toString().replaceAll("\n", " ")});
		R ret = r.call(method, arg);

		log.log(Level.INFO, "Sleeping {0}ms.", duration);
		Thread.sleep(duration);

		if (remoteConf instanceof Resetable) {
			Resetable<String, ?> rc = (Resetable<String, ?>) remoteConf;
			String rMeth = rc.getResetInterface();
			Object rArg = rc.getResetArgument();
			if (rMeth == null) {
				rMeth = method;
			}
			log.log(Level.INFO, "Calling remote server ''{0}'' at method ''{1}'' with argument ''{2}'' for reset.", new Object[]{r.getScope(), rMeth, rArg != null ? rArg.toString().replaceAll("\n", " ") : rArg});
			r.call(rMeth, rArg);
		}
		
		if (remoteConf instanceof Finalizeable) {
			Finalizeable<String, ?> rc = (Finalizeable<String, ?>) remoteConf;
			String fMeth = rc.getFinalizeInterface();
			Object fArg = rc.getFinalizeArgument();
			if (fMeth == null) {
				fMeth = method;
			}
			log.log(Level.INFO, "Calling remote server ''{0}'' at method ''{1}'' with argument ''{2}'' for finalization.", new Object[]{r.getScope(), fMeth, fArg != null ? fArg.toString().replaceAll("\n", " ") : fArg});
			r.call(fMeth, fArg);
		}
		
		return ret;

	}

}
