package com.example.myapp;

/**
 * Hello World 标准输出程序（功能点 F01）。
 *
 * 独立 JVM 程序入口：向标准输出打印精确字符串 "hello world" 后返回，
 * 进程以退出码 0 正常退出。不依赖 Spring 容器与任何第三方库。
 */
public class HelloWorld {

    /**
     * 程序入口。命令行参数不读取、不校验、不使用（设计规则 R04）。
     *
     * @param args 命令行参数（本程序不使用）
     */
    public static void main(String[] args) {
        System.out.println("hello world");
    }
}
