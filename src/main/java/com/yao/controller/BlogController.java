package com.yao.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.yao.entity.Blog;
import com.yao.lucene.BlogIndex;
import com.yao.service.BlogService;
import com.yao.service.CommentService;
import com.yao.util.StringUtil;

/**
 * ����Controller��
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/blog")
public class BlogController {

	@Resource
	private BlogService blogService;

	@Resource
	private CommentService commentService;

	private BlogIndex blogIndex = new BlogIndex();

	/**
	 * ���󲩿���ϸ��Ϣ
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/articles/{id}")
	public ModelAndView details(@PathVariable("id") Integer id,
			HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		Blog blog = blogService.findById(id);
		String keyWords = blog.getKeyWord();
		if (StringUtil.isNotEmpty(keyWords)) {
			String arr[] = keyWords.split(" ");
			mav.addObject("keyWords",
					StringUtil.filterWhite(Arrays.asList(arr)));
		} else {
			mav.addObject("keyWords", null);
		}
		mav.addObject("blog", blog);
		blog.setClickHit(blog.getClickHit() + 1);
		blogService.update(blog);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("blogId", blog.getId());
		map.put("state", 1);
		mav.addObject("commentList", commentService.list(map));
		mav.addObject("pageCode", this.getUpAndDownPageCode(
				blogService.getLastBlog(id), blogService.getNextBlog(id),
				request.getServletContext().getContextPath()));
		mav.addObject("pageTitle", blog.getTitle() + "java开源博客系统");
		mav.addObject("mainPage", "foreground/blog/view.jsp");
		mav.setViewName("mainTemp");
		return mav;
	}

	/**
	 * 获取上一篇博客和下一篇博客
	 *
	 * @param lastBlog
	 * @param nextBlog
	 * @param projectContext
	 * @return
	 */
	private String getUpAndDownPageCode(Blog lastBlog, Blog nextBlog,
										String projectContext) {
		StringBuffer pageCode = new StringBuffer();
		if (lastBlog == null || lastBlog.getId() == null) {
			pageCode.append("<p>上一篇：没有了</p>");
		} else {
			pageCode.append("<p>上一篇：<a href='" + projectContext
					+ "/blog/articles/" + lastBlog.getId() + ".html'>"
					+ lastBlog.getTitle() + "</a></p>");
		}

		if (nextBlog == null || nextBlog.getId() == null) {
			pageCode.append("<p>下一篇：没有了</p>");
		} else {
			pageCode.append("<p>下一篇：<a href='" + projectContext
					+ "/blog/articles/" + nextBlog.getId() + ".html'>"
					+ nextBlog.getTitle() + "</a></p>");
		}
		return pageCode.toString();
	}

	/**
	 * 根据关键字查询相关博客信息
	 *
	 * @param q
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/q")
	public ModelAndView search(
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "page", required = false) String page,
			HttpServletRequest request) throws Exception {
		int pageSize = 5;
		if (StringUtil.isEmpty(page)) {
			page = "1";
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("pageTitle", "搜索关键字'" + q + "'结果页面_java开源博客系统");
		mav.addObject("mainPage", "foreground/blog/result.jsp");
		List<Blog> blogList = blogIndex.searchBlog(q);
		Integer toIndex = blogList.size() >= Integer.parseInt(page) * pageSize ? Integer
				.parseInt(page) * pageSize
				: blogList.size();
		mav.addObject("blogList", blogList.subList((Integer.parseInt(page) - 1)
				* pageSize, toIndex));
		mav.addObject("pageCode", this.genUpAndDownPageCode(Integer
				.parseInt(page), blogList.size(), q, pageSize, request
				.getServletContext().getContextPath()));
		mav.addObject("q", q);
		mav.addObject("resultTotal", blogList.size());
		mav.setViewName("mainTemp");
		return mav;
	}

	/**
	 * 获取上一页，下一页代码
	 *
	 * @param page
	 * @param totalNum
	 * @param q
	 * @param pageSize
	 * @param projectContext
	 * @return
	 */
	private String genUpAndDownPageCode(Integer page, Integer totalNum,
										String q, Integer pageSize, String projectContext) {
		long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize
				: totalNum / pageSize + 1;
		StringBuffer pageCode = new StringBuffer();
		if (totalPage == 0) {
			return "";
		} else {
			pageCode.append("<nav>");
			pageCode.append("<ul class='pager'>");
			//拼接上一页;
			if (page > 1) {
				pageCode.append("<li><a href='" + projectContext
						+ "/blog/q.html?page=" + (page - 1) + "&q=" + q
						+ "'>上一页</a></li>");
			} else {
				pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
			}
			//拼接下一页;
			if (page < totalPage) {
				pageCode.append("<li><a href='" + projectContext
						+ "/blog/q.html?page=" + (page + 1) + "&q=" + q
						+ "'>下一页</a></li>");
			} else {
				pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
			}
			pageCode.append("</ul>");
			pageCode.append("</nav>");
		}
		return pageCode.toString();
	}
}
