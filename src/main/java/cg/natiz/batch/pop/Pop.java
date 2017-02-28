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

	private Puller<T1> provider;
	private Processor<T1, T2> processor;
	private Pusher<T2> consumer;

	private ExecutorService executorService;

	public Pop<T1, T2> setProvider(Puller<T1> provider) {
		this.provider = provider;
		return this;
	}

	public Pop<T1, T2> setProcessor(Processor<T1, T2> processor) {
		this.processor = processor;
		return this;
	}

	public Pop<T1, T2> setConsumer(Pusher<T2> consumer) {
		this.consumer = consumer;
		return this;
	}

	/**
	 * 
	 * @param popProperties
	 * @param executionOptions
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void execute(PopProperties popProperties, ExecutionOption... executionOptions) throws Exception {

		logger.info("Incoming/Outcoming repositories initializing ... ");
		Repository<T1> incoming = Pop.newInstance(Repository.class);
		Repository<T2> outcoming = Pop.newInstance(Repository.class);

		logger.info("PoP settings {}", popProperties);
		this.executorService = Executors.newFixedThreadPool(popProperties.getPool());

		Set<Callable<String>> workers = new HashSet<Callable<String>>(popProperties.getPool());
		logger.info("Provider worker initializing ... ");
		int count = popProperties.getProviderWorkerCount();
		for (int i = 0; i < count; i++) {
			workers.add(Pop.newInstance(IncomingWorker.class).setProvider(provider).setIncoming(incoming));
		}

		logger.info("Processor worker initializing ... ");
		count = popProperties.getProcessorWorkerCount();
		for (int i = 0; i < count; i++) {
			workers.add(Pop.newInstance(DispatcherWorker.class).setProcessor(processor).setIncoming(incoming)
					.setOutcoming(outcoming));
		}

		logger.info("Consumer worker initializing ... ");
		count = popProperties.getConsumerWorkerCount();
		for (int i = 0; i < count; i++) {
			workers.add(Pop.newInstance(OutcomingWorker.class).setConsumer(consumer).setOutcoming(outcoming));
		}

		logger.info("PoP workers start running ... ");
		List<Future<String>> futures = executorService.invokeAll(workers);
		for (Future<String> future : futures) {
			logger.debug(future.get());
		}
		executorService.shutdown();
		logger.info("PoP workers end running successfully");
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
