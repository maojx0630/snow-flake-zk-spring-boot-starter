package com.github.maojx0630.snowFlakeZk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Twitter的Snowflake算法实现分布式高效有序ID生产黑科技(sequence)——升级版Snowflake
 * <p>优化开源项目：https://gitee.com/yu120/sequence</p>
 * @author lry
 * @version 3.0
 */
final class Sequence {

	private static final Logger log = LoggerFactory.getLogger(Sequence.class);
	/**
	 * 起始时间戳
	 **/
	final static long START_TIME = 1519740777809L;


	final static long WORKER_ID_BITS = 10L;
	/**
	 * 序列号占用的位数：12（表示只允许workId的范围为：0-4095）
	 **/
	private final static long SEQUENCE_BITS = 12L;


	private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

	final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
	final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

	/**
	 * 用mask防止溢出:位与运算保证计算的结果范围始终是 0-4095
	 **/
	private final static long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

	private final static long timeOffset = 5L;

	private final long workerId;
	private long sequence = 0L;
	private long lastTimestamp = -1L;

	/**
	 * 基于Snowflake创建分布式ID生成器
	 *
	 * @param workerId       工作机器ID,数据范围为0~1023
	 */
	Sequence(long workerId) {

		if (workerId > MAX_WORKER_ID || workerId < 0) {
			throw new IllegalArgumentException("Worker Id can't be greater than " + MAX_WORKER_ID + " or less than 0");
		}
		this.workerId = workerId;
		log.info("Sequence初始化成功,目前workerId为[{}]", workerId);
	}

	/**
	 * 获取ID
	 *
	 * @return long
	 */
	synchronized Long nextId() {
		long currentTimestamp = this.timeGen();

		// 闰秒：如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，这个时候应当抛出异常
		if (currentTimestamp < lastTimestamp) {
			// 校验时间偏移回拨量
			long offset = lastTimestamp - currentTimestamp;
			if (offset > timeOffset) {
				throw new RuntimeException("Clock moved backwards, refusing to generate id for [" + offset + "ms]");
			}

			try {
				// 时间回退timeOffset毫秒内，则允许等待2倍的偏移量后重新获取，解决小范围的时间回拨问题
				this.wait(offset << 1);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			// 再次获取
			currentTimestamp = this.timeGen();
			// 再次校验
			if (currentTimestamp < lastTimestamp) {
				throw new RuntimeException("Clock moved backwards, refusing to generate id for [" + offset + "ms]");
			}
		}

		if (lastTimestamp == currentTimestamp) {
			// 相同毫秒内，序列号自增
			sequence = (sequence + 1) & SEQUENCE_MASK;
			if (sequence == 0) {
				// 同一毫秒的序列数已经达到最大
				currentTimestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			// 不同毫秒内，序列号置为 1 - 3 随机数
			sequence = 0L;
		}

		lastTimestamp = currentTimestamp;
		long currentOffsetTime = currentTimestamp - START_TIME;

		/*
		 * 1.左移运算是为了将数值移动到对应的段(41、5、5，12那段因为本来就在最右，因此不用左移)
		 * 2.然后对每个左移后的值(la、lb、lc、sequence)做位或运算，是为了把各个短的数据合并起来，合并成一个二进制数
		 * 3.最后转换成10进制，就是最终生成的id
		 */
		return (currentOffsetTime << TIMESTAMP_LEFT_SHIFT) |
				// 工作ID位
				(workerId << WORKER_ID_SHIFT) |
				// 毫秒序列化位
				sequence;
	}

	/**
	 * 保证返回的毫秒数在参数之后(阻塞到下一个毫秒，直到获得新的时间戳)——CAS
	 *
	 * @param lastTimestamp last timestamp
	 * @return next millis
	 */
	private long tilNextMillis(long lastTimestamp) {
		long timestamp = this.timeGen();
		while (timestamp <= lastTimestamp) {
			// 如果发现时间回拨，则自动重新获取（可能会处于无限循环中）
			timestamp = this.timeGen();
		}

		return timestamp;
	}

	/**
	 * 获得系统当前毫秒时间戳
	 *
	 * @return timestamp 毫秒时间戳
	 */
	private long timeGen() {
		return SystemClock.INSTANCE.currentTimeMillis();
	}

	static boolean test(long workerId) {
		return workerId <= MAX_WORKER_ID && workerId >= 0;
	}

	long getWorkerId() {
		return workerId;
	}
}