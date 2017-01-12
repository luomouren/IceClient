package com.weisi.Client.frame.init;

import org.apache.log4j.Logger;

import com.weisi.Client.service.ice.impl.SwitchCallbackI;
import com.weisi.Server.switcher.Callback_ISwitch_heartbeat;
import com.weisi.Server.switcher.ISwitchPrx;
import com.weisi.Server.switcher.ISwitchPrxHelper;

import Ice.LocalException;
import Ice.UserException;

public class SwitchClient {
    private static Logger LOGGER = Logger.getLogger(SwitchClient.class);
    /**
     * 心跳 双向通信客户端
     * @param args
     */
    public static void main(String[] args) {
      int status=0;
      Ice.Communicator ic =null;
      try {
        //初始化通信器
        ic=Ice.Util.initialize(args);
        //传入远程服务单元的名称、网络协议、Ip以及端口，构造一个Proxy对象
        Ice.ObjectPrx base = ic.stringToProxy("Switch:default -p 5010");
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
        
        //调用服务方法
        
        try {
          while (true) {
              LOGGER.info("客户端开启心跳---SwitchClient is begin heartbeat.");
              // 使用异步的方式
              switchPushPrx.begin_heartbeat(id, "0481deb6494848488048578316516694", 1, 2, new Callback_ISwitch_heartbeat() {

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
}
