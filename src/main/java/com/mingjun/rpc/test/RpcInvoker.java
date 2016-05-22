package com.mingjun.rpc.test;

import com.mingjun.rpc.core.RpcCoreHandler;

/**
 * Created by mingjun on 16/5/22.
 * 启动服务调用方
 * 调用方无需关注HelloService具体业务实现细节
 */
public class RpcInvoker {


    public static void main(String[] args) throws Exception {
        HelloService service = RpcCoreHandler.invoke(HelloService.class, "127.0.0.1", 8181);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String hello = service.sayHello("xiao_huang" + i);
            String goodBye = service.sayGoodBye("xiao_huang" + i);
            System.out.println(hello);
            System.out.println(goodBye);
            Thread.sleep(1000);
        }
    }

}
