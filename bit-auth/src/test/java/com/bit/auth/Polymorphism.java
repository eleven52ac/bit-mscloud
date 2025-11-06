package com.bit.auth;

/**
 * @Datetime: 2025年05月28日15:56
 * @Author: Eleven也想AC
 * @Description:
 */
public class Polymorphism {



    public static void main(String[] args) {
        Person person = new Studente();
        person.eat(new Teacher());
    }
}

class Person {
    public void eat(Person person){
        System.out.println("人吃饭");
    }
}

class Studente extends Person {
    @Override
    public void eat(Person person) {
        System.out.println("学生吃饭");
    }
}

class Teacher extends Person {
    @Override
    public void eat(Person person) {
        System.out.println("老师吃饭");
    }
}