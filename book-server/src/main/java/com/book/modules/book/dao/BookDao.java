package com.book.modules.book.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.book.modules.book.entity.BookEntity;
import com.book.modules.sys.dao.BaseDao;

/**
 * 图书
 *
 * @author Nicole
 * @email 974368524@qq.com
 * @date 2021-01-15 18:23:05
 */
@Mapper
public interface BookDao extends BaseDao<BookEntity> {

	void updateStatus(@Param("status")Integer status, @Param("ids")Integer[] ids);
	
	void delStock(@Param("id")Integer id, @Param("stock")int stock);

	void addStock(@Param("id")Integer id, @Param("stock")int stock);

	List<BookEntity> queryByIds(Integer[] ids);
}
