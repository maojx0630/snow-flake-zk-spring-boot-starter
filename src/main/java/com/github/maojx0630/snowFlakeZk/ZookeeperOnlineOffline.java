package com.github.maojx0630.snowFlakeZk;

/**
 *
 * <br/>
 * @author MaoJiaXing
 * @date 2019-08-29 14:19 
 */
public interface ZookeeperOnlineOffline {

	//上线
	default void online() {
	}

	//离线
	default void offline() {

	}
}
