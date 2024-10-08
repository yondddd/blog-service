package com.wonder.blog.web.admin.controller;

import com.wonder.blog.entity.VisitUserDO;
import com.wonder.blog.service.VisitUserService;
import com.wonder.blog.web.admin.convert.VisitUserConverter;
import com.wonder.blog.web.admin.req.VisitUserDelReq;
import com.wonder.blog.web.admin.req.VisitUserPageReq;
import com.wonder.blog.web.admin.vo.VisitUserVO;
import com.wonder.common.annotation.OperationLogger;
import com.wonder.common.resp.PageResponse;
import com.wonder.common.resp.Response;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 访客统计
 * @Author: Yond
 */
@RestController
@RequestMapping("/admin/visit")
public class VisitorAdminController {

    @Resource
    private VisitUserService visitUserService;

    @PostMapping("/page")
    public PageResponse<List<VisitUserVO>> page(@RequestBody VisitUserPageReq req) {
        Pair<Integer, List<VisitUserDO>> pair = visitUserService.page(req.getPageNo(), req.getPageSize(), req.getStartDate(), req.getEndDate());
        List<VisitUserVO> data = pair.getRight().stream().map(VisitUserConverter::do2vo).toList();
        return PageResponse.<List<VisitUserVO>>custom().setData(data).setTotal(pair.getLeft()).setPageNo(req.getPageNo()).setPageSize(req.getPageSize()).setSuccess();
    }

    @OperationLogger("删除访客记录")
    @PostMapping("/del")
    public Response<Boolean> delete(@RequestBody VisitUserDelReq req) {
        visitUserService.deleteVisitor(req.getId(), req.getUuid());
        return Response.success();
    }

}
