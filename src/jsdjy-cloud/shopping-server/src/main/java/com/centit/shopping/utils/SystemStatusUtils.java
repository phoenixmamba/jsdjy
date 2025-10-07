package com.centit.shopping.utils;


import com.centit.shopping.po.TSystemStatus;
import com.centit.shopping.webmgr.service.SystemStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class SystemStatusUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemStatusUtils.class);
    private static final Map<String, Registry> socketMap=new HashMap<>();

    public static TSystemStatus getSystemStatus(String ip) {
        TSystemStatus ssp;
        Registry registry = socketMap.get(ip);

        try {
//            if (registry==null){
//                Socket socket = new Socket();
//                registry = LocateRegistry.getRegistry(ip, 8828, (host, port) -> {
//                    socket.connect(new InetSocketAddress(host, port), 5000);
//                    return socket;
//                });
//                socketMap.put(ip,registry);
//            }
            Socket socket = new Socket();
            registry = LocateRegistry.getRegistry(ip, 8828, (host, port) -> {
                socket.connect(new InetSocketAddress(host, port), 2000);
                return socket;
            });
            socketMap.put(ip,registry);
            SystemStatusService rhello = (SystemStatusService) registry.lookup("systemstatus");
            ssp = rhello.getRuntime();
        } catch (Exception e) {
            ssp = new TSystemStatus();
            ssp.setRemark(e.getMessage());
            LOGGER.error("连接失败，ip:{},message:{}", ip, e.getMessage());
        }
        return ssp;
    }

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        System.out.println(getSystemStatus("192.168.131.104"));
        System.out.println(getSystemStatus("192.168.131.104"));
        System.out.println(getSystemStatus("192.168.131.104"));
        System.out.println(getSystemStatus("192.168.131.104"));
        System.out.println(getSystemStatus("192.168.131.104"));
        System.out.println(getSystemStatus("192.168.131.104"));
        System.out.println(getSystemStatus("192.168.131.104"));
        System.out.println(getSystemStatus("192.168.131.104"));
        System.out.println(System.currentTimeMillis() - l);
    }
}
