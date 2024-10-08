package com.wonder.blog.web.admin.controller;

import com.wonder.blog.entity.BlogDO;
import com.wonder.blog.entity.CategoryDO;
import com.wonder.blog.service.BlogService;
import com.wonder.blog.service.CategoryService;
import com.wonder.blog.web.admin.convert.CategoryConverter;
import com.wonder.blog.web.admin.req.CategoryDelReq;
import com.wonder.blog.web.admin.req.CategoryPageReq;
import com.wonder.blog.web.admin.vo.CategoryVO;
import com.wonder.common.annotation.OperationLogger;
import com.wonder.common.resp.PageResponse;
import com.wonder.common.resp.Response;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 博客分类后台管理
 * @Author: Yond
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminController {
    
    @Resource
    private BlogService blogService;
    @Resource
    private CategoryService categoryService;
    
    @PostMapping("/listAll")
    public Response<List<CategoryVO>> listAll() {
        List<CategoryDO> list = categoryService.listAll();
        List<CategoryVO> data = list.stream().map(CategoryConverter::do2vo).collect(Collectors.toList());
        return Response.success(data);
    }
    
    @PostMapping("/page")
    public PageResponse<List<CategoryVO>> page(@RequestBody CategoryPageReq req) {
        Pair<Integer, List<CategoryDO>> pair = categoryService.page(req.getPageNo(), req.getPageSize());
        List<CategoryVO> data = pair.getRight().stream().map(CategoryConverter::do2vo).toList();
        return PageResponse.<List<CategoryVO>>custom().setSuccess().setData(data).setTotal(pair.getLeft());
    }
    
    @OperationLogger("新增分类")
    @PostMapping("/save")
    public Response<Boolean> save(@RequestBody CategoryVO category) {
        CategoryDO exist = categoryService.getByName(category.getName());
        Assert.isNull(exist, "分类名称已存在");
        categoryService.insertSelective(CategoryConverter.vo2do(category));
        return Response.success();
    }
    
    @OperationLogger("更新分类")
    @PostMapping("/update")
    public Response<Boolean> updateCategory(@RequestBody CategoryVO category) {
        CategoryDO exist = categoryService.getByName(category.getName());
        if (exist != null) {
            Assert.isTrue(category.getId().equals(exist.getId()), "分类名称已存在");
        }
        categoryService.updateSelective(CategoryConverter.vo2do(category));
        return Response.success();
    }
    
    @OperationLogger("删除分类")
    @PostMapping("/del")
    public Response<Boolean> del(@RequestBody CategoryDelReq req) {
        Long id = req.getId();
        Pair<Integer, List<BlogDO>> pair = blogService.adminPageBy(null, req.getId(), null, 1, 1);
        if (pair.getLeft() != 0) {
            return Response.<Boolean>custom().setFailure("已有博客与此分类关联，不可删除");
        }
        categoryService.deleteById(id);
        return Response.success();
    }
    
}
