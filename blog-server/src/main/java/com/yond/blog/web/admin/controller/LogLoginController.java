package com.yond.blog.web.admin.controller;

import com.yond.blog.entity.LogLoginDO;
import com.yond.blog.service.LogLoginService;
import com.yond.blog.web.admin.convert.LogLoginConverter;
import com.yond.blog.web.admin.req.LogLoginPageReq;
import com.yond.blog.web.admin.vo.LogLoginVO;
import com.yond.blog.web.admin.req.LogLoginDelReq;
import com.yond.common.enums.EnableStatusEnum;
import com.yond.common.resp.PageResponse;
import com.yond.common.resp.Response;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 登录日志后台管理
 * @Author: Yond
 */
@RestController
@RequestMapping("/admin/logLogin")
public class LogLoginController {

    @Resource
    private LogLoginService logLoginService;

    @PostMapping("/page")
    public PageResponse<List<LogLoginVO>> page(@RequestBody LogLoginPageReq req) {
        Pair<Integer, List<LogLoginDO>> pair = logLoginService.page(req.getStartDate(), req.getEndDate(), req.getPageNo(), req.getPageSize());
        List<LogLoginVO> data = pair.getRight().stream().map(LogLoginConverter::do2vo).toList();
        return PageResponse.<List<LogLoginVO>>custom().setData(data).setTotal(pair.getLeft()).setPageNo(req.getPageNo()).setPageSize(req.getPageSize()).setSuccess();
    }

    @PostMapping("/del")
    public Response<Boolean> del(@RequestBody LogLoginDelReq req) {
        LogLoginDO update = new LogLoginDO();
        update.setId(req.getId());
        update.setStatus(EnableStatusEnum.DELETE.getVal());
        logLoginService.updateSelective(update);
        return Response.success();
    }

}