package com.wonder.blog.service.impl;

import com.wonder.blog.entity.VisitRecordDO;
import com.wonder.blog.mapper.VisitRecordMapper;
import com.wonder.blog.service.VisitRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 访问记录业务层实现
 * @Author: Yond
 */
@Service
public class VisitDayServiceImpl implements VisitRecordService {

    private final VisitRecordMapper visitRecordMapper;

    public VisitDayServiceImpl(VisitRecordMapper visitRecordMapper) {
        this.visitRecordMapper = visitRecordMapper;
    }

    @Override
    public List<VisitRecordDO> listByLimit(Integer limit) {
        return visitRecordMapper.listByLimit(limit);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insert(VisitRecordDO visitRecord) {
        visitRecordMapper.insert(visitRecord);
    }
}
