package com.book.modules.order.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.common.utils.NumberUtil;
import com.book.modules.book.dao.BookDao;
import com.book.modules.order.dao.OrderDao;
import com.book.modules.order.dao.OrderBookDao;
import com.book.modules.order.entity.OrderEntity;
import com.book.modules.order.entity.OrderBookEntity;
import com.book.modules.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private OrderBookDao orderBookDao;
	@Autowired
	private BookDao bookDao;
	
	@Override
	public OrderEntity queryObject(Integer id){
		OrderEntity order = orderDao.queryObject(id);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderId", order.getId());
		List<OrderBookEntity> orderBookList = orderBookDao.queryList(params);
		order.setOrderBookList(orderBookList);
		return order;
	}
	
	@Override
	public List<OrderEntity> queryList(Map<String, Object> map){
		List<OrderEntity> orderList = orderDao.queryList(map);
		for (OrderEntity orderEntity : orderList) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderId", orderEntity.getId());
			List<OrderBookEntity> orderBookList = orderBookDao.queryList(params);
			orderEntity.setOrderBookList(orderBookList);
		}
		return orderList;
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return orderDao.queryTotal(map);
	}
	
	@Override
	public void save(OrderEntity order){
		orderDao.save(order);
	}
	
	@Override
	public void update(OrderEntity order){
		orderDao.update(order);
	}
	
	@Override
	public void delete(Integer orderId){
		orderDao.delete(orderId);
	}
	
	@Override
	public void deleteBatch(Integer[] orderIds){
		orderDao.deleteBatch(orderIds);
	}

	@Override
	@Transactional
	public void createOrder(OrderEntity order) {
		order.setOrderNumber(NumberUtil.getOrderNumber());
		orderDao.save(order);
		List<OrderBookEntity> orderBookList = order.getOrderBookList();
		for (OrderBookEntity orderBook : orderBookList) {
			orderBook.setOrderId(order.getId());
			orderBookDao.save(orderBook);
			bookDao.delStock(orderBook.getBookId(), orderBook.getNum());
		}
	}

	@Override
	public void updateByOrderNumber(OrderEntity order) {
		orderDao.updateByOrderNumber(order);
	}

	@Override
	public List<Map<String, String>> queryOrderCount() {
		return orderDao.queryOrderCount();
	}

	@Override
	@Transactional
	public void returnBook(OrderEntity order) {
		orderDao.update(order);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", order.getId());
		List<OrderBookEntity> orderBookList = orderBookDao.queryList(map);
		for (OrderBookEntity orderBook : orderBookList) {
			bookDao.addStock(orderBook.getBookId(), orderBook.getNum());
		}
	}
	
}
