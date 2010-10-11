/**
 * 
 */
package com.sipcm.startup;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sipcm.sip.SipCenter;

/**
 * @author wgao
 * 
 */
public class Bootstrap {
	private ConfigurableApplicationContext ctx;

	private SipCenter sipCenter;

	/**
	 * The API that will be called by jsvc to initial Bootstrap
	 * 
	 * @param arguments
	 * @throws Exception
	 */
	public void init(String[] arguments) throws Exception {
		ctx = new ClassPathXmlApplicationContext("classpath:/appContext*.xml");
		sipCenter = (SipCenter)ctx.getBean("sipCenter");
	}

	public void init(String[] arguments, boolean flag) throws Exception {
		init(arguments);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				sipCenter.stop();
				ctx.close();
			}
		});
	}

	public void start() throws Exception {
		sipCenter.start();
	}

	public void stop() throws Exception {
		sipCenter.stop();
	}

	public void destroy() throws Exception {
		ctx.close();
	}

	/**
	 * Test only
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Bootstrap main = new Bootstrap();
		try {
			main.init(args, true);
			main.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
