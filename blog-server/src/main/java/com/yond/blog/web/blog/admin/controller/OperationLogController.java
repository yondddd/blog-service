package com.yond.blog.web.blog.admin.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yond.blog.entity.OperationLogDO;
import com.yond.blog.service.OperationLogService;
import com.yond.common.resp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 操作日志后台管理
 * @Author: Naccl
 * @Date: 2020-12-01
 */
@RestController
@RequestMapping("/admin")
public class OperationLogController {
    @Autowired
    OperationLogService operationLogService;

    /**
     * 分页查询操作日志列表
     *
     * @param date     按操作时间查询
     * @param pageNum  页码
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/operationLogs")
    public Response operationLogs(@RequestParam(defaultValue = "") String[] date,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        String startDate = null;
        String endDate = null;
        if (date.length == 2) {
            startDate = date[0];
            endDate = date[1];
        }
        String orderBy = "create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<OperationLogDO> pageInfo = new PageInfo<>(operationLogService.getOperationLogListByDate(startDate, endDate));
        return Response.ok("请求成功", pageInfo);
    }

    /**
     * 按id删除操作日志
     *
     * @param id 日志id
     * @return
     */
    @DeleteMapping("/operationLog")
    public Response delete(@RequestParam Long id) {
        operationLogService.deleteOperationLogById(id);
        return Response.ok("删除成功");
    }
}
