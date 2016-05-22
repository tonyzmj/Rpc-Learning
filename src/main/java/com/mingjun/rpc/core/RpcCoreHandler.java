package com.mingjun.rpc.core;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mingjun on 16/5/21.
 */
public class RpcCoreHandler {

    private static ExecutorService threadPool;

    /**
     * 初始化线程池
     */
    static {
        threadPool = Executors.newFixedThreadPool(3);
    }

    public static void publishService(final Object serviceObj, int port) throws IOException {
        if (serviceObj == null) {
            new IllegalArgumentException("serviceObj is null");
        }
        if (port <= 0 || port > 65535) {
            new IllegalArgumentException("port is invalid");
        }
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("publishService : " + serviceObj.getClass().getName() + " on port " + port);
        while (true) {
            final Socket socket = serverSocket.accept();
            threadPool.submit(new Task(socket, serviceObj));
        }
    }

    /**
     * 主要通过动态代理
     *
     * @param interfaceClass
     * @param host
     * @param port
     * @param <T>
     * @return
     */
    public static <T> T invoke(final Class<T> interfaceClass, final String host, final int port) {
        if (interfaceClass == null) {
            new IllegalArgumentException("interfaceClass is null");
        }
        if (!interfaceClass.isInterface()) {
            new IllegalArgumentException("interfaceClass is not interface");
        }
        if (StringUtils.isBlank(host)) {
            new IllegalArgumentException("host is empty");
        }
        if (port <= 0 || port > 65535) {
            new IllegalArgumentException("port is invalid");
        }
        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
                Socket socket = new Socket(host, port);
                try {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    try {
                        output.writeUTF(method.getName());
                        output.writeObject(method.getParameterTypes());
                        output.writeObject(arguments);
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try {
                            Object result = input.readObject();
                            if (result instanceof Throwable) {
                                throw (Throwable) result;
                            }
                            return result;
                        } finally {
                            input.close();
                        }
                    } finally {
                        output.close();
                    }
                } finally {
                    socket.close();
                }
            }
        });
    }

    /**
     * 任务task
     */
    private static class Task implements Runnable {

        private Socket socket;

        private Object serviceObj;

        public Task(Socket socket, Object serviceObj) {
            this.socket = socket;
            this.serviceObj = serviceObj;
        }

        public void run() {
            try {
                ObjectInputStream inputStream = null;
                ObjectOutputStream outputStream = null;
                try {
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    String methodName = inputStream.readUTF();
                    Class<?>[] parameterTypes = (Class<?>[]) inputStream.readObject();
                    Object[] arguments = (Object[]) inputStream.readObject();
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    Method method = serviceObj.getClass().getMethod(methodName, parameterTypes);
                    Object result = method.invoke(serviceObj, arguments);
                    outputStream.writeObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
