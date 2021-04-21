package com.cosson.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Aspect
@Component
public class DynamicDataSourceAspect {
	@Autowired
	private DynamicRoutingDataSource dynamicDataSource;

	@Pointcut("execution(* *..controller.*Controller+.*(..))")
	public void controllerAspect() {
		log.info("pointcut at controller");
	}

	@Before("controllerAspect()")
	public void switchDataSource(JoinPoint point) {
		Object[] args = point.getArgs();
		String tenant = args[0].toString();

		if (tenant != null && tenant.length() != 0) {
			if (DynamicRoutingDataSource.isExistDataSource(tenant)) {
				if (!Objects.equals(tenant, DynamicDataSourceContextHolder.getDataSourceKey())) {
					DynamicDataSourceContextHolder.setDataSourceKey(tenant);
				}
			} else {
				dynamicDataSource.addDataSource(tenant);
				DynamicDataSourceContextHolder.setDataSourceKey(tenant);
			}
			log.info("Switch DataSource to {} in Method {}", DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
		}
	}
}
