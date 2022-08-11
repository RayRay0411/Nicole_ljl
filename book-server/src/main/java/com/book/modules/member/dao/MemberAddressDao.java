package com.book.modules.member.dao;

import org.apache.ibatis.annotations.Mapper;

import com.book.modules.member.entity.MemberAddressEntity;
import com.book.modules.sys.dao.BaseDao;

/**
 * 用户地址
 *
 * @author Nicole
 * @email 974368524@qq.com
 */
@Mapper
public interface MemberAddressDao extends BaseDao<MemberAddressEntity> {

	void updateByUserId(MemberAddressEntity t);
	
}
