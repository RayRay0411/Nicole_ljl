package com.book.modules.order.service;

import java.util.List;
import java.util.Map;

import com.book.modules.order.entity.OrderEvaluationEntity;

/**
 * 评价
 *
 */
public interface OrderEvaluationService {
	
	OrderEvaluationEntity queryObject(Integer id);
	
	List<OrderEvaluationEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(OrderEvaluationEntity orderEvaluation);
	
	void update(OrderEvaluationEntity orderEvaluation);
	
	void delete(Integer id);
	
	void deleteBatch(Integer[] ids);
}
