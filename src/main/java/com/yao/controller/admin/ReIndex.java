package com.yao.controller.admin;

import com.sun.org.apache.xml.internal.utils.Hashtree2Node;
import com.yao.entity.Blog;
import com.yao.lucene.BlogIndex;
import com.yao.service.BlogService;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/lucene")
public class ReIndex {

    @Resource
    private BlogService blogService;

    @RequestMapping("/reIndex")
    public Map<String, Object> reIndex() throws Exception {
        Map<String, Object> map = new HashMap(1);

        Map<String, Object> mapPar = new HashMap<String, Object>(0);
        mapPar.put("luanxiede", 1);
        boolean flag = reIndex(mapPar);
        if(flag){
            map.put("success", true);
        }else{
            map.put("success", false);
        }
        return map;
    }

    public boolean reIndex(Map<String, Object> map) throws Exception {
        for(File f : new File(BlogIndex.getP()).listFiles()){
            if(!f.delete()){
                //删除失败了
                System.out.println(f.getName() + " 删除失败了, 即将返回false, 结束方法");
                return false;
            }
        }
        BlogIndex blogIndex = new BlogIndex();
        List<Blog> blogList = blogService.list(map);
        for(Blog blog : blogList){
            blogIndex.addIndex(fillBlog(blog));
        }
        return true;
    }

    public Blog fillBlog(Blog blog){
        String content = blog.getContent();
        Whitelist addTags = new Whitelist();
        String safe = Jsoup.clean(content, addTags);
        blog.setContentNoTag(safe);
        return blog;
    }

}
