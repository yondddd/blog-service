package com.wonder.blog.web.admin.convert;

import com.wonder.blog.entity.SiteConfigDO;
import com.wonder.blog.web.admin.vo.SiteSettingVO;

/**
 * @Description:
 * @Author: yond
 */
public class SiteSettingConverter {

    public static SiteSettingVO do2vo(SiteConfigDO from) {
        SiteSettingVO to = new SiteSettingVO();
        to.setKey(from.getKey());
        to.setName(from.getName());
        to.setValue(from.getValue());
        to.setType(from.getType());
        return to;
    }

    public static SiteConfigDO vo2do(SiteSettingVO from) {
        SiteConfigDO to = new SiteConfigDO();
        to.setKey(from.getKey());
        to.setName(from.getName());
        to.setValue(from.getValue());
        to.setType(from.getType());
        return to;
    }


}
