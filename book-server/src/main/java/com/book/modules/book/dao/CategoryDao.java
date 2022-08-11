package com.book.modules.book.dao;

import org.apache.ibatis.annotations.Mapper;

import com.book.modules.book.entity.CategoryEntity;
import com.book.modules.sys.dao.BaseDao;

/**
 * 分类
 *
 * @author Nicole
 * @email 974368524@qq.com
 * @date 2021-01-15 18:25:18
 */
@Mapper
public interface CategoryDao extends BaseDao<CategoryEntity> {

	
}
