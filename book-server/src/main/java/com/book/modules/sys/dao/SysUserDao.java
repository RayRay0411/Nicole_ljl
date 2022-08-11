package com.book.modules.sys.dao;


import com.book.modules.sys.entity.SysUserEntity;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户
 *
 * @author Nicole
 * @email 974368524@qq.com
 */
@Mapper
public interface SysUserDao extends BaseDao<SysUserEntity> {
	
	/**
	 * 根据用户名，查询系统用户
	 */
	SysUserEntity queryByUserName(String username);
	
	/**
	 * 修改密码
	 */
	int updatePassword(Map<String, Object> map);
}
