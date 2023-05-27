package com.jinyao.xdp.lock.exception;

/**
 * 获取锁失败的异常
 *
 * @author 谢进伟
 * @createDate 2023/5/4 16:29
 */
public class XGetLockFailedException extends RuntimeException {

	/**
	 * Constructs a new runtime exception with the specified detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for
	 *                later retrieval by the {@link #getMessage()} method.
	 */
	public XGetLockFailedException(String message) {
		super(message);
	}
}
