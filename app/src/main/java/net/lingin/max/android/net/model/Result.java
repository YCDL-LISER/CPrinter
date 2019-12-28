package net.lingin.max.android.net.model;

/**
 * Created by: var_rain.
 * Created date: 2018/10/21.
 * Description: 统一返回对象
 */
public class Result<T> {

    /* 返回状态码 */
    private int code;

    /* 提示信息 */
    private String msg;

    /* 数据对象 */
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
