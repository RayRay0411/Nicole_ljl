package com.book.modules.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.book.modules.order.entity.OrderEntity;
import com.book.modules.sys.dao.BaseDao;

/**
 * 借阅
 *
 */
@Mapper
public interface OrderDao extends BaseDao<OrderEntity> {

	void updateByOrderNumber(OrderEntity order);

	List<Map<String, String>> queryOrderCount();
	
}
