package com.mingjun.rpc.test;

import com.mingjun.rpc.core.RpcCoreHandler;

/**
 * Created by mingjun on 16/5/22.
 */
public class RpcInvoker {


    public static void main(String[] args) throws Exception {
        HelloService service = RpcCoreHandler.invoke(HelloService.class, "127.0.0.1", 8181);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String hello = service.sayHello("World" + i);
            System.out.println(hello);
            Thread.sleep(1000);
        }
    }

}
