package com.book.modules.order.dao;

import org.apache.ibatis.annotations.Mapper;

import com.book.modules.order.entity.OrderBookEntity;
import com.book.modules.sys.dao.BaseDao;

/**
 * 
 *
 */
@Mapper
public interface OrderBookDao extends BaseDao<OrderBookEntity> {
	
}
