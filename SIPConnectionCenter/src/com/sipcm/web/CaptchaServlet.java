/**
 * 
 */
package com.sipcm.web;

import static nl.captcha.Captcha.NAME;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;

/**
 * @author wgao
 * 
 */
public class CaptchaServlet extends HttpServlet {
	private static final long serialVersionUID = 6994633296192052929L;

	private static final String PARAM_HEIGHT = "height";
	private static final String PARAM_WIDTH = "width";

	protected int _width = 200;
	protected int _height = 50;

	@Override
	public void init() throws ServletException {
		if (getInitParameter(PARAM_HEIGHT) != null) {
			_height = Integer.valueOf(getInitParameter(PARAM_HEIGHT));
		}

		if (getInitParameter(PARAM_WIDTH) != null) {
			_width = Integer.valueOf(getInitParameter(PARAM_WIDTH));
		}
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Captcha captcha = new Captcha.Builder(_width, _height).addText()
				.addBackground(new GradiatedBackgroundProducer()).gimp()
				.addNoise().addNoise().addBorder().build();

		CaptchaServletUtil.writeImage(resp, captcha.getImage());
		req.getSession().setAttribute(NAME, captcha);
	}
}
