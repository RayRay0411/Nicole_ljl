package com.book.modules.book.dao;

import org.apache.ibatis.annotations.Mapper;

import com.book.modules.book.entity.BookPicEntity;
import com.book.modules.sys.dao.BaseDao;

/**
 * 图书图片
 *
 * @author Nicole
 * @email 974368524@qq.com
 * @date 2021-01-15 18:25:05
 */
@Mapper
public interface BookPicDao extends BaseDao<BookPicEntity> {

	String[] queryByBookId(Integer bookId);

	void deleteByBookId(Integer bookId);
	
}
