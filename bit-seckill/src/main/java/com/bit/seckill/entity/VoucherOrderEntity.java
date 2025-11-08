package com.bit.seckill.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * @TableName tb_voucher_order
 */
@TableName(value ="tb_voucher_order")
@Data
@Accessors(chain = true)
public class VoucherOrderEntity implements Serializable {

    private static final long serialVersionUID = -2115262199721669536L;
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 下单的用户id
     */
    private Long userId;

    /**
     * 购买的代金券id
     */
    private Long voucherId;

    /**
     * 支付方式 1：余额支付；2：支付宝；3：微信
     */
    private Integer payType;

    /**
     * 订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款
     */
    private Integer status;

    /**
     * 下单时间
     */
    private Date createTime;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 核销时间
     */
    private Date useTime;

    /**
     * 退款时间
     */
    private Date refundTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public static class Builder {
        private final VoucherOrderEntity entity = new VoucherOrderEntity();

        public Builder id(Long id){
            entity.setId(id);
            return this;
        }
        public Builder userId(Long userId){
            entity.setUserId(userId);
            return this;
        }
        public Builder voucherId(Long voucherId){
            entity.setVoucherId(voucherId);
            return this;
        }
        public Builder payType(Integer payType){
            entity.setPayType(payType);
            return this;
        }
        public Builder status(Integer status){
            entity.setStatus(status);
            return this;
        }
        public Builder createTime(Date createTime){
            entity.setCreateTime(createTime);
            return this;
        }
        public Builder payTime(Date payTime){
            entity.setPayTime(payTime);
            return this;
        }
        public Builder useTime(Date useTime){
            entity.setUseTime(useTime);
            return this;
        }
        public Builder refundTime(Date refundTime){
            entity.setRefundTime(refundTime);
            return this;
        }
        public Builder updateTime(Date updateTime){
            entity.setUpdateTime(updateTime);
            return this;
        }
        public VoucherOrderEntity build() {
            return entity;
        }
        public static Builder builder() {
            return new Builder();
        }

    }

}