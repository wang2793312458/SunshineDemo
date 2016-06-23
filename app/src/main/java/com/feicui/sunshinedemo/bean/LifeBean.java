package com.feicui.sunshinedemo.bean;

/**
 * Created by AAAAA on 2016/6/23.
 */
public class LifeBean {
    public String title;//生活指数类型
    public String exp;//指数
    public String content;//详细介绍

    public LifeBean(String exp, String content) {
        this.exp = exp;
        this.content = content;
    }
}
