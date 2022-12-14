package com.book.modules.book.service;

import java.util.List;
import java.util.Map;

import com.book.modules.book.entity.BookEntity;

/**
 * 图书
 * 
 * @author Nicole
 * @email 974368524@qq.com
 */
public interface BookService {
	
	BookEntity queryObject(Integer id);
	
	List<BookEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(BookEntity book);
	
	void update(BookEntity book);
	
	void delete(Integer id);
	
	void deleteBatch(Integer[] ids);
	
	void updateStatus(Integer status, Integer[] ids);
	
	List<BookEntity> getLike(Long userId);
}
