package com.cosson.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
	private static Map<Object, Object> targetDataSources = new HashMap<>();

	private final DynamicDataSourceConfigProperties dynamicDataSourceConfigProperties;

	@Autowired
	public DynamicRoutingDataSource(DynamicDataSourceConfigProperties dynamicDataSourceConfigProperties) {
		this.dynamicDataSourceConfigProperties = dynamicDataSourceConfigProperties;
	}

	public static boolean isExistDataSource(String key) {
		return targetDataSources.containsKey(key);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSourceKey = DynamicDataSourceContextHolder.getDataSourceKey();
		if (dataSourceKey == null || dataSourceKey.length() == 0) {
			dataSourceKey = this.getDefaultSchema();
			DynamicDataSourceContextHolder.setDataSourceKey(dataSourceKey);
		}
		log.info("Current DataSource is {}", dataSourceKey);
		return dataSourceKey;
	}

	public String getDefaultSchema() {
		return this.dynamicDataSourceConfigProperties.getDataSources().get(0).getTenant();
	}

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
		DynamicRoutingDataSource.targetDataSources = targetDataSources;
	}

	public synchronized boolean addDataSource(String tenant) {
		if (tenant == null || tenant.length() == 0) {
			return false;
		}
		if (DynamicRoutingDataSource.isExistDataSource(tenant)) return true;
		DataSource newDataSource = null;
		try {
			newDataSource = this.createNewDataSource(tenant);
			targetDataSources.put(tenant, newDataSource);
			super.setTargetDataSources(targetDataSources);
			this.afterPropertiesSet();
			log.info("dataSource {} has been added", tenant);
		} finally {
			if (newDataSource != null) {
				try (Connection conn = newDataSource.getConnection()) {
				} catch (SQLException e) {
					log.error("release datasource connection failed: " + e);
				}
			}
			return true;
		}
	}

	public DataSource createNewDataSource(String tenant) {
		for (DynamicDataSourceConfigProperties.DataSourceProperties dsCfg : this.dynamicDataSourceConfigProperties.getDataSources()) {
			if (Objects.equals(tenant, dsCfg.getTenant())) {
				HikariConfig jdbcConfig = new HikariConfig();
				jdbcConfig.setPoolName(tenant + "DS");
				jdbcConfig.setDataSourceJNDI(dsCfg.getName());
				jdbcConfig.setJdbcUrl(dsCfg.getUrl());
				jdbcConfig.setUsername(dsCfg.getUsername());
				jdbcConfig.setPassword(dsCfg.getPassword());
				jdbcConfig.setAutoCommit(dsCfg.isAutoCommit());
				return new HikariDataSource(jdbcConfig);
			}
		}
		throw new IllegalArgumentException(tenant + " has not been setup");
	}
}
