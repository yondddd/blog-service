package com.yond.service.impl;

import com.yond.common.exception.PersistenceException;
import com.yond.entity.VisitLog;
import com.yond.mapper.VisitLogMapper;
import com.yond.model.dto.UserAgentDTO;
import com.yond.model.dto.VisitLogUuidTime;
import com.yond.service.VisitLogService;
import com.yond.util.IpAddressUtils;
import com.yond.util.UserAgentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 访问日志业务层实现
 * @Author: Naccl
 * @Date: 2020-12-04
 */
@Service
public class VisitLogServiceImpl implements VisitLogService {
    @Autowired
    VisitLogMapper visitLogMapper;
    @Autowired
    UserAgentUtils userAgentUtils;

    @Override
    public List<VisitLog> getVisitLogListByUUIDAndDate(String uuid, String startDate, String endDate) {
        return visitLogMapper.getVisitLogListByUUIDAndDate(uuid, startDate, endDate);
    }

    @Override
    public List<VisitLogUuidTime> getUUIDAndCreateTimeByYesterday() {
        return visitLogMapper.getUUIDAndCreateTimeByYesterday();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveVisitLog(VisitLog log) {
        String ipSource = IpAddressUtils.getCityInfo(log.getIp());
        UserAgentDTO userAgentDTO = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
        log.setIpSource(ipSource);
        log.setOs(userAgentDTO.getOs());
        log.setBrowser(userAgentDTO.getBrowser());
        if (visitLogMapper.saveVisitLog(log) != 1) {
            throw new PersistenceException("日志添加失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteVisitLogById(Long id) {
        if (visitLogMapper.deleteVisitLogById(id) != 1) {
            throw new PersistenceException("删除日志失败");
        }
    }

    @Override
    public int countVisitLogByToday() {
        return visitLogMapper.countVisitLogByToday();
    }
}
