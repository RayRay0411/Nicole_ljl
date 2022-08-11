package com.book.modules.book.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.common.annotation.AuthIgnore;
import com.book.common.utils.Query;
import com.book.common.utils.R;
import com.book.modules.book.entity.BookEntity;
import com.book.modules.book.service.BookService;

/**
 * 图书接口
 * @author Nicole
 * @email 974368524@qq.com
 * 类注解
 */
@RestController
@RequestMapping("/api/book")
public class ApiBookController {
	
	@Autowired
	private BookService bookService;
	
	/**
	 * 查询列表
	 * @param map
	 * @return
	 */
	@AuthIgnore
	@GetMapping("list")
    public R list(@RequestParam Map<String, Object> map){
		Query query = new Query(map);
    	List<BookEntity> bookList = bookService.queryList(query);
    	Integer total = bookService.queryTotal(map);
        return R.ok().put("bookList", bookList).put("total", total);
    }
    
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	@AuthIgnore
    @GetMapping("detail")
    public R detail(int id){
    	BookEntity book = bookService.queryObject(id);
    	return R.ok().put("book", book);
    }
	
	/**
	 * 推荐图书
	 * @param userId
	 * @return
	 */
	@GetMapping("recommend")
    public R recommend(@RequestAttribute("userId") Long userId){
    	List<BookEntity> bookList = bookService.getLike(userId);
    	if(bookList == null || bookList.size() == 0) {
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("status", 1);
    		Query query = new Query(map);
    		bookList = bookService.queryList(query);
    	}
        return R.ok().put("bookList", bookList);
    }
    
}
