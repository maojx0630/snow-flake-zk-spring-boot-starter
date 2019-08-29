package com.github.maojx0630.snowFlakeZk;

import org.springframework.context.annotation.Bean;

/**
 *
 * @author MaoJiaXing
 * @date 2019-08-29 14:43 
 */
public class SnowFlakeZookeeperConfiguration {

	@Bean
	public SnowFlakeZkApplicationRunner snowFlakeZkApplicationRunner(ZookeeperConfig config) {
		return new SnowFlakeZkApplicationRunner(config);
	}

}
