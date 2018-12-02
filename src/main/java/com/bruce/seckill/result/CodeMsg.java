package com.bruce.seckill.result;

import lombok.Data;

@Data
public class CodeMsg {
    private int code;
    private String message;

    //general issue
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "server issue");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "parameter verification error: %s");

    //login module  5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session not exist or expire");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "  password is empty");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "telephone number is empty");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "telephone number format is not expected");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "telephone number not exist");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "password not correct");

    //product module 5003XX

    //order module 5004XX

    //seckill module 5005XX
    public static CodeMsg SECKILL_OVER = new CodeMsg(500500, "seckill goods sell out");
    public static CodeMsg REPEATE_SECKILL = new CodeMsg(500501, "one seckill good per one");

    public CodeMsg fillArgs(Object...args) {
        int code = this.code;
        String message = String.format(this.message, args);
        return new CodeMsg(code, message);
    }

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
