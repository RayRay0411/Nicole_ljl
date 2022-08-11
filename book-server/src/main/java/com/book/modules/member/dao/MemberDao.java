package com.book.modules.member.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.book.modules.member.entity.MemberEntity;
import com.book.modules.sys.dao.BaseDao;


/**
 * 会员
 *
 * @author Nicole
 * @email 974368524@qq.com
 */
@Mapper
public interface MemberDao extends BaseDao<MemberEntity> {

	MemberEntity queryByOpenid(String openid);

	MemberEntity queryByLoginName(String loginName);
	
	void addDeposit(@Param("id")Long id, @Param("deposit")BigDecimal deposit);
}
