package com.top.mtung.common.domain;

import lombok.Data;

import java.util.List;

/**
 * @author zhenguo.yao
 */
@Data
public class PageContent<T> extends ArrayContent<T> {

    private static final long serialVersionUID = -944974623890915071L;

    /**
     * 总记录数
     */
    private long total;
    /**
     * 分页记录数
     */
    private int pageSize;

    /**
     * 当前页码
     */
    private int pageNo;

    public PageContent(List<T> content, int pageNo, int pageSize, long total) {
        super(content);
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
    }

    public static <T> PageContent<T> pageOf(List<T> content, int pageNo, int pageSize, long total) {
        return new PageContent<>(content, pageNo, pageSize, total);
    }

}
