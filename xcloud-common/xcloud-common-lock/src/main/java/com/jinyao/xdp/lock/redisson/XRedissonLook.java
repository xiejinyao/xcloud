package com.jinyao.xdp.lock.redisson;

import com.jinyao.xdp.lock.IXLock;
import com.jinyao.xdp.lock.exception.XGetLockFailedException;
import com.jinyao.xdp.lock.function.XLockCallBackFunction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Redis读锁实现
 *
 * @author 谢进伟
 * @createDate 2022/8/24 14:51
 */
@Slf4j
@AllArgsConstructor
public class XRedissonLook implements IXLock {

	private final RedissonClient redisson;

	private static final String KEY_PREFIX = "x-distributed-lock::";

	/**
	 * 互斥锁
	 *
	 * @param call     回调
	 * @param key      加锁的key
	 * @param maxWait  等待的时间
	 * @param waitUnit 时间单位
	 * @return
	 */
	@Override
	public void lock(XLockCallBackFunction call, String key, long maxWait, TimeUnit waitUnit) throws Exception {
		log.info("Thread {} tries to acquire lock :{}", Thread.currentThread().getName(), key);
		RLock lock = redisson.getLock(genKey(key));
		if (lock.tryLock(maxWait, waitUnit)) {
			log.info("Thread {} takes lock :{} succeeded", Thread.currentThread().getName(), key);
			try {
				Optional.ofNullable(call).ifPresent(XLockCallBackFunction::apply);
				return;
			} finally {
				log.info("Thread {} releases lock :{}", Thread.currentThread().getName(), key);
				lock.unlock();
			}
		}
		log.error("Thread {} failed to fetch lock :{}", Thread.currentThread().getName(), key);
		throw new XGetLockFailedException("Service busy (failed to get distributed lock \"" + key + "\"!)");
	}

	/**
	 * 读锁
	 *
	 * @param call     回调
	 * @param key      加锁的key
	 * @param maxWait  等待的时间
	 * @param waitUnit 时间单位
	 * @return
	 */
	@Override
	public void readLock(XLockCallBackFunction call, String key, long maxWait, TimeUnit waitUnit) throws Exception {
		log.info("Thread {} tries to acquire read lock :{}", Thread.currentThread().getName(), key);
		ReadWriteLock readWriteLock = redisson.getReadWriteLock(genKey(key));
		if (readWriteLock != null) {
			Lock lock = readWriteLock.readLock();
			if (lock.tryLock(maxWait, waitUnit)) {
				log.info("Thread {} takes read lock :{} succeeded", Thread.currentThread().getName(), key);
				try {
					Optional.ofNullable(call).ifPresent(XLockCallBackFunction::apply);
					return;
				} finally {
					log.info("Thread {} releases read lock :{}", Thread.currentThread().getName(), key);
					lock.unlock();
				}
			}
		}
		log.error("Thread {} failed to fetch read lock :{}", Thread.currentThread().getName(), key);
		throw new XGetLockFailedException("Service busy (failed to get distributed read lock \"" + key + "\"!)");
	}

	/**
	 * 写锁
	 *
	 * @param call     回调
	 * @param key      加锁的key
	 * @param maxWait  等待的时间
	 * @param waitUnit 时间单位
	 * @return
	 */
	@Override
	public void writeLock(XLockCallBackFunction call, String key, long maxWait, TimeUnit waitUnit) throws Exception {
		log.info("Thread {} tries to acquire write lock :{}", Thread.currentThread().getName(), key);
		ReadWriteLock readWriteLock = redisson.getReadWriteLock(genKey(key));
		if (readWriteLock != null) {
			Lock lock = readWriteLock.writeLock();
			if (lock.tryLock(maxWait, waitUnit)) {
				log.info("Thread {} takes write lock :{} succeeded", Thread.currentThread().getName(), key);
				try {
					Optional.ofNullable(call).ifPresent(XLockCallBackFunction::apply);
					return;
				} finally {
					log.info("Thread {} releases write lock :{}", Thread.currentThread().getName(), key);
					lock.unlock();
				}
			}
		}
		log.error("Thread {} failed to fetch write lock :{}", Thread.currentThread().getName(), key);
		throw new XGetLockFailedException("Service busy (failed to get distributed write lock \"" + key + "\"!)");
	}

	private String genKey(String key) {
		return KEY_PREFIX + key;
	}
}
