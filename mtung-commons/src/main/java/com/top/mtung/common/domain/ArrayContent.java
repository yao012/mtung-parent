package com.top.mtung.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 列表数据
 *
 * @author zhenguo.yao
 */
@Data
public class ArrayContent<T> implements Serializable {

    private static final long serialVersionUID = -7262754790284748702L;

    private List<T> content;

    public ArrayContent(List<T> content){
        this.content = content;
    }

    public static <T> ArrayContent<T> arrayOf(List<T> data) {
        return new ArrayContent<>(data);
    }
}
