package com.xjinyao.xcloud.common.mybatis.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.xjinyao.xcloud.common.mybatis.service.IXService;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author 谢进伟
 * @createDate 2023/4/7 10:39
 */
public class XServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IXService<T> {


	/**
	 * 批处理更新
	 *
	 * @param list             列表
	 * @param updateWrapperFun 更新包装
	 * @return boolean
	 */
	protected <E, M> boolean updateBatch(Collection<E> list, Function<E, Wrapper<E>> updateWrapperFun) {
		return updateBatch(list, DEFAULT_BATCH_SIZE, updateWrapperFun);
	}

	/**
	 * 批处理更新
	 *
	 * @param list             列表
	 * @param batchSize        批量大小
	 * @param updateWrapperFun 更新包装
	 * @return boolean
	 */
	protected <E, M> boolean updateBatch(Collection<E> list, int batchSize, Function<E, Wrapper<E>> updateWrapperFun) {
		String sqlStatement = SqlHelper.getSqlStatement(mapperClass, SqlMethod.UPDATE);
		return updateBatch(list, batchSize, (sqlSession, entity) -> {
			Map<String, Object> param = new HashMap<>(2);
			param.put(Constants.ENTITY, entity);
			param.put(Constants.WRAPPER, updateWrapperFun.apply(entity));
			sqlSession.update(sqlStatement, param);
		});
	}

	/**
	 * 执行批量操作
	 *
	 * @param list      数据集合
	 * @param batchSize 批量大小
	 * @param consumer  执行方法
	 * @param <E>       泛型
	 * @return 操作结果
	 */
	private <E> boolean updateBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
		return SqlHelper.executeBatch(this.getEntityClass(), LogFactory.getLog(this.getClass()), list, batchSize,
				consumer);
	}
}
