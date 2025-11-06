package com.bit.cache.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 人员信息表
 * @TableName person_info
 */
@TableName(value ="person_info")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfoEntity implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 性别
     */
    private String gender;

    /**
     * 户籍
     */
    private String register;

    /**
     *
     */
    private String idCard;

    /**
     * 是否删除
     */
    private String isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}