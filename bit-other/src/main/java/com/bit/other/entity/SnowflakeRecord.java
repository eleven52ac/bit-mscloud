package com.bit.other.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Snowflake ID 生成日志表
 * @TableName snowflake_record
 */
@TableName(value ="snowflake_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnowflakeRecord implements Serializable {
    /**
     * 雪花算法生成的唯一ID
     */
    @TableId
    private Long id;

    /**
     * ID 的二进制表示（64位）
     */
    private String idBinary;

    /**
     * 时间戳部分（毫秒）
     */
    private Long timestampMs;

    /**
     * 解析后的时间（精确到毫秒）
     */
    private Date timestampDatetime;

    /**
     * 数据中心 ID（5位）
     */
    private Integer datacenterId;

    /**
     * 工作节点 ID（5位）
     */
    private Integer workerId;

    /**
     * 序列号（12位）
     */
    private Integer sequence;

    /**
     * 原始字段位值，例如：{ "sign": "0", "timestamp": "...", "datacenter": "...", ... }
     */
    private Object rawBits;

    /**
     * 记录插入时间
     */
    private Date createdAt;

    public static class Builder {
        private SnowflakeRecord snowflakeRecord;

        public Builder() {
            snowflakeRecord = new SnowflakeRecord();
        }

        public Builder id(Long id) {
            snowflakeRecord.setId(id);
            return this;
        }

        public Builder idBinary(String idBinary) {
            snowflakeRecord.setIdBinary(idBinary);
            return this;
        }

        public Builder timestampMs(Long timestampMs) {
            snowflakeRecord.setTimestampMs(timestampMs);
            return this;
        }

        public Builder timestampDatetime(Date timestampDatetime) {
            snowflakeRecord.setTimestampDatetime(timestampDatetime);
            return this;
        }

        public Builder datacenterId(Integer datacenterId) {
            snowflakeRecord.setDatacenterId(datacenterId);
            return this;
        }

        public Builder workerId(Integer workerId) {
            snowflakeRecord.setWorkerId(workerId);
            return this;
        }

        public Builder sequence(Integer sequence) {
            snowflakeRecord.setSequence(sequence);
            return this;
        }

        public Builder rawBits(Object rawBits) {
            snowflakeRecord.setRawBits(rawBits);
            return this;
        }

        public Builder createdAt(Date createdAt) {
            snowflakeRecord.setCreatedAt(createdAt);
            return this;
        }

        public SnowflakeRecord build() {
            return snowflakeRecord;
        }

        public Builder timestampDatetime(String timestamp) {
            snowflakeRecord.setTimestampDatetime(new Date(Long.parseLong(timestamp)));
            return this;
        }
    }

    private static final long serialVersionUID = 8759810076785482039L;

}