package com.github.maojx0630.snowFlakeZk;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 *
 * <br/>
 * @author MaoJiaXing
 * @date 2019-08-29 12:39 
 */
public class SnowFlakeZkApplicationRunner implements ApplicationRunner {

	private ZookeeperConfig zookeeperConfig;

	private ZookeeperOnlineOffline zoo;

	public SnowFlakeZkApplicationRunner(ZookeeperConfig zookeeperConfig) {
		this.zookeeperConfig = zookeeperConfig;
	}

	public SnowFlakeZkApplicationRunner(ZookeeperConfig zookeeperConfig, ZookeeperOnlineOffline zoo) {
		this.zookeeperConfig = zookeeperConfig;
		this.zoo = zoo;
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		new SnowFlakeZookeeper(zookeeperConfig, zoo);
	}
}
