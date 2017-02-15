package com.weisi.Client.frame.init;

import org.apache.log4j.Logger;

import com.weisi.Client.service.ice.impl.SwitchCallbackI;
import com.weisi.Server.switcher.Callback_ISwitch_heartbeat;
import com.weisi.Server.switcher.ISwitchPrx;
import com.weisi.Server.switcher.ISwitchPrxHelper;

import Ice.LocalException;
import Ice.UserException;

/**
 * 服务端为IceGrid方式启动
 * 服务端配置了IceSSL加密
 * 客户端与服务端保持定时心跳
 * @author Amy
 */
public class SwitcherGridClient {
    private static Logger LOGGER = Logger.getLogger(SwitchClient.class);
    public static String sn = "0481deb6494848488048578316516694";
    

    public static void main(String[] args) {
      mainConnector();
    }
    
    /**
     * 心跳 双向通信客户端 
     * 服务端为IceGrid启动方式
     */
    public static void mainConnector(){
        int status=0;
        Ice.Communicator ic =null;
        try {
          connectIceServer(ic, "--Ice.Default.Locator=IceGrid/Locator:ssl -h 192.168.5.1 -p 4062", "SwitchService",  
              "0481deb6494848488048578316516694", "","SwitchClient");
        } catch (Exception e) {
          e.printStackTrace();
          LOGGER.error(e);
          status=1;//status=1表示非正常情况退出
        }finally{
          if(ic!=null){
            ic.destroy();
          }
        }
        System.exit(status);
    }
    
    /**
     * @param ic 通信器
     * @param endpoints 服务器连接地址
     * @param serviceName  服务名称
     * @param sn 设备串号
     * @param category 客户端种类
     * @param clientName 客户端名称
     */
    public static void  connectIceServer(Ice.Communicator ic,String endpoints, String serviceName,
        String sn,String category,String clientName){
      //初始化通信器
      String[] parms = new String[]{endpoints,
          "--Ice.Plugin.IceSSL=IceSSL:IceSSL.PluginFactory",
          "--IceSSL.DefaultDir=src/main/resources/certs5.1",
          "--IceSSL.Keystore=client.jks",
          "--IceSSL.Password=password"};
          
      ic=Ice.Util.initialize(parms);
      //传入远程服务单元的名称、网络协议、Ip以及端口，构造一个Proxy对象
      Ice.ObjectPrx base = ic.stringToProxy(serviceName);
      
      //是否连接成功
      boolean isConnect=true;
      
      ISwitchPrx switchPushPrx = null;
      try {
        //通过checkedCast向下转型，获取MyService接口的远程，并同时监测根据传入的名称，
        //获取服务单元是否OnlineBook的代理接口，如果不是则返回null对象
        switchPushPrx = ISwitchPrxHelper.checkedCast(base);
      } catch (Exception e) {
        LOGGER.error("连接服务器 Exception,endpoints:"+endpoints+"  ,客户端名称:"+clientName+"  ,设备串号:"+sn+"  ,详细错误为:"+e);
        isConnect = false;
      }
      
      //第一次连接服务器，若此时服务器宕了，则进行重连
      if(!isConnect){
        try{
          //多少毫秒后重连
          Thread.currentThread().sleep(10000);//毫秒 
          connectIceServer(ic,endpoints,serviceName,sn,category,clientName);
        }catch(Exception e){
          LOGGER.error("连接服务器 Exception,endpoints:"+endpoints+"  ,客户端名称:"+clientName+"  ,设备串号:"+sn+"  ,详细错误为:"+e);
        }
      }
      
      if(switchPushPrx==null){
        throw new Error("Invalid proxy");
      }
      switchPushPrx.ice_ping();
      //创建adapter
      Ice.ObjectAdapter adapter = ic.createObjectAdapter("");
      Ice.Identity id = new Ice.Identity();
      id.category = category;
      id.name = clientName;
      
      adapter.add(new SwitchCallbackI(), id);
      adapter.activate();
      switchPushPrx.ice_getConnection().setAdapter(adapter);
      
      
      LOGGER.info("客户端启动成功" + "getEndpoint = "
              + switchPushPrx.ice_getConnection().getEndpoint()._toString());
      
      try {
        while (true) {
            LOGGER.info("客户端开启心跳---SwitchClient is begin heartbeat.");
            
            switchPushPrx.ice_isBatchDatagram();
            //心跳时,获取当前时间戳
            long time = System.currentTimeMillis();//毫秒级别，13位
            // 使用异步的方式
            switchPushPrx.begin_heartbeat(id, sn,time, new Callback_ISwitch_heartbeat() {
               
                @Override
                public void exception(LocalException __ex) {
                  //服务端停掉后，心跳异常
                  LOGGER.error("心跳异常 LocalException,endpoints:"+endpoints+"  ,客户端名称:"+clientName+"  ,设备串号:"+sn+"  ,详细错误为:"+__ex);
                  //心跳过程中出现连接失败，则从新连接服务器，直到连上服务器
                  try{
                    //多少毫秒后重连
                    Thread.currentThread().sleep(10000);//毫秒 
                    Ice.Communicator ic =null;
                    connectIceServer(ic,endpoints,serviceName,sn,category,clientName);
                  }catch(Exception e){
                    LOGGER.error("连接服务器 Exception,endpoints:"+endpoints+"  ,客户端名称:"+clientName+"  ,设备串号:"+sn+"  ,详细错误为:"+e);
                  }
                }
                
                @Override
                public void response(boolean arg) {
                    LOGGER.info("心跳结果---heartbeat result = " + arg);
                    if (arg) {
                        LOGGER.info("心跳成功");
                    } else {
                        LOGGER.info("心跳失败");
                    }
                }

                @Override
                public void exception(UserException ex) {
                  LOGGER.error("心跳异常 UserException,endpoints:"+endpoints+"  ,客户端名称:"+clientName+"  ,设备串号:"+sn+"  ,详细错误为:"+ex);
                }
                
            });
            LOGGER.info("客户端心跳结束---SwitchClient is end heartbeat.\n");
            //休息多少毫秒后发送心跳
            Thread.sleep(10000);
          }
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.error("endpoints:"+endpoints+"  ,客户端名称:"+clientName+"  ,设备串号:"+sn+"  ,详细错误为:"+e);
      }
      
    }
    
    
    /**
     * ice自带保持心跳
     * @param con
     */
    private void holdHeartbeat(Ice.Connection con) {
        con.setCallback(new Ice.ConnectionCallback() {
            @Override
            public void heartbeat(Ice.Connection c) {
                System.out.println("sn:" + sn + " client heartbeat....");
            }

            @Override
            public void closed(Ice.Connection c) {
                System.out.println("sn:" + sn + " " + "closed....");
            }
        });

        // 每30/2 s向对方做心跳
        // 客户端向服务端做心跳 服务端打印服务端的con.setCallback(new Ice.ConnectionCallback()
        con.setACM(new Ice.IntOptional(10), new Ice.Optional<Ice.ACMClose>(Ice.ACMClose.CloseOff),
                new Ice.Optional<Ice.ACMHeartbeat>(Ice.ACMHeartbeat.HeartbeatAlways));

    }
    
}
