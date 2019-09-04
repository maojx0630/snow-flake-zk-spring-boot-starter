package com.github.maojx0630.snowFlakeZk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 *
 * @author MaoJiaXing
 * @date 2019-08-28 14:51
 */
public class SnowFlakeZookeeper {

	private static final Logger log = LoggerFactory.getLogger(SnowFlakeZookeeper.class);

	private CuratorFramework client;

	private ZookeeperConfig config;

	private String uuid;

	private long workerId = 0L;

	public SnowFlakeZookeeper(ZookeeperConfig config) {

		//探测zk是否启动
		if (config.isDetection()) {
			try {
				String[] array = config.getConnect().split(":");
				Socket socket = new Socket(array[0], Integer.valueOf(array[1]));
				socket.close();
			} catch (Exception e) {
				workerId = config.getDefaultWorkerId();
				log.error("zk连接失败,使用默认参数初始化,若非集群模式请取消zk获取改为固定值,目前workerID为[{}]", workerId);
				return;
			}

		}
		//初始化zk操作工具
		this.config = config;
		client = CuratorFrameworkFactory.builder().
				connectString(config.getConnect()).
				sessionTimeoutMs(config.getSessionTimeoutMs()).
				connectionTimeoutMs(config.getConnectionTimeoutMs()).
				retryPolicy(new RetryNTimes(config.getRetryCount(), config.getRetryInterval())).
				build();
		//开启操作zk node
		client.start();
		try {
			//创建root节点
			client.create().withMode(CreateMode.PERSISTENT).forPath(config.getRoot());
		} catch (Exception e) {
			//若节点已存在则忽略,不存在则抛出异常
			if (!(e instanceof KeeperException.NodeExistsException)) {
				throw new RuntimeException(e.getMessage());
			}
		}
		//初始化集群与机器id
		initWorkIdAndCenterId();
		//添加连接监听,处理zk断开的意外情况
		client.getConnectionStateListenable().addListener((curatorFramework, connectionState) -> {
			if (connectionState.isConnected()) {
				if (connectionState == ConnectionState.RECONNECTED) {
					try {
						String path = config.getRoot() + "/" + workerId;
						String str = new String(client.getData().forPath(path), StandardCharsets.UTF_8);
						if (str.equals(uuid)) {
							try {
								client.delete().forPath(path);
								log.info("zk连接已恢复,zk存储信息比对成功,即将移除并重新注册");
							} catch (Exception ignored) {
							}
						}
						client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
						log.info("重新注册成功,目前workerID为[{}]", workerId);
					} catch (KeeperException.NodeExistsException | KeeperException.NoNodeException exception) {
						try {
							log.info("zk连接已恢复,但当前节点信息已被占用,或已被清除将重新注册");
							initWorkIdAndCenterId();
							log.info("重新注册,并重新初始化雪花信息成功,目前workerID为[{}]", workerId);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				log.error("zk连接已断开!!!!");
				log.error("zk连接已断开!!!!");
				log.error("zk连接已断开!!!!");
			}
		});
	}

	private void initWorkIdAndCenterId() {
		workerId = 0;
		while (true) {
			try {
				//创建子节点
				String path = config.getRoot() + "/" + workerId;
				client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
				uuid = UUID.randomUUID().toString();
				client.setData().forPath(path, uuid.getBytes(StandardCharsets.UTF_8));
			} catch (KeeperException.NodeExistsException exception) {
				workerId++;
				if (Sequence.test(workerId)) {
					continue;
				} else {
					throw new RuntimeException("超出最大id生成规则,不能启动!");
				}
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			IdUtils.initSequence(workerId);
			break;
		}
	}
}
