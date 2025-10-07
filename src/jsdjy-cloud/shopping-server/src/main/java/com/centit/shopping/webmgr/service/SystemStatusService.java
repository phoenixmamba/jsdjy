package com.centit.shopping.webmgr.service;


import com.centit.shopping.po.TSystemStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SystemStatusService extends Remote {
    TSystemStatus getRuntime() throws RemoteException;//内存 M 网络 kb/s
}
