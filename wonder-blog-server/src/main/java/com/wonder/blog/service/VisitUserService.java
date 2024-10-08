package com.wonder.blog.service;

import com.wonder.blog.entity.VisitUserDO;
import com.wonder.blog.web.view.dto.VisitLogUuidTime;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;
import java.util.List;

public interface VisitUserService {
    
    Pair<Integer, List<VisitUserDO>> page(Integer pageNo, Integer pageSize, Date startDate, Date endDate);
    
    List<String> getNewVisitorIpSourceByYesterday();
    
    boolean hasUUID(String uuid);
    
    @Async
    void saveVisitor(VisitUserDO visitor);
    
    void updatePVAndLastTimeByUUID(VisitLogUuidTime dto);
    
    void deleteVisitor(Long id, String uuid);
    
}
