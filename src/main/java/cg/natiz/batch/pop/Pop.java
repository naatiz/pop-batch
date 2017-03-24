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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;
import cg.natiz.batch.pop.util.ExecutionOption;
import cg.natiz.batch.pop.util.PopProperties;

/**
 * 
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
public class Pop<T1 extends Serializable, T2 extends Serializable> implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(Pop.class);

	private PopProperties popProperties;
	private ExecutionOption[] executionOptions;

	private Set<Callable<Reporting>> workers;

	/**
	 * Add PoP configuration
	 * 
	 * @param popProperties
	 * @return Pop instance with ExecutionOption set to <code>ExecutionOption.NONE</code>
	 */
	public Pop<T1, T2> addConfiguration(PopProperties popProperties) {
		return addConfiguration(popProperties, ExecutionOption.NONE);
	}

	/**
	 * Add PoP configuration
	 * 
	 * @param popProperties
	 * @param executionOptions
	 * @return Pop instance
	 */
	public Pop<T1, T2> addConfiguration(PopProperties popProperties, ExecutionOption... executionOptions) {
		this.popProperties = popProperties;
		this.executionOptions = executionOptions;
		return this;
	}

	/**
	 * Build Pop before execution
	 * 
	 * @param provider
	 *            a data provider
	 * 
	 * @param processor
	 *            a data processor
	 * 
	 * @param consumer
	 *            a processed data consumer
	 * @return Pop instance
	 */
	@SuppressWarnings("unchecked")
	public Pop<T1, T2> build(@Controller(ControllerType.PROVIDER) Puller<T1> provider,
			@Controller(ControllerType.PROCESSOR) Processor<T1, T2> processor,
			@Controller(ControllerType.CONSUMER) Pusher<T2> consumer) {

		logger.info("Incoming/Outcoming repositories initializing ... ");
		Repository<T1> incoming = Pop.newInstance(Repository.class);
		Repository<T2> outcoming = Pop.newInstance(Repository.class);

		this.workers = new HashSet<Callable<Reporting>>(popProperties.getPool());
		logger.info("Provider worker initializing ... ");
		int count = popProperties.getProviderWorkerCount();
		for (int i = 0; i < count; i++) {
			this.workers.add(Pop.newInstance(IncomingWorker.class).init(provider, incoming));
		}

		logger.info("Processor worker initializing ... ");
		count = popProperties.getProcessorWorkerCount();
		for (int i = 0; i < count; i++) {
			this.workers.add(Pop.newInstance(DispatcherWorker.class).init(processor, incoming, outcoming));
		}

		logger.info("Consumer worker initializing ... ");
		count = popProperties.getConsumerWorkerCount();
		for (int i = 0; i < count; i++) {
			this.workers.add(Pop.newInstance(OutcomingWorker.class).init(consumer, outcoming));
		}

		logger.info("PoP settings {}", popProperties);
		logger.info("PoP execution options {}", (Object[]) executionOptions);

		return this;
	}

	/**
	 * Execute a current thread pool
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(popProperties.getPool());
		logger.info("PoP engine start running ... ");
		List<Future<Reporting>> futures = executorService.invokeAll(this.workers);
		for (Future<Reporting> future : futures) {
			logger.debug(future.get().toString());
		}
		executorService.shutdown();
		logger.info("PoP engine ends successfully");
	}

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
			logger.error("Instanciation failed: {}", e.getMessage());
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.error("Access denied: {}", e.getMessage());
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
