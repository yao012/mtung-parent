package com.top.mtung.common.exception;

import com.top.mtung.common.ResultStatus;
import lombok.Data;

/**
 * @author zhenguo.yao
 */
@Data
public class BaseException extends RuntimeException {

    private ResultStatus resultStatus;
    private String code;
    private String msg;


    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Exception e) {
        super(message, e);
    }

    public BaseException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BaseException(ResultStatus resultStatus) {
        super(resultStatus.getStatusMsg());
        this.resultStatus = resultStatus;
    }

    public BaseException(ResultStatus resultStatus, Exception e) {
        super(resultStatus.getStatusMsg(), e);
        this.resultStatus = resultStatus;
    }

}
