/*
 * Copyright 2014, NATIZ and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cg.natiz.batch.pop.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author natiz
 * 
 */
public class PopResources {

	private static final Logger logger = LoggerFactory.getLogger(PopProperties.class);

	/**
	 * Expose logger using the resource producer patterns
	 * 
	 * @param ip
	 * @return
	 */
	@Produces
	Logger getLogger(InjectionPoint ip) {
		return LoggerFactory.getLogger(ip.getMember().getDeclaringClass());
	}

	/**
	 * 
	 * @param ip
	 * @return
	 */
	@Produces
	@Property
	public String injectStringProperty(InjectionPoint ip) {
		Property property = ip.getAnnotated().getAnnotation(Property.class);
		Properties properties = this.loadFromFile(property.src()[0]);
		String value = properties.getProperty(property.key(), property.defaultValue());
		if (property.mandatory() && (value == null || value.isEmpty())) {
			throw new IllegalStateException(MessageFormat.format("Value is required for the key %s", property.key()));
		}
		return value;
	}

	/**
	 * 
	 * @param ip
	 * @return
	 */
	@Produces
	@Property
	public int injectIntegerProperty(InjectionPoint ip) {
		return Integer.parseInt(injectStringProperty(ip));
	}

	/**
	 * 
	 * @param ip
	 * @return
	 * @throws IllegalStateException
	 */
	@Produces
	@Property
	public Long injectLongProperty(InjectionPoint ip) {
		return Long.parseLong(injectStringProperty(ip));
	}

	/**
	 * 
	 * @param ip
	 * @return
	 */
	@Produces
	@PopConfig
	public PopProperties injectPopProperties(InjectionPoint ip) throws IllegalStateException {
		PopConfig popConfig = ip.getAnnotated().getAnnotation(PopConfig.class);
		if (popConfig == null || popConfig.value() == null || popConfig.value().length == 0) {
			throw new IllegalStateException(
					"PopConfig value not found in " + ip.getMember().getDeclaringClass().getName());
		}
		PopProperties pp = new PopProperties();

		// Properties properties = this.loadFromFile(popConfig.value()[0]);

		// int providerWorkerCount =
		// Integer.parseInt(properties.getProperty("provider.worker.count",
		// "1"));
		// int processorWorkerCount =
		// Integer.parseInt(properties.getProperty("processor.worker.count",
		// "1");
		// int consumerWorkerCount =
		// Integer.parseInt(properties.getProperty("consumer.worker.count",
		// "1");

		return pp;
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	private Properties loadFromFile(String path) {
		InputStream in = null;
		Properties properties = new Properties();
		try {
			URL url = this.getClass().getClassLoader().getResource(path);
			logger.debug("Loading properties from the file : {}", url);
			in = this.getClass().getClassLoader().getResourceAsStream(path);
			properties.load(in);
		} catch (Exception e) {
			logger.error("Cannot load the properties file : {}", path);
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.warn("Cannot close the properties InputStream", e);
				}
			}
		}
		return properties;
	}
}
