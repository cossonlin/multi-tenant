package com.cosson.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(basePackages = {"com.cosson.repo"},
		transactionManagerRef = "transactionManagerCommon",
		entityManagerFactoryRef = "entityManagerFactoryCommon")
@EnableTransactionManagement
public class DynamicDBConfig {
	@Value("${jpa.properties.hibernate.dialect}")
	private String dbDialect;
	@Value("${jpa.hibernate.ddl-auto}")
	private String dbDdlAuto;
	@Value("${jpa.hibernate.naming.physical-strategy}")
	private String dbPhysicalStrategy;

	private DynamicDataSourceConfigProperties dynamicDataSourceConfigProperties;

	public DynamicDBConfig(DynamicDataSourceConfigProperties dynamicDataSourceConfigProperties) {
		this.dynamicDataSourceConfigProperties = dynamicDataSourceConfigProperties;
	}

	@Bean("entityManagerFactoryCommon")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryCommon() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dynamicDataSource());
		em.setPackagesToScan(new String[]{"com.cosson.entity"});

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaPropertyMap(getJpaProperties());

		return em;
	}

	private Map<String, Object> getJpaProperties() {
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", dbDialect);
		properties.put("hibernate.hbm2ddl.auto", dbDdlAuto);
		properties.put("hibernate.physical_naming_strategy", dbPhysicalStrategy);
		return properties;
	}

	@Bean
	@Primary
	public PlatformTransactionManager transactionManagerCommon(@Qualifier("entityManagerFactoryCommon") EntityManagerFactory entityManagerFactoryCommon) {
		return new JpaTransactionManager(entityManagerFactoryCommon);
	}

	@Bean
	@Primary
	public DataSource dynamicDataSource() {
		DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource(dynamicDataSourceConfigProperties);
		Map<Object, Object> dataSourceMap = new HashMap<>();
		String defaultSchema = dynamicRoutingDataSource.getDefaultSchema();
		DataSource defaultDS = dynamicRoutingDataSource.createNewDataSource(defaultSchema);
		dataSourceMap.put(defaultSchema, defaultDS);
		dynamicRoutingDataSource.setDefaultTargetDataSource(defaultDS);
		dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);
		return dynamicRoutingDataSource;
	}
}
