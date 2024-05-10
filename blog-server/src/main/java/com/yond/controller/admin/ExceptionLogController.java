package com.yond.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yond.common.resp.Result;
import com.yond.entity.ExceptionLog;
import com.yond.service.ExceptionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 异常日志后台管理
 * @Author: Naccl
 * @Date: 2020-12-04
 */
@RestController
@RequestMapping("/admin")
public class ExceptionLogController {
    @Autowired
    ExceptionLogService exceptionLogService;

    /**
     * 分页查询异常日志列表
     *
     * @param date     按操作时间查询
     * @param pageNum  页码
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/exceptionLogs")
    public Result exceptionLogs(@RequestParam(defaultValue = "") String[] date,
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
        PageInfo<ExceptionLog> pageInfo = new PageInfo<>(exceptionLogService.getExceptionLogListByDate(startDate, endDate));
        return Result.ok("请求成功", pageInfo);
    }

    /**
     * 按id删除异常日志
     *
     * @param id 日志id
     * @return
     */
    @DeleteMapping("/exceptionLog")
    public Result delete(@RequestParam Long id) {
        exceptionLogService.deleteExceptionLogById(id);
        return Result.ok("删除成功");
    }
}
