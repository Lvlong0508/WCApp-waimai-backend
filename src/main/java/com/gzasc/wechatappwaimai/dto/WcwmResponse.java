package com.gzasc.wechatappwaimai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WcwmResponse<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public static <T> WcwmResponse<T> success(T data) {
        WcwmResponse<T> resp = new WcwmResponse<T>();
        resp.setCode(200);
        resp.setMsg("操作成功");
        resp.setData(data);
        return resp;
    }

    public static <T> WcwmResponse<T> success(String message, T data) {
        WcwmResponse<T> resp = new WcwmResponse<T>();
        resp.setCode(200);
        resp.setMsg(message);
        resp.setData(data);
        return resp;
    }

    public static <T> WcwmResponse<T> error(int code, String message) {
        WcwmResponse<T> resp = new WcwmResponse<T>();
        resp.setCode(code);
        resp.setMsg(message);
        return resp;
    }

    public static <T> WcwmResponse<T> error(String message) {
        WcwmResponse<T> resp = new WcwmResponse<T>();
        resp.setCode(500);
        resp.setMsg(message);
        return resp;
    }
}
