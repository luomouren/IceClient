package com.weisi.Client.frame.init;

import org.apache.log4j.Logger;

import com.weisi.Client.service.ice.impl.SwitchCallbackI;
import com.weisi.Server.serverIce.MyServicePrx;
import com.weisi.Server.serverIce.MyServicePrxHelper;
import com.weisi.Server.switcher.Callback_ISwitch_heartbeat;
import com.weisi.Server.switcher.ISwitchPrx;
import com.weisi.Server.switcher.ISwitchPrxHelper;

import Ice.LocalException;
import Ice.UserException;

public class SwitcherGridClient {
    private static Logger LOGGER = Logger.getLogger(SwitchClient.class);
    public static String sn = "0481deb6494848488048578316516694";
    /**
     * 双向通信  
     * 服务端Grid方式启动
     * @param args
     */
    public static void main(String[] args) {
      int status=0;
      Ice.Communicator ic =null;
      try {
        //初始化通信器
        String reg ="--Ice.Default.Locator=IceGrid/Locator:tcp -h 60.205.168.218 -p 4061";
        //String reg ="--Ice.Default.Locator=IceGrid/Locator:tcp -h 192.168.1.109 -p 20000";
        String[] parms = new String[]{reg};
        ic=Ice.Util.initialize(parms);
        //传入远程服务单元的名称、网络协议、Ip以及端口，构造一个Proxy对象
        Ice.ObjectPrx base = ic.stringToProxy("SwitchService");
        //Ice.ObjectPrx base = ic.stringToProxy("SwitchServer");
        
        //通过checkedCast向下转型，获取MyService接口的远程，并同时监测根据传入的名称，
        //获取服务单元是否OnlineBook的代理接口，如果不是则返回null对象
        ISwitchPrx switchPushPrx = ISwitchPrxHelper.checkedCast(base);
        
        if(switchPushPrx==null){
          throw new Error("Invalid proxy");
        }
        switchPushPrx.ice_ping();
        //创建adapter
        Ice.ObjectAdapter adapter = ic.createObjectAdapter("");
        Ice.Identity id = new Ice.Identity();
        id.category = "";
        id.name = "SwitchClient";
        
        adapter.add(new SwitchCallbackI(), id);
        adapter.activate();
        switchPushPrx.ice_getConnection().setAdapter(adapter);
        
        
        LOGGER.info("客户端启动成功---SwitchClient ice is started! " + "getEndpoint = "
                + switchPushPrx.ice_getConnection().getEndpoint()._toString());
        
        
        try {
          while (true) {
              LOGGER.info("客户端开启心跳---SwitchClient is begin heartbeat.");
              // 使用异步的方式
              switchPushPrx.begin_heartbeat(id, sn, 1, 2, new Callback_ISwitch_heartbeat() {

                  @Override
                  public void exception(LocalException __ex) {
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
                  }
              });

              LOGGER.info("客户端心跳结束---SwitchClient is end heartbeat.\n");
              Thread.sleep(10000);
            }
          // 可以使用以上的while(true) Thread.sleep(HEARTBEAT_TIME);的方式定时请求保持tcp连接的心跳，这个方式是为了每次心跳都传参
          // 也可以使用一下的方式，使用ice自带的功能保持tcp的连接心跳，无法每次心跳传参
          //holdHeartbeat(switchPushPrx.ice_getConnection());
        } catch (Exception e) {
          e.printStackTrace();
        }
        
        
      } catch (Exception e) {
        e.printStackTrace();
        status=1;
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
