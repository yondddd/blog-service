package com.wonder.blog.web.admin.controller;

import com.wonder.blog.entity.SiteConfigDO;
import com.wonder.blog.service.SiteConfigService;
import com.wonder.blog.web.admin.convert.SiteSettingConverter;
import com.wonder.blog.web.admin.req.SiteConfigTypeReq;
import com.wonder.blog.web.admin.vo.SiteSettingVO;
import com.wonder.common.annotation.OperationLogger;
import com.wonder.common.enums.SiteConfigTypeEnum;
import com.wonder.common.resp.Response;
import jakarta.annotation.Resource;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/siteConfig")
public class SiteConfigAdminController {

    @Resource
    private SiteConfigService siteConfigService;

    @PostMapping("/listAll")
    public Response<List<SiteSettingVO>> siteSettings() {
        List<SiteSettingVO> data = siteConfigService.listAll().stream()
                .map(SiteSettingConverter::do2vo).toList();
        return Response.success(data);
    }

    @PostMapping("/listByType")
    public Response<List<SiteSettingVO>> listByType(@RequestBody SiteConfigTypeReq req) {
        SiteConfigTypeEnum typeEnum = SiteConfigTypeEnum.getByVal(req.getType());
        Assert.notNull(typeEnum, "type not exist：" + req.getType());
        List<SiteSettingVO> data = siteConfigService.listByType(typeEnum).stream()
                .map(SiteSettingConverter::do2vo).toList();
        return Response.success(data);
    }

    @OperationLogger("更新站点配置信息")
    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody List<SiteSettingVO> req) {
        List<SiteConfigDO> list = req.stream().map(SiteSettingConverter::vo2do).toList();
        siteConfigService.coverUpdate(list);
        return Response.success();
    }

}
