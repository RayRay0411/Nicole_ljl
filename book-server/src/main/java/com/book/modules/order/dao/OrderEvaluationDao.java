package com.book.modules.order.dao;

import com.book.modules.order.entity.OrderEvaluationEntity;
import com.book.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评价
 *
 */
@Mapper
public interface OrderEvaluationDao extends BaseDao<OrderEvaluationEntity> {
	
}
