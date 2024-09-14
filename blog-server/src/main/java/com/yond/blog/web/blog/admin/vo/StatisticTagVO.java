package com.yond.blog.web.blog.admin.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: WangJieLong
 * @Date: 2024-09-14
 */
public class StatisticTagVO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = -8679509936057378199L;
    
    /**
     * 标签名
     */
    private List<String> legend;
    /**
     * 标签名对应博客数量
     */
    private List<StatisticTagSeriesVO> series;
    
    public List<String> getLegend() {
        return legend;
    }
    
    public StatisticTagVO setLegend(List<String> legend) {
        this.legend = legend;
        return this;
    }
    
    public List<StatisticTagSeriesVO> getSeries() {
        return series;
    }
    
    public StatisticTagVO setSeries(List<StatisticTagSeriesVO> series) {
        this.series = series;
        return this;
    }
    
    @Override
    public String toString() {
        return "StatisticTagVO{" +
                "legend=" + legend +
                ", series=" + series +
                '}';
    }
}