package com.jianbackup.utils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap<String,Object> {

    private static final Gson gson = new Gson();

    public R(){
        put("code",0);
        put("msg","操作成功");
    }

    public R(String code, String msg){
        put("code",code);
        put("msg",msg);
    }

    public static R error(String msg){
        return error(500,msg);
    }

    public static R error(){
        return error(1,"操作失败");
    }

    public static R error(int code,String  msg){
        R r = new R();
        r.put("code",code);
        r.put("msg",msg);
        return  r;
    }

    public static R ok(String msg){
        R r = new R();
        r.put("msg",msg);
        return r;
    }

    public static R ok(Map<String,Object> map){
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok(){
        return  new R();
    }

    public R put(String key,Object value){
        super.put(key,value);
        return  this;
    }

    public static String okJson(String msg){
        return gson.toJson(R.ok(msg));
    }

    public static String errorJson(String msg){
        return gson.toJson(R.error(msg));
    }


}
