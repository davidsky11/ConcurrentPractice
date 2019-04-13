package com.kvlt.domain;

/**
 * ResultViewModel
 *
 * @author KVLT
 * @date 2019-04-13.
 */
public class ResultViewModel {

    public static final ResultViewModel EMPTY_RESULT = new ResultViewModel();

    private int code;
    private String message;
    private ResultModel data;

    public ResultViewModel() {}

    public ResultViewModel(int code, String message, ResultModel data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultModel getData() {
        return data;
    }

    public void setData(ResultModel data) {
        this.data = data;
    }
}
