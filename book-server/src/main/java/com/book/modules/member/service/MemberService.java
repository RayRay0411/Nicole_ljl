package com.book.modules.member.service;

import java.util.List;
import java.util.Map;

import com.book.modules.member.entity.MemberEntity;

/**
 * 用户
 *
 */
public interface MemberService {
	
	MemberEntity queryObject(Long id);
	
	List<MemberEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(MemberEntity member);
	
	void update(MemberEntity member);
	
	void delete(Long id);
	
	void deleteBatch(Integer[] ids);

	MemberEntity queryByOpenid(String openid);

	MemberEntity queryByLoginName(String loginName);
}
