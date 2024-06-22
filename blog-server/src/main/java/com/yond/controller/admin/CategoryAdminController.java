package com.yond.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yond.common.annotation.OperationLogger;
import com.yond.common.resp.Result;
import com.yond.entity.CategoryDO;
import com.yond.service.BlogService;
import com.yond.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 博客分类后台管理
 * @Author: Naccl
 * @Date: 2020-08-02
 */
@RestController
@RequestMapping("/admin")
public class CategoryAdminController {
    @Autowired
    BlogService blogService;
    @Autowired
    CategoryService categoryService;

    /**
     * 获取博客分类列表
     *
     * @param pageNum  页码
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/categories")
    public Result<PageInfo<CategoryDO>> categories(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "id desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<CategoryDO> pageInfo = new PageInfo<>(categoryService.listAll());
        return Result.success(pageInfo);
    }

    /**
     * 添加新分类
     *
     * @param category 分类实体
     * @return
     */
    @OperationLogger("添加分类")
    @PostMapping("/category")
    public Result saveCategory(@RequestBody CategoryDO category) {
        return getResult(category, "save");
    }

    /**
     * 修改分类名称
     *
     * @param category 分类实体
     * @return
     */
    @OperationLogger("修改分类")
    @PutMapping("/category")
    public Result updateCategory(@RequestBody CategoryDO category) {
        return getResult(category, "update");
    }

    /**
     * 执行分类添加或更新操作：校验参数是否合法，分类是否已存在
     *
     * @param category 分类实体
     * @param type     添加或更新
     * @return
     */
    private Result getResult(CategoryDO category, String type) {
        if (StringUtils.isBlank(category.getName())) {
            return Result.failure("分类名称不能为空");
        }
        //查询分类是否已存在
        CategoryDO category1 = categoryService.getByName(category.getName());
        //如果 category1.getId().equals(category.getId()) == true 就是更新分类
        if (category1 != null && !category1.getId().equals(category.getId())) {
            return Result.failure("该分类已存在");
        }
        if ("save".equals(type)) {
            categoryService.save(category);
            return Result.ok("分类添加成功");
        } else {
            categoryService.update(category);
            return Result.ok("分类更新成功");
        }
    }

    /**
     * 按id删除分类
     *
     * @param id 分类id
     * @return
     */
    @OperationLogger("删除分类")
    @DeleteMapping("/category")
    public Result<Boolean> delete(@RequestParam Long id) {
        //删除存在博客关联的分类后，该博客的查询会出异常
        int num = blogService.countBlogByCategoryId(id);
        if (num != 0) {
            return Result.<Boolean>custom().setFailure("已有博客与此分类关联，不可删除");
        }
        categoryService.deleteById(id);
        return Result.success();
    }

}
