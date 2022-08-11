package com.book.modules.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.modules.book.dao.BookDao;
import com.book.modules.book.dao.BookPicDao;
import com.book.modules.book.dao.HistoryDao;
import com.book.modules.book.entity.BookEntity;
import com.book.modules.book.entity.BookPicEntity;
import com.book.modules.book.service.BookService;
import com.book.modules.member.dao.MemberDao;


@Service("bookService")
public class BookServiceImpl implements BookService {
	@Autowired
	private BookDao bookDao;
	@Autowired
	private BookPicDao bookPicDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private HistoryDao historyDao;
	
	@Override
	public BookEntity queryObject(Integer id){
		String[] picUrls = bookPicDao.queryByBookId(id);
		BookEntity book = bookDao.queryObject(id);
		book.setPicUrls(picUrls);
		return book;
	}
	
	@Override
	public List<BookEntity> queryList(Map<String, Object> map){
		List<BookEntity> bookList = bookDao.queryList(map);
		return bookList;
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return bookDao.queryTotal(map);
	}
	
	@Override
	public void save(BookEntity book){
		bookDao.save(book);
		if (book.getPicUrls() != null) {
			String[] picUrls = book.getPicUrls();
			for (String picUrl : picUrls) {
				BookPicEntity bookPic = new BookPicEntity();
				bookPic.setBookId(book.getId());
				bookPic.setPicUrl(picUrl);
				bookPicDao.save(bookPic);
			}
		}
	}
	
	@Override
	public void update(BookEntity book){
		if (book.getPicUrls() != null) {
			String[] picUrls = bookPicDao.queryByBookId(book.getId());
			boolean e = isQualsPic(picUrls, book.getPicUrls());
			if (!e) {
				bookPicDao.deleteByBookId(book.getId());
				for (String picUrl : book.getPicUrls()) {
					BookPicEntity bookPic = new BookPicEntity();
					bookPic.setBookId(book.getId());
					bookPic.setPicUrl(picUrl);
					bookPicDao.save(bookPic);
				}
			}
		}
		bookDao.update(book);
	}
	
	private boolean isQualsPic(String[] picUrls, String[] picUrls2) {
		if (picUrls.length == picUrls2.length) {
			for (int i = 0; i < picUrls.length; i++) {
				if (!picUrls[i].equals(picUrls2[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void delete(Integer id){
		bookDao.delete(id);
	}
	
	@Override
	public void deleteBatch(Integer[] ids){
		bookDao.deleteBatch(ids);
	}
	
	@Override
	public void updateStatus(Integer status, Integer[] ids) {
		bookDao.updateStatus(status, ids);
	}
	
	/**
	 * 可能喜欢
	 * */
	@Override
    public List<BookEntity> getLike(Long userId) {
		
        return new ArrayList<BookEntity>();
    }
}
