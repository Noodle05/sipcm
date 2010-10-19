/**
 * 
 */
package com.sipcm.process;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class for single thread processor. It will make sure there has
 * only one thread been used to process request. In order to use it, user should
 * extends it and implement internalRun. Typical usage should be:
 * <p>
 * <code>
 * public class RequestProcessor extends SingleThreadProcessor<Integer> {
 * 	 private BlockingQueue<Request> requests;
 * 
 *   protected Integer internalRun() {
 *     Request request;
 *     Integer total = 0;
 *     while ((request = requests.poll()) != null) {
 *       try {
 *         ... process request here.
 *       } finally {
 *         total ++;
 *       }
 *     }
 *     return total;
 *   }
 *   
 *   public void addRequest(Request request) {
 *     requests.offer(request;
 *     start();
 *   }
 * }
 * </code>
 * </p>
 * 
 * @author Jack
 * 
 */
public abstract class SingleThreadProcessor<V> implements Callable<V> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private AtomicReference<ProcessState> state;

	@Resource(name = "global.threadPool")
	private ExecutorService threadPool;

	private Future<V> future;

	@PostConstruct
	public void init() {
		state = new AtomicReference<ProcessState>(ProcessState.STOPPED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public V call() throws Exception {
		try {
			return internalRun();
		} finally {
			state.set(ProcessState.STOPPED);
		}
	}

	/**
	 * Internal function for thread to run. Implementations need to implement
	 * this function.
	 */
	protected abstract V internalRun();

	/**
	 * Start processor to process data. If it's running, it will not start
	 * another thread.
	 */
	public void start() {
		if (state.compareAndSet(ProcessState.STOPPED, ProcessState.RUNNING)) {
			future = threadPool.submit(this);
		}
	}

	/**
	 * Get process result. This going to block current thread till process done.
	 * 
	 * @return
	 * @throws Exception
	 */
	public V getLastResult() throws Exception {
		if (future != null) {
			return future.get();
		}
		throw new ProcessNotStartException();
	}

	/**
	 * Check if current processor is running.
	 * 
	 * @return <tt>true</tt> if the processor is running, <tt>false</tt>
	 *         otherwise.
	 */
	public boolean isProcessing() {
		return ProcessState.RUNNING.equals(state.get());
	}

	/**
	 * Check if current processor is stopped.
	 * 
	 * @return <tt>true</tt> if the processor is not running, <tt>false</tt>
	 *         otherwise.
	 */
	public boolean isStopped() {
		return ProcessState.STOPPED.equals(state.get());
	}

	/**
	 * @return the state
	 */
	public ProcessState getState() {
		return state.get();
	}
}
