package com.yond.service.impl;

import com.yond.common.exception.PersistenceException;
import com.yond.entity.OperationLog;
import com.yond.mapper.OperationLogMapper;
import com.yond.model.dto.UserAgentDTO;
import com.yond.service.OperationLogService;
import com.yond.util.IpAddressUtils;
import com.yond.util.UserAgentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 操作日志业务层实现
 * @Author: Naccl
 * @Date: 2020-11-30
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Autowired
    OperationLogMapper operationLogMapper;
    @Autowired
    UserAgentUtils userAgentUtils;

    @Override
    public List<OperationLog> getOperationLogListByDate(String startDate, String endDate) {
        return operationLogMapper.getOperationLogListByDate(startDate, endDate);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOperationLog(OperationLog log) {
        String ipSource = IpAddressUtils.getCityInfo(log.getIp());
        UserAgentDTO userAgentDTO = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
        log.setIpSource(ipSource);
        log.setOs(userAgentDTO.getOs());
        log.setBrowser(userAgentDTO.getBrowser());
        if (operationLogMapper.saveOperationLog(log) != 1) {
            throw new PersistenceException("日志添加失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteOperationLogById(Long id) {
        if (operationLogMapper.deleteOperationLogById(id) != 1) {
            throw new PersistenceException("删除日志失败");
        }
    }
}
