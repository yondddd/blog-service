package com.wonder.blog.service.impl;

import com.wonder.blog.cache.local.FriendCache;
import com.wonder.blog.entity.FriendDO;
import com.wonder.blog.entity.SiteConfigDO;
import com.wonder.blog.mapper.FriendMapper;
import com.wonder.blog.service.FriendService;
import com.wonder.blog.service.SiteConfigService;
import com.wonder.blog.web.admin.dto.FriendConfigDTO;
import com.wonder.common.constant.SiteConfigConstant;
import com.wonder.common.enums.EnableStatusEnum;
import com.wonder.common.enums.SiteConfigTypeEnum;
import com.wonder.common.exception.PersistenceException;
import com.wonder.common.utils.page.PageUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Description: 友链业务层实现
 * @Author: Yond
 */
@Service
public class FriendServiceImpl implements FriendService {

    @Resource
    private FriendMapper friendMapper;
    @Resource
    private SiteConfigService siteConfigService;

    @Override
    public List<FriendDO> listAll() {
        List<FriendDO> cache = FriendCache.getAll();
        if (cache != null) {
            return cache;
        }
        cache = friendMapper.listAll().stream()
                .filter(x -> EnableStatusEnum.ENABLE.getVal().equals(x.getStatus())).toList();
        return cache;
    }

    @Override
    public Pair<Integer, List<FriendDO>> page(Integer pageNo, Integer pageSize) {
        List<FriendDO> all = new ArrayList<>(this.listAll());
        all.sort(Comparator.comparing(FriendDO::getId).reversed());
        return Pair.of(all.size(), PageUtil.pageList(all, pageNo, pageSize));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void incrViewById(Long id) {
        if (friendMapper.incrViewById(id) != 1) {
            throw new PersistenceException("操作失败");
        }
    }

    @Override
    public void insertSelective(FriendDO friendDO) {
        friendMapper.insertSelective(friendDO);
        FriendCache.delAll();
    }

    @Override
    public void updateSelective(FriendDO friendDO) {
        friendMapper.updateSelective(friendDO);
        FriendCache.delAll();
    }

    @Override
    public FriendConfigDTO getFriendConfig() {
        List<SiteConfigDO> siteSettings = siteConfigService.listAll()
                .stream().filter(x -> SiteConfigTypeEnum.FRIEND.getVal().equals(x.getType())).toList();
        FriendConfigDTO config = new FriendConfigDTO();
        for (SiteConfigDO siteSetting : siteSettings) {
            if (SiteConfigConstant.FRIEND_CONTENT.equals(siteSetting.getKey())) {
                config.setContent(siteSetting.getValue());
                config.setContent(siteSetting.getValue());
            } else if (SiteConfigConstant.FRIEND_COMMENT_ENABLED.equals(siteSetting.getKey())) {
                config.setCommentEnabled(BooleanUtils.toBoolean(siteSetting.getValue()));
            }
        }
        return config;
    }

}
