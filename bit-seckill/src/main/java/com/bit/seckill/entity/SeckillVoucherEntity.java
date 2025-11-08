package com.bit.seckill.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 秒杀优惠券表，与优惠券是一对一关系
 * @TableName tb_seckill_voucher
 */
@TableName(value ="tb_seckill_voucher")
@Data
@Accessors(chain = true)
public class SeckillVoucherEntity implements Serializable {

    private static final long serialVersionUID = 1816932457293264354L;

    /**
     * 关联的优惠券的id
     */
    @TableId
    private Long voucherId;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 生效时间
     */
    private Date beginTime;

    /**
     * 失效时间
     */
    private Date endTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public static class Builder {
        private final SeckillVoucherEntity entity = new SeckillVoucherEntity();

        public Builder voucherId(Long voucherId) {
            entity.voucherId = voucherId;
            return this;
        }

         public Builder stock(Integer stock) {
             entity.stock = stock;
            return this;
        }

         public Builder createTime(Date createTime) {
             entity.createTime = createTime;
            return this;
        }

         public Builder beginTime(Date beginTime) {
             entity.beginTime = beginTime;
            return this;
        }

         public Builder endTime(Date endTime) {
             entity.endTime = endTime;
            return this;
        }

         public Builder updateTime(Date updateTime) {
             entity.updateTime = updateTime;
            return this;
        }

         public SeckillVoucherEntity build() {
            return entity;
        }

    }

}