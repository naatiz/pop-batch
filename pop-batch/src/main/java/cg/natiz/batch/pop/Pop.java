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

import java.io.Serializable;

import javax.inject.Inject;

import org.slf4j.Logger;


/**
 * 
 * @author natiz
 *
 */
@SuppressWarnings("serial")
public class Pop <T1 extends Serializable, T2 extends Serializable> implements
Serializable {
	
	@Inject
	private static Logger logger;

	/**
	 * Build a new instance of clazz
	 * 
	 * @param clazz
	 *            Class of the object to be built
	 * @return clazz instance
	 */
	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			StringBuilder sb = new StringBuilder("Field instanciation failed");
			logger.error(sb.toString());
			throw new IllegalStateException(sb.toString(), e);
		} catch (IllegalAccessException e) {
			StringBuilder sb = new StringBuilder("Field access failed");
			logger.error(sb.toString());
			throw new IllegalStateException(sb.toString(), e);
		}
	}
}
