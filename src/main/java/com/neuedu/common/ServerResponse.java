package com.neuedu.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//响应前端的高复用对象
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//NON_NULL排除null值
public class ServerResponse<T> {
    private Integer status;//状态码0：成功
    private T date;//当status=0时。date对应接口响应的数据
    private String  msg;//提示信息
    private ServerResponse(){

    }
    private ServerResponse(Integer status){
    this.status = status;
    }
    private ServerResponse(Integer status,String msg){
    this.status = status;
    this.msg = msg;
    }
    private  ServerResponse(Integer status,String msg,T date){
        this.status = status;
        this.msg = msg;
        this.date = date;
    }
    //判断接口是否访问成功
    @JsonIgnore
    //@JsonIgnore忽略空值
    public boolean isSucess(){
        return this.status==ResponseCode.SUCCESS;
    }
    //{"status":0}
    public static ServerResponse createServerResponseBySuccess(){
        return new ServerResponse(ResponseCode.SUCCESS);
    }
    //{"status":0
    // "msg":"aaa"}
    public static ServerResponse createServerResponseBySuccess(String msg){
        return new ServerResponse(ResponseCode.SUCCESS,msg);
    }
    //{"status":0
    // "msg":"aaa"
    // "date":{}
    // }
    public static <T> ServerResponse createServerResponseBySuccess(String msg,T date){
        return new ServerResponse(ResponseCode.SUCCESS,msg,date);
    }
    //{"status":1}
    public static ServerResponse createServerResponseByError(){
        return new ServerResponse(ResponseCode.ERROR);
    }
    //{"status":custcm}
    public static ServerResponse createServerResponseByError(Integer status){
        return new ServerResponse(status);
    }
    //{"status":1
    // "msg","aaa"}
    public static ServerResponse createServerResponseByError(String msg){
        return new ServerResponse(ResponseCode.ERROR,msg);
    }
    //{"status":custcm
    // "msg","aaa"}
    public static ServerResponse createServerResponseByError(Integer status,String msg){
        return new ServerResponse(status,msg);
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
