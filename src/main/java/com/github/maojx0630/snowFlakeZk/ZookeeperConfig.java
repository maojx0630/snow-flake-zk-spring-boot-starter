package com.github.maojx0630.snowFlakeZk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author MaoJiaXing
 * @date 2019-08-28 15:50 
 */
@Configuration
@ConfigurationProperties(prefix = "zk")
public class ZookeeperConfig {

	//根目录
	private String root = "/Snowflake";

	//zk连接地址
	private String connect = "127.0.0.1:2181";

	//重试次数
	private int retryCount = 0;

	//重试间隔
	private int retryInterval = 1000;

	//session超时时间
	private int sessionTimeoutMs = 1000;

	//连接超市时间
	private int connectionTimeoutMs = 1000;

	//是否探测zk是否可用,不支持zk集群模式下使用,集群模式请设置为false
	private boolean detection = true;

	//若无法连接zk 则使用默认
	private long defaultWorkerId = 0;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getConnect() {
		return connect;
	}

	public void setConnect(String connect) {
		this.connect = connect;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public int getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	public void setSessionTimeoutMs(int sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
	}

	public int getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}

	public void setConnectionTimeoutMs(int connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}

	public boolean isDetection() {
		return detection;
	}

	public void setDetection(boolean detection) {
		this.detection = detection;
	}

	public long getDefaultWorkerId() {
		return defaultWorkerId;
	}

	public void setDefaultWorkerId(long defaultWorkerId) {
		this.defaultWorkerId = defaultWorkerId;
	}
}
