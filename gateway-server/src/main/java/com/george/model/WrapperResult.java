package com.george.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author George
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrapperResult<T> {

    private T data;
    private String msg;
    private Integer status;


    private transient static final String SUCCESS_MSG = "OK";

    private transient static final String FAILD_MSG = "FAIL";

    private transient static final int SUCCESS_STATUS = 200;

    private transient static final int FAIL_STATUS = 500;

    public transient static final int TOKEN_EXPIRE = 601;

    public static <T> WrapperResult<T> success(T data) {
        return WrapperResult.<T>builder().data(data).msg(SUCCESS_MSG).status(SUCCESS_STATUS).build();
    }

    public static <T> WrapperResult<T> fail(String msg) {
        return WrapperResult.<T>builder().data(null).msg(msg).status(FAIL_STATUS).build();
    }

    public static <T> WrapperResult<T> unAuthed(String msg,int status) {
        return WrapperResult.<T>builder().data(null).msg(msg).status(status).build();
    }



    /**
     * 判定是否是成功的方法
     * @return
     */
    public boolean isSuccess() {
        return this.status == SUCCESS_STATUS;
    }


}
