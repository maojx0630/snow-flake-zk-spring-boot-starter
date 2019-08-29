package com.github.maojx0630.snowFlakeZk;

import java.util.Date;

import static com.github.maojx0630.snowFlakeZk.Sequence.*;

/**
 *
 * <br/>
 * @author MaoJiaXing
 * @date 2019-08-29 13:44 
 */
public final class SnowflakeUtils {

	private static Sequence sequence = new Sequence(0);

	/**
	 * 根据Snowflake的ID，获取机器id
	 *
	 * @param id snowflake算法生成的id
	 * @return 所属机器的id
	 */
	public static long getWorkerId(long id) {
		return id >> WORKER_ID_SHIFT & ~(-1L << WORKER_ID_BITS);
	}

	/**
	 * 获取当前正在使用的workerId
	 */
	public static long getWorkerId() {
		return sequence.getWorkerId();
	}

	/**
	 *根据Snowflake的ID，获取生成时间
	 *
	 * @param id snowflake算法生成的id
	 * @return 生成的时间
	 */
	public static long getTime(long id) {
		return (id >> TIMESTAMP_LEFT_SHIFT & ~(-1L << 41L)) + START_TIME;
	}

	/**
	 *根据Snowflake的ID，获取生成时间
	 *
	 * @param id snowflake算法生成的id
	 * @return 生成的时间
	 */
	public static Date getDate(long id) {
		return new Date(getTime(id));
	}

	public static Long next() {
		return sequence.nextId();
	}

	public static String nextStr() {
		return String.valueOf(sequence.nextId());
	}

	public static void initSequence(long workerId) {
		sequence = new Sequence(workerId);
	}
}
