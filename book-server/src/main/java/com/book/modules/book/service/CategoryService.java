package com.book.modules.book.service;

import java.util.List;
import java.util.Map;

import com.book.modules.book.entity.CategoryEntity;

/**
 * 分类
 * 
 * @author Nicole
 * @email 974368524@qq.com
 * @date 2022-01-14 13:43:12
 */
public interface CategoryService {
	
	CategoryEntity queryObject(Integer id);
	
	List<CategoryEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(CategoryEntity category);
	
	void update(CategoryEntity category);
	
	void delete(Integer id);
	
	void deleteBatch(Integer[] ids);

}
