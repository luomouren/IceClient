package com.weisi.Client.frame.init;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.weisi.Client.service.ice.impl.SwitchCallbackI;
import com.weisi.Server.switcher.Callback_ISwitch_heartbeat;
import com.weisi.Server.switcher.ISwitchPrx;
import com.weisi.Server.switcher.ISwitchPrxHelper;

import Ice.Connection;
import Ice.LocalException;
import Ice.TCPConnectionInfo;
import Ice.UserException;

/**
 * 服务端为IceBox方式启动
 * @author Amy
 */
public class SwitchClient {
    private static Logger LOGGER = Logger.getLogger(SwitchClient.class);
    
    public static void main(String[] args) {
      /*//当前时间
      SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM--dd hh:mm:ss");
      System.out.println("重连前 当前时间为:"+sdf.format(new Date()));
      //第一次连接服务器，若此时服务器宕了，则进行重连
      try{
        Thread.currentThread().sleep(10000);//毫秒 
        System.out.println("睡眠后时间为:"+sdf.format(new Date()));
        long s = System.currentTimeMillis();
        System.out.println(s);
      }catch(Exception e){
      }*/
      mainConnector();
    }
    
    /**
     * 心跳 双向通信客户端 
     * 服务端为IceBox.Server启动方式
     */
    public static void mainConnector(){
        int status=0;
        Ice.Communicator ic =null;
        long time = System.currentTimeMillis();//毫秒级别，13位
        try {
          connectIceServer(ic, "MyService:ssl -h 192.168.5.1 -p 4062",   
              "0481deb6494848488048578316516694", 1,  2, "","SwitchClient");
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
     * ice自带保持心跳
     * @param con
     */
    private static void holdHeartbeat(Ice.Connection con,String sn) {
        con.setCallback(new Ice.ConnectionCallback() {
            @Override
            public void heartbeat(Ice.Connection c) {
              LOGGER.info("sn:" + sn + " client heartbeat....");
            }

            @Override
            public void closed(Ice.Connection c) {
              LOGGER.info("sn:" + sn + " " + "closed....");
            }
        });

        // 每30/2 s向对方做心跳
        // 客户端向服务端做心跳 服务端打印服务端的con.setCallback(new Ice.ConnectionCallback()
        con.setACM(new Ice.IntOptional(10), new Ice.Optional<Ice.ACMClose>(Ice.ACMClose.CloseOff),
                new Ice.Optional<Ice.ACMHeartbeat>(Ice.ACMHeartbeat.HeartbeatAlways));
    }
    
    
    /**
     * @param ic 通信器
     * @param endpoints 服务器连接地址
     * @param sn 设备串号
     * @param netMode 网络接入方式 0:没有 1:3G 2:4G 3:以太网 4：wifi 5：2G
     * @param netStrength 网络信号强度
     * @param category 客户端种类
     * @param clientName 客户端名称
     */
    public static void  connectIceServer(Ice.Communicator ic,String endpoints,   
        String sn, int netMode, int netStrength,
        String category,String clientName){
      //初始化通信器
      ic=Ice.Util.initialize(new String[]{
          "--Ice.Plugin.IceSSL=IceSSL:IceSSL.PluginFactory",
          "--IceSSL.DefaultDir=src/main/resources/certs1",
          "--IceSSL.Keystore=client.jks",
          "--IceSSL.Password=password"});
      //传入远程服务单元的名称、网络协议、Ip以及端口，构造一个Proxy对象
      Ice.ObjectPrx base = ic.stringToProxy(endpoints);
      
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
          //重新传当前时间戳
          connectIceServer(ic,endpoints,sn,netMode,netStrength,category,clientName);
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
      System.out.println("+++++++++++++++++1111++++++++++++++++++++++");
      switchPushPrx.begin_sendMsgToOtherClient("0481deb6494848488048578316516699", "*****来自客户端1发送********");
      System.out.println("+++++++++++++++++2222++++++++++++++++++++++");
      try {
        String ip = ""; // 缓存当前ip
        int port = 0; // 缓存当前端口
        
        while (true) {
            // ip或端口不一样时，重置适配器（服务端重启的情况）
            if (switchPushPrx == null) {
                break;
            }
            
            Connection connection = null;
            String localIp = null;
            int localPort = 0;
            try {
                connection = switchPushPrx.ice_getConnection();
                TCPConnectionInfo connectionInfo = (TCPConnectionInfo) connection.getInfo();
                localIp = connectionInfo.localAddress;
                localPort = connectionInfo.localPort;
                LOGGER.info("localIp:" + localIp + "; localPort:" + localPort);
                if (!ip.equals(localIp) || port != localPort) {
                    ip = localIp;
                    port = localPort;
                    LOGGER.info("ip or port is change. set adapter.");
                    connection.setAdapter(adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
          
          
          
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
                    connectIceServer(ic,endpoints,sn,netMode,netStrength,category,clientName);
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
        
        
        // 可以使用以上的while(true) Thread.sleep(HEARTBEAT_TIME);的方式定时请求保持tcp连接的心跳，这个方式是为了每次心跳都传参
        // 也可以使用一下的方式，使用ice自带的功能保持tcp的连接心跳，无法每次心跳传参
        //holdHeartbeat(switchPushPrx.ice_getConnection(),sn);
        
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.error("endpoints:"+endpoints+"  ,客户端名称:"+clientName+"  ,设备串号:"+sn+"  ,详细错误为:"+e);
      }
      
    }
    
    
    
}
