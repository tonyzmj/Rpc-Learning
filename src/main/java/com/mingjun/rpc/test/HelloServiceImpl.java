package com.mingjun.rpc.test;

/**
 * Created by mingjun on 16/5/22.
 */
public class HelloServiceImpl implements HelloService {

    public String sayHello(String name) {
        return String.format("%s sayHello and threadName: %s", name, Thread.currentThread().getName());
    }

    public String sayGoodBye(String name) {
        return String.format("%s sayGoodBye and threadName: %s", name, Thread.currentThread().getName());
    }
}
