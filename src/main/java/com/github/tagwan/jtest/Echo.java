package com.github.tagwan.jtest;

public class Echo {

    public void print() {
        System.out.println("-->");
    }

    public static void main(String[] args) {
        new Echo().print();
    }
}
