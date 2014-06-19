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

import javax.enterprise.inject.Produces;


import javax.inject.Named;

import cg.natiz.batch.pop.IncomingWorker;
import cg.natiz.batch.pop.OutcomingWorker;
import cg.natiz.batch.pop.Pop;
import cg.natiz.batch.pop.ProcessorWorker;
import cg.natiz.batch.pop.Repository;

/**
 * 
 * @author natiz
 * 
 */
public class PopRunnerResources {

	@Named
	@Produces
	@SuppressWarnings("unchecked")
	Repository<String> getIncoming() {
		return Pop.newInstance(Repository.class);
	}

	@Named
	@Produces
	@SuppressWarnings("unchecked")
	Repository<Long> getOutcoming() {
		return Pop.newInstance(Repository.class);
	}

	@Named
	@Produces
	@SuppressWarnings("unchecked")
	IncomingWorker<String> getIncomingWorker() {
		return Pop.newInstance(IncomingWorker.class);
	}

	@Named
	@Produces
	@SuppressWarnings("unchecked")
	ProcessorWorker<String, Long> getProcessorWorker() {
		return Pop.newInstance(ProcessorWorker.class);
	}

	@Named
	@Produces
	@SuppressWarnings("unchecked")
	OutcomingWorker<Long> getOutcomingWorker() {
		return Pop.newInstance(OutcomingWorker.class);
	}
}
