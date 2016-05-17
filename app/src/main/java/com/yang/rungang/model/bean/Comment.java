package com.yang.rungang.model.bean;

import cn.bmob.v3.BmobObject;

/**
 * 评论实体类
 *
 * Created by 洋 on 2016/5/10.
 */
public class Comment  extends BmobObject{

    private String dynamicId; //动态id

    private String fromUser; //评论者

    private String toUser; //被评论者

    private String content; //内容

    public String getDynamicId() {
        return dynamicId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public String getContent() {
        return content;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
