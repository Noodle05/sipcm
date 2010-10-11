/**
 * 
 */
package com.sipcm;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sipcm.sip.SipCenter;

/**
 * @author wgao
 * 
 */
public class Main {

	public void start() {
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:/appContext*.xml");
		final SipCenter center = (SipCenter) ctx.getBean("sipCenter");
		System.out.println("Hello World!");
		ctx.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.start();
	}
}
