package com.centit.shopping.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Comparator;


@Data
public class PlatformMonitorInfo implements Comparable<PlatformMonitorInfo> {
    /**
     * 用户数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer userNum;
    /**
     * 统计日期
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String createDate;

    /**
     * 百分比
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String percentage;

    /**
     * 版本号
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String version;
    /**
     * 版本安装数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer versionNum;

    /**
     * 品牌
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String brand;
    /**
     * 品牌数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer brandNum;

    @Override
    public int compareTo(PlatformMonitorInfo o1) {
        return this.getCreateDate().compareTo(o1.getCreateDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PlatformMonitorInfo that = (PlatformMonitorInfo) o;

        return new EqualsBuilder().append(createDate, that.createDate).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(createDate).toHashCode();
    }
}
