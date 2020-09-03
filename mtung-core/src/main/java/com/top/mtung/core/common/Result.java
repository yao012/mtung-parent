package com.top.mtung.core.common;

import com.top.mtung.common.ResultStatus;
import com.top.mtung.common.domain.ArrayContent;
import com.top.mtung.common.domain.PageContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhenguo.yao
 */
@Data
@AllArgsConstructor
@ApiModel("响应参数")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -7003555692535226981L;

    private static final String SUCCESS = "SUCCESS";

    private static final String HTTP_STATUS_CODE_500 = "500";

    public static final Result<Object> OK = success(null);
    public static final Result<Object> FAIL = error("操作失败");

    @ApiModelProperty("状态代码")
    private String code;

    @ApiModelProperty("状态描述")
    private String msg;

    @ApiModelProperty("数据")
    private T data;


    /* ------------ 成功的响应体 ----------- */

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS, "成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(SUCCESS, msg, data);
    }

    /* ------------ 失败的响应体 ----------- */

    public static Result<Object> error(ResultStatus statusResult, String msg) {
        return error(statusResult.getStatusCode(), StringUtils.defaultIfBlank(msg, statusResult.getStatusMsg()));
    }

    public static Result<Object> error(String msg) {
        return error(HTTP_STATUS_CODE_500, msg);
    }

    public static Result<Object> error(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    /* ------------ 分页数据的响应体 ----------- */

    public static <T> Result<PageContent<T>> page(Page<T> page) {
        return success(PageContent.pageOf(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements()));
    }

    /* ------------ 全量列表的响应体 ----------- */

    public static <T> Result<ArrayContent<T>> array(List<T> data) {
        return success(ArrayContent.arrayOf(data));
    }

}
