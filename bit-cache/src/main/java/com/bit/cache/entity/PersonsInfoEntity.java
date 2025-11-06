package com.bit.cache.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 保障人员信息
 * @TableName prh_protected_persons_info
 */
@TableName(value ="prh_protected_persons_info")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonsInfoEntity implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long personId;

    /**
     * 家庭CODE
     */
    private String familyCode;

    /**
     * 中房数据库ID
     */
    private String oldId;

    /**
     * 人员编号
     */
    private String personCode;

    /**
     * 人员关系
     */
    private String personRelevancy;

    /**
     * 姓名
     */
    private String personName;

    /**
     * 性别
     */
    private String personSex;

    /**
     * 证件类别
     */
    private String cardType;

    /**
     * 证件号码
     */
    private String cardNumber;

    /**
     * 婚姻状况
     */
    private String maritalStatus;

    /**
     * 年可支配收入
     */
    private String yearRevenue;

    /**
     * 户籍类型
     */
    private String householdType;

    /**
     * 户籍号码
     */
    private String householdNumber;

    /**
     * 户口所在地
     */
    private String accountsAddress;

    /**
     * 户口入本市时间
     */
    private Date accountsIntime;

    /**
     * 现居住住址
     */
    private String liveAddress;

    /**
     * 联系电话
     */
    private String mobilePhone;

    /**
     * 联系地址
     */
    private String contactAddress;

    /**
     * 邮编
     */
    private String zip;

    /**
     * 状态：0无效；1有效
     */
    private Integer personStatus;

    /**
     * 开户行
     */
    private String bank;

    /**
     * 开户支行
     */
    private String bankBranch;

    /**
     * 银行卡卡号
     */
    private String bankCard;

    /**
     * 数据来源：1导入；2业务产生
     */
    private String dataSource;

    /**
     * 入库时间
     */
    private Date dataTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}