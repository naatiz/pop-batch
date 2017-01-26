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
package cg.natiz.batch.pop;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.PopConfig;
import cg.natiz.batch.pop.util.PopProperties;
import cg.natiz.batch.pop.util.Property;

/**
 * 
 * @author natiz
 * 
 */
public class PopResources {

	// Expose logger using the resource producer pattern
	@Produces
	Logger getLogger(InjectionPoint ip) {
		return LoggerFactory.getLogger(ip.getMember().getDeclaringClass());
	}

	@Produces
	@PopConfig
	public PopProperties injectPropertiesConfiguration(InjectionPoint ip)
			throws IllegalStateException {
		PopConfig popConfig = ip.getAnnotated().getAnnotation(PopConfig.class);
		if (popConfig == null || popConfig.value() == null
				|| popConfig.value().length == 0)
			throw new IllegalStateException(
					"PopConfig value not found in "
							+ ip.getMember().getDeclaringClass().getName());
		return Pop.newInstance(PopProperties.class).loadPrperties(popConfig.value());
	}

	@Produces
	@Property
	public String injectProperty(InjectionPoint ip)
			throws IllegalStateException {
		final String MANDATORY_VALUE_MISSING = "Value required for the key %s";
		String[] filePath = { "/pop.cfg" };
		Property property = ip.getAnnotated().getAnnotation(Property.class);
		if (filePath == null || filePath.length == 0 || property.key() == null
				|| property.key().isEmpty()) {
			return property.defaultValue();
		}
		String value;
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(filePath[0]);
			value = bundle.getString(property.key());
			if (value == null || value.trim().length() == 0) {
				if (property.mandatory()) {
					throw new IllegalStateException(MessageFormat.format(
							MANDATORY_VALUE_MISSING, property.key()));
				} else {
					return property.defaultValue();
				}
			}
			return value;
		} catch (MissingResourceException e) {
			if (property.mandatory())
				throw new IllegalStateException(String.format(
						MANDATORY_VALUE_MISSING, property.key()));
			// return MessageFormat.format("Invalid key %s", property.key());
			return property.defaultValue();
		}
	}
}
