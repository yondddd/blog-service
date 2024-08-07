package com.yond.blog.web.blog.admin.req;

import com.yond.common.req.PageReq;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yond
 * @date 7/13/2024
 * @description
 */
public class VisitorPageReq extends PageReq implements Serializable {

    @Serial
    private static final long serialVersionUID = -1965632279964606709L;

    private Date start;
    private Date end;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "VisitorPageReq{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
