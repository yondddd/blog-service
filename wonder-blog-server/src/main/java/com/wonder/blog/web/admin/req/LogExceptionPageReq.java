package com.wonder.blog.web.admin.req;

import com.wonder.common.req.PageReq;

import java.io.Serial;
import java.util.Date;

/**
 * @Author: Yond
 */
public class LogExceptionPageReq extends PageReq {
    
    @Serial
    private static final long serialVersionUID = 6617373261376220831L;
    
    private Date startDate;
    private Date endDate;
    
    public Date getStartDate() {
        return startDate;
    }
    
    public LogExceptionPageReq setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public LogExceptionPageReq setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }
    
    @Override
    public String toString() {
        return "LogExceptionPageReq{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
