package com.cosson.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "multitenant")
public class DynamicDataSourceConfigProperties {
	private List<DataSourceProperties> dataSources;

	public List<DataSourceProperties> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<DataSourceProperties> dataSources) {
		this.dataSources = dataSources;
	}

	public static class DataSourceProperties extends org.springframework.boot.autoconfigure.jdbc.DataSourceProperties {
		private boolean autoCommit;
		private String tenant;

		public boolean isAutoCommit() {
			return autoCommit;
		}

		public void setAutoCommit(boolean autoCommit) {
			this.autoCommit = autoCommit;
		}

		public String getTenant() {
			return tenant;
		}

		public void setTenant(String tenant) {
			this.tenant = tenant;
		}
	}
}
