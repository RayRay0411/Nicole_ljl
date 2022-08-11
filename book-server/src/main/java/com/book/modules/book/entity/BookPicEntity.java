package com.book.modules.book.entity;

import java.io.Serializable;

/**
 * 图书图片
 *
 * @author Nicole
 * @email 974368524@qq.com
 */
public class BookPicEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	//
	private Integer id;
	// 图书ID
	private Integer bookId;
	// 图片
	private String picUrl;

	/**
	 * 设置：
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取：
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 设置：图书ID
	 */
	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	/**
	 * 获取：图书ID
	 */
	public Integer getBookId() {
		return bookId;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
}
