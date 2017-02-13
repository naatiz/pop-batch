package cg.natiz.batch.pop.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author natiz
 */
@SuppressWarnings("serial")
public class PopProperties implements Serializable {
	private static final Logger logger = LoggerFactory
			.getLogger(PopProperties.class);

	int providerWorkerCount = 1;
	int processorWorkerCount = 1;
	int consumerWorkerCount = 1;

	public int getPool() {
		int pool = getProviderWorkerCount();
		pool += getProcessorWorkerCount();
		pool += getConsumerWorkerCount();
		return pool;
	}

	public int getProviderWorkerCount() {
		return Math.max(1, providerWorkerCount);
	}

	public int getProcessorWorkerCount() {
		return Math.max(1, processorWorkerCount);
	}

	public int getConsumerWorkerCount() {
		return Math.max(1, consumerWorkerCount);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PopProperties [providerWorkerCount=")
				.append(providerWorkerCount).append(", processorWorkerCount=")
				.append(processorWorkerCount).append(", consumerWorkerCount=")
				.append(consumerWorkerCount).append(", pool=").append(getPool())
				.append("]");
		return sb.toString();
	}

	public PopProperties loadPrperties(String ...path) {
		InputStream in = null;
		try {
			logger.info("Loading pop properties from the file : " + this.getClass().getClassLoader().getResource(path[0]));
			in = this.getClass().getClassLoader().getResourceAsStream(path[0]);
			Properties properties = new Properties();
			properties.load(in);

			providerWorkerCount = Integer.valueOf(properties.getProperty(
					"provider.worker.count", "1"));
			processorWorkerCount = Integer.valueOf(properties.getProperty(
					"processor.worker.count", "1"));
			consumerWorkerCount = Integer.valueOf(properties.getProperty(
					"consumer.worker.count", "1"));

		} catch (Exception e) {
			logger.warn("Cannot load the properties file : " + path[0], e);
			logger.warn("Using default pop settings used");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.warn("Cannot close the properties input stream", e);
				}
			}
			logger.info("Pop settings : " + this);
		}
		return this;
	}
}
