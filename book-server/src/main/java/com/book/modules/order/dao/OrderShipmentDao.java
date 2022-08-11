package com.book.modules.order.dao;

import org.apache.ibatis.annotations.Mapper;

import com.book.modules.order.entity.OrderShipmentEntity;
import com.book.modules.sys.dao.BaseDao;

/**
 */
@Mapper
public interface OrderShipmentDao extends BaseDao<OrderShipmentEntity> {

	OrderShipmentEntity queryByOrderId(Integer orderId);
	
}
