package com.tansu.testcustomer.enumeration;


public enum Sex {
    MALE("MALE"),
    FEMININE("FEMININE");

    private final String sex;

    Sex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return this.sex;
    }
}
