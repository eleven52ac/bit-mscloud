package com.bit.test.rpc.api.user.model;

import java.io.Serial;
import java.io.Serializable;


/**
 * 用户实体类
 * @Datetime: 2026年02月02日02:55
 * @Author: Eleven52AC
 */
public class User implements Serializable {

    /**
     * 序列化版本号
     */
    @Serial
    private static final long serialVersionUID = -1093604918512829670L;

    /**
     * 用户名
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
