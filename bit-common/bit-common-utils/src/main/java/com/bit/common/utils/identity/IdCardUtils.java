package com.bit.common.utils.identity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class IdCardUtils {
    public static final Map<String, String> DISTRICT = new HashMap<>();


    // 验证身份证有效性（不抛异常）
    public static boolean validate(String idNumber) {
        if (idNumber == null) return false;
        int len = idNumber.length();

        // 基础格式验证
        if (len != 15 && len != 18) return false;
        if (len == 15 && !idNumber.matches("\\d{15}")) return false;
        if (len == 18 && !idNumber.matches("\\d{17}[\\dX]")) return false;

        // 日期验证
        try {
            parseDate(idNumber);
        } catch (Exception e) {
            return false;
        }

        // 18位校验码验证
        if (len == 18 && !validateCheckCode(idNumber)) return false;

        return true;
    }

    // 获取年龄（可能返回null）
    public static Integer getAge(String idNumber) {
        if (!validate(idNumber)) return null;
        LocalDate birthday = parseDate(idNumber);
        LocalDate now = LocalDate.now();
        int age = now.getYear() - birthday.getYear();
        return (birthday.plusYears(age).isAfter(now)) ? age - 1 : age;
    }

    // 获取生日字符串（yyyy-MM-dd格式）
    public static String getBirthday(String idNumber) {
        if (!validate(idNumber)) return "未知";
        return parseDate(idNumber).format(DateTimeFormatter.ISO_DATE);
    }

    // 获取性别
    public static String getGender(String idNumber) {
        if (!validate(idNumber)) return "未知";
        int pos = idNumber.length() == 15 ? 14 : 16;
        return (Character.getNumericValue(idNumber.charAt(pos)) % 2 == 1) ? "男" : "女";
    }

    // 获取户籍地
    public static String getAddress(String idNumber) {
        if (!validate(idNumber)) return "未知";
        String code = idNumber.substring(0, 6);
        String PlaceOfDomicile = DISTRICT.getOrDefault(code, "未知");
        return PlaceOfDomicile;
    }

    /****************** 内部方法 ********************/
    private static LocalDate parseDate(String idNumber) {
        String dateStr = idNumber.length() == 15 ?
                "19" + idNumber.substring(6, 12) :
                idNumber.substring(6, 14);
        return LocalDate.parse(dateStr, DateTimeFormatter.BASIC_ISO_DATE);
    }

    private static boolean validateCheckCode(String idNumber) {
        if (idNumber.length() != 18) return false;
        int[] weights = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
        char[] codes = {'1','0','X','9','8','7','6','5','4','3','2'};

        int sum = 0;
        for (int i=0; i<17; i++) {
            sum += Character.getNumericValue(idNumber.charAt(i)) * weights[i];
        }
        return idNumber.charAt(17) == codes[sum % 11];
    }

}