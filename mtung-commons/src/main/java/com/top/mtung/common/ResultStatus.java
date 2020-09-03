package com.top.mtung.common;

/**
 * @author zhenguo.yao
 */
public interface ResultStatus {

    /**
     * 获取响应code
     *
     * @return
     */
    String getStatusCode();

    /**
     * 获取响应提示语
     *
     * @return
     */
    String getStatusMsg();


}
