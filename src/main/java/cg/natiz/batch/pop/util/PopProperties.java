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

	int providerWorker = 1;
	int processorWorker = 1;
	int consumerWorker = 1;

	public int getPool() {
		int pool = getProviderWorker();
		pool += getProcessorWorker();
		pool += getConsumerWorker();
		return pool;
	}

	public int getProviderWorker() {
		return Math.max(1, providerWorker);
	}

	public int getProcessorWorker() {
		return Math.max(1, processorWorker);
	}

	public int getConsumerWorker() {
		return Math.max(1, consumerWorker);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PopProperties [providerWorker=")
				.append(providerWorker).append(", processorWorker=")
				.append(processorWorker).append(", consumerWorker=")
				.append(consumerWorker).append(", pool=").append(getPool())
				.append("]");
		return sb.toString();
	}

	public PopProperties loadPrperties(String ...path) {
		InputStream in = null;
		try {
			logger.info("Loading pop properties from the file : " + path[0]);
			in = this.getClass().getClassLoader().getResourceAsStream(path[0]);
			Properties properties = new Properties();
			properties.load(in);

			providerWorker = Integer.valueOf(properties.getProperty(
					"provider.worker", "1"));
			processorWorker = Integer.valueOf(properties.getProperty(
					"processor.worker", "1"));
			consumerWorker = Integer.valueOf(properties.getProperty(
					"consumer.worker", "1"));

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
