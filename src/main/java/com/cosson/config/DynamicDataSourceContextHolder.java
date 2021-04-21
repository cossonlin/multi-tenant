package com.cosson.config;

public class DynamicDataSourceContextHolder {
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>() {
	};

	public static String getDataSourceKey() {
		return contextHolder.get();
	}

	public static void setDataSourceKey(String key) {
		contextHolder.set(key);
	}

	public static void clearDataSourceKey() {
		contextHolder.remove();
	}
}
