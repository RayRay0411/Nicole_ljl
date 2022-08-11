package com.book.modules.book.dao;

import com.book.modules.book.entity.HistoryEntity;
import com.book.modules.sys.dao.BaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 浏览记录
 *
 * @author Nicole
 * @email 974368524@qq.com
 * @date 2021-01-15 18:33:05
 */
@Mapper
public interface HistoryDao extends BaseDao<HistoryEntity> {

	List<HistoryEntity> queryByUserId(Long userId);

	List<HistoryEntity> queryByBookId(Integer bookId);
	
}
