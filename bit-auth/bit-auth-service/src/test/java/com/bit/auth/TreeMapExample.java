package com.bit.auth;

import java.util.Map;
import java.util.TreeMap;

class Student {
    private String name;
    private int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Student{" +
               "name='" + name + '\'' +
               ", score=" + score +
               '}';
    }
}

public class TreeMapExample {
    public static void main(String[] args) {
        // 创建一个 TreeMap，键是 String (学生姓名)，值是 Integer (学生成绩)
        TreeMap<String, Integer> studentScores = new TreeMap<>();

        // 添加学生成绩
        studentScores.put("李明", 95);
        studentScores.put("王芳", 88);
        studentScores.put("张伟", 92);
        studentScores.put("赵丽", 90);

        System.out.println("按姓名自然顺序排序的学生成绩：");
        for (Map.Entry<String, Integer> entry : studentScores.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\n--------------------\n");

        // 创建一个 TreeMap，键是 Student 对象，使用自定义的 Comparator 按姓名排序
        TreeMap<Student, Integer> studentScoresWithObjectKey = new TreeMap<>(
            (s1, s2) -> s1.getName().compareTo(s2.getName())
        );

        // 添加学生成绩
        studentScoresWithObjectKey.put(new Student("李明", 95), 95);
        studentScoresWithObjectKey.put(new Student("王芳", 88), 88);
        studentScoresWithObjectKey.put(new Student("张伟", 92), 92);
        studentScoresWithObjectKey.put(new Student("赵丽", 90), 90);

        System.out.println("按学生姓名自定义排序的学生成绩 (使用 Student 对象作为键)：");
        for (Map.Entry<Student, Integer> entry : studentScoresWithObjectKey.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }
}