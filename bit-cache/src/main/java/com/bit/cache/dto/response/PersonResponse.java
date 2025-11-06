package com.bit.cache.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Datetime: 2025年10月24日09:28
 * @Author: Eleven52AC
 * @Description: 响应类
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse implements Serializable {

    private static final long serialVersionUID = 4992322823092862519L;

    /**
     * 主键
     */
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

    public static class Builder {

        private PersonResponse response = new PersonResponse();

        public Builder id(Long id) {
            response.id = id;
            return this;
        }

        public Builder name(String name) {
            response.name = name;
            return this;
        }

        public Builder age(Integer age) {
            response.age = age;
            return this;
        }

        public Builder birthday(String birthday) {
            response.birthday = birthday;
            return this;
        }

        public Builder gender(String gender) {
            response.gender = gender;
            return this;
        }

        public Builder register(String register) {
            response.register = register;
            return this;
        }

        public PersonResponse build() {
            return response;
        }

    }
}
