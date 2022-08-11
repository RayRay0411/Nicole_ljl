package com.book.modules.member.service;

import java.util.List;
import java.util.Map;

import com.book.modules.member.entity.MemberAddressEntity;

/**
 * 用户地址
 *
 */
public interface MemberAddressService {
	
	MemberAddressEntity queryObject(Integer id);
	
	List<MemberAddressEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(MemberAddressEntity memberAddress);
	
	void update(MemberAddressEntity memberAddress);
	
	void delete(Integer id);
	
	void deleteBatch(Integer[] ids);

	MemberAddressEntity queryByDefault(Integer userId);

	void updateDft(MemberAddressEntity memberAddress);
}
