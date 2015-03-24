package de.nierbeck.javaland.config;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(enabled=true, immediate=true)
public class ConfigureCredit {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureCredit.class);

	private ConfigurationAdmin configAdmin;

	@Activate
	protected void start() {
		
		
		try {
			Configuration configuration = configAdmin
					.createFactoryConfiguration("de.nierbeck.javaland.calculator");

			Dictionary<String, Object> dictionary = configuration.getProperties();
			if (dictionary == null) {
				dictionary = new Hashtable<String, Object>();
			}

			dictionary.put("institute", "JavaBank");
			dictionary.put("fee", "1500");
			LOG.info("updating configuration");

			configuration.setBundleLocation(null);
			configuration.update(dictionary);
		} catch (IOException e) {
			LOG.error("Failed to create a configuration");
		}
	}

	@Reference(unbind = "unsetConfigAdminService")
	protected void setConfigAdminService(ConfigurationAdmin ca) {
		this.configAdmin = ca;
	}

	protected void unsetConfigAdminService(ConfigurationAdmin ca) {
		this.configAdmin = null;
	}
}
