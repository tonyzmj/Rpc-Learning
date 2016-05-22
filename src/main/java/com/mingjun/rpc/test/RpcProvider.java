package com.mingjun.rpc.test;

import com.mingjun.rpc.core.RpcCoreHandler;

import java.io.IOException;

/**
 * Created by mingjun on 16/5/22.
 * 启动服务提供方
 */
public class RpcProvider {
    public static void main(String[] args) throws IOException {

        HelloService helloService = new HelloServiceImpl();
        RpcCoreHandler.publishService(helloService, 8181);

    }
}
