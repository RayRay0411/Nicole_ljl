package com.book.modules.book.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.common.utils.Query;
import com.book.common.utils.R;
import com.book.modules.book.entity.BookEntity;
import com.book.modules.book.service.BookService;


/**
 * 图书
 *
 * @author Nicole
 * @email 974368524@qq.com
 * @date 2021-01-15 18:23:05
 */
@RestController
@RequestMapping("/book")
public class BookController {
	@Autowired
	private BookService bookService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<BookEntity> bookList = bookService.queryList(query);
		int total = bookService.queryTotal(query);
		
		return R.ok().put("rows", bookList).put("total", total);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Integer id){
		BookEntity book = bookService.queryObject(id);
		
		return R.ok().put("book", book);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody BookEntity book){
		book.setCreateTime(new Date());
		bookService.save(book);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody BookEntity book){
		bookService.update(book);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Integer[] ids){
		bookService.deleteBatch(ids);
		
		return R.ok();
	}
	
    @RequestMapping("/lower")
    public R lower(@RequestBody Integer[] ids) {
    	bookService.updateStatus(0, ids);
    	return R.ok();
    }
    
    @RequestMapping("/upper")
    public R upper(@RequestBody Integer[] ids) {
    	bookService.updateStatus(1, ids);
    	return R.ok();
    }
	
	@RequestMapping("/getAll")
	public R getAll() {
		List<BookEntity> bookList = bookService.queryList(null);
		return R.ok().put("bookList", bookList);
	}
	
}
