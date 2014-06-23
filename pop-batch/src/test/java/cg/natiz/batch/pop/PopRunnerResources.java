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
import javax.inject.Inject;
import javax.inject.Named;

import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;
import cg.natiz.batch.pop.util.PopConfiguration;
import cg.natiz.batch.pop.util.Property;
import cg.natiz.batch.pop.util.Savings;
import cg.natiz.batch.pop.util.Processor;
import cg.natiz.batch.pop.util.Puller;
import cg.natiz.batch.pop.util.Pusher;

/**
 * 
 * @author natiz
 * 
 */
public class PopRunnerResources {

	@Inject
	@Savings
	@Controller(ControllerType.PROVIDER)
	private Puller<String> sender;
	@Inject
	@Savings
	@Controller(ControllerType.PROCESSOR)
	private Processor<String, Long> processor;
	@Inject
	@Savings
	@Controller(ControllerType.CONSUMER)
	private Pusher<Long> recipient;

	@Named
	@Produces
	@SuppressWarnings("unchecked")
	Pop<String, Long> getPop() {
		Pop<String, Long> pop = Pop.newInstance(Pop.class).setProvider(sender)
				.setConsumer(recipient).setProcessor(processor);
		return pop;
	}

	@Produces
	@Property
	public String injectProperty(InjectionPoint ip)
			throws IllegalStateException {

		final String MANDATORY_PARAM_MISSING = "No definition found for a mandatory"
				+ " configuration parameter : %s";
		PopConfiguration config = PopRunner.class
				.getAnnotation(PopConfiguration.class);
		Property param = ip.getAnnotated().getAnnotation(Property.class);
		if (param.key() == null || param.key().isEmpty()) {
			return param.defaultValue();
		}
		String value;
		try {

			ResourceBundle bundle = ResourceBundle.getBundle(config.value()[0]);
			value = bundle.getString(param.key());
			if (value == null || value.trim().length() == 0) {
				if (param.mandatory())
					throw new IllegalStateException(MessageFormat.format(
							MANDATORY_PARAM_MISSING, param.key()));
				else
					return param.defaultValue();
			}
			return value;
		} catch (MissingResourceException e) {
			if (param.mandatory())
				throw new IllegalStateException(String.format(
						MANDATORY_PARAM_MISSING, param.key()));
			//return MessageFormat.format("Invalid key %s", param.key());
			return param.defaultValue();
		}
	}
}
