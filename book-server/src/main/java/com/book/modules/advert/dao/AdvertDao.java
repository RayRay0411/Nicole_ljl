package com.book.modules.advert.dao;

import org.apache.ibatis.annotations.Mapper;

import com.book.modules.advert.entity.AdvertEntity;
import com.book.modules.sys.dao.BaseDao;

/**
 * @author Nicole
 * @email 974368524@qq.com
 */
@Mapper
public interface AdvertDao extends BaseDao<AdvertEntity> {
	
}
