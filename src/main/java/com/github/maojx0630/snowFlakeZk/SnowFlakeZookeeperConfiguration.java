package com.github.maojx0630.snowFlakeZk;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 *
 * <br/>
 * @author MaoJiaXing
 * @date 2019-08-29 14:43 
 */
public class SnowFlakeZookeeperConfiguration {

	@Bean
	@ConditionalOnMissingBean(ZookeeperOnlineOffline.class)
	public SnowFlakeZkApplicationRunner snowFlakeZkApplicationRunner(ZookeeperConfig config) {
		return new SnowFlakeZkApplicationRunner(config, null);
	}

	@Bean
	@ConditionalOnBean(ZookeeperOnlineOffline.class)
	public SnowFlakeZkApplicationRunner snowFlakeZkApplicationRunner(ZookeeperConfig config,
	                                                                 ZookeeperOnlineOffline zoo) {
		return new SnowFlakeZkApplicationRunner(config, zoo);
	}
}
