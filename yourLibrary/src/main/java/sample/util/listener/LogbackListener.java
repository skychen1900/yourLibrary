package sample.util.listener;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class LogbackListener implements ServletContextListener {
	public static final String CONFIG_LOCATION = "logbackLocation";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		String location = servletContext.getInitParameter(CONFIG_LOCATION);
		servletContext.log("Loading[" + location + "]");
		URL url = getXmlResource(servletContext, location);
		if (url != null) {
			try {
				configureLogback(loggerContext, url);
				servletContext.log("loading[" + location + "] successful");
			} catch (JoranException e) {
				servletContext.log("Could not load[" + location + "]");
			}
		} else {
			servletContext.log("Could not find [" + location + "]");
		}
	}

	public URL getXmlResource(ServletContext servletContext, String location) {
		URL resource = LogbackListener.class.getResource(location);
		if (resource != null) {
			return resource;
		} else {
			return null;
		}
	}

	private void configureLogback(LoggerContext context, URL configUrl) throws JoranException {
		//implementation ommited
		context.reset();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		configurator.doConfigure(configUrl);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
	}
}