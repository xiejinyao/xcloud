package com.jinyao.xdp.lock.aspect;

import com.jinyao.xdp.lock.IXLock;
import com.jinyao.xdp.lock.annotations.XLock;
import com.jinyao.xdp.lock.exception.XGetLockFailedException;
import com.jinyao.xdp.lock.function.XLockCallBackFunction;
import com.jinyao.xdp.lock.function.XLockType;
import com.jinyao.xdp.lock.redisson.XRedissonLook;
import com.xjinyao.xcloud.common.core.spel.SPELUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.jinyao.xdp.lock.function.XLockType.*;

/**
 * Lock注解拦截，自动执行加锁处理
 *
 * @author 谢进伟
 * @createDate 2022/8/25 08:35
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class XLockAspect {

	private final XRedissonLook redisLook;

	@Around("@annotation(com.jinyao.xdp.lock.annotations.XLock)")
	public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Object[] args = joinPoint.getArgs();
		XLock annotation = method.getAnnotation(XLock.class);
		Object target = joinPoint.getTarget();
		String keyExpression = annotation.key();
		log.info("lock key expression is {}", keyExpression);
		String key = SPELUtil.parseExpression(target, method, args, keyExpression);
		log.info("lock key is {}", key);
		long maxWait = annotation.maxWait();
		log.info("lock max wait is {}", maxWait);
		TimeUnit timeUnit = annotation.waitUnit();
		log.info("lock time unit is {}", timeUnit);

		AtomicReference<Object> result = new AtomicReference<>();
		AtomicReference<Throwable> execThrowable = new AtomicReference<>();
		XLockCallBackFunction xLockCallBackFunction = proceed(joinPoint, result, execThrowable);

		log.info("Thread {} starts locking scheduling", Thread.currentThread().getName());
		try {
			switch (annotation.type()) {
				case REDIS_LOCK:
					checkLock(REDIS_LOCK, redisLook);
					redisLook.lock(xLockCallBackFunction, key, maxWait, timeUnit);
					break;
				case REDIS_READ_LOCK:
					checkLock(REDIS_READ_LOCK, redisLook);
					redisLook.readLock(xLockCallBackFunction, key, maxWait, timeUnit);
					break;
				case REDIS_WRITE_LOCK:
					checkLock(REDIS_WRITE_LOCK, redisLook);
					redisLook.writeLock(xLockCallBackFunction, key, maxWait, timeUnit);
					break;
				default:
					log.error("not support !");
			}
		} catch (Throwable e) {
			if (e instanceof XGetLockFailedException) {
				throw new RuntimeException("请求频繁，请稍后在再试！");
			}
			throw new RuntimeException(e);
		}
		Throwable throwable = execThrowable.get();
		if (throwable != null) {
			throw throwable;
		}
		return result.get();
	}

	private XLockCallBackFunction proceed(ProceedingJoinPoint joinPoint,
										  AtomicReference<Object> result,
										  AtomicReference<Throwable> execThrowable) {
		return () -> {
			try {
				result.set(joinPoint.proceed(joinPoint.getArgs()));
			} catch (Throwable e) {
				execThrowable.set(e);
			}
		};
	}

	private void checkLock(XLockType type, IXLock lock) {
		if (lock == null) {
			throw new RuntimeException("Distributed locks of type \"" + type + "\" are not currently supported!");
		}
	}
}
