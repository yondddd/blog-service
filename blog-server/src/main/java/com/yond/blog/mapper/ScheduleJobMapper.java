package com.yond.blog.mapper;

import com.yond.blog.entity.ScheduleJobDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 定时任务持久层接口
 * @Author: Yond
 */
@Mapper
@Repository
public interface ScheduleJobMapper {
    
    int insertSelective(ScheduleJobDO job);
    
    int updateSelective(ScheduleJobDO job);
    
    Integer countBy();
    
    List<ScheduleJobDO> pageBy(Integer offset, Integer size);
    
}
