package com.weisi.Client.frame.init;

import com.weisi.Server.serverIce.MyServicePrx;
import com.weisi.Server.serverIce.MyServicePrxHelper;

/**
 * 简单启动客户端
 * 服务端为IceGrid方式启动
 * 服务端配置了IceSSL加密
 */
public class MyClient2 {
    
    public static void main(String[] args) {
      int status=0;
      Ice.Communicator ic =null;
      try {
        //初始化通信器
        //String reg ="--Ice.Default.Locator=IceGrid/Locator:tcp -h localhost -p 4061";
        String reg ="--Ice.Default.Locator=IceGrid/Locator:ssl -h 192.168.5.1 -p 4062";
        String[] parms = new String[]{reg,"--Ice.Plugin.IceSSL=IceSSL:IceSSL.PluginFactory",
            "--IceSSL.DefaultDir=src/main/resources/certs1",
            "--IceSSL.Keystore=client.jks",
            "--IceSSL.Password=password"};
        
        ic=Ice.Util.initialize(parms);
        //传入远程服务单元的名称、网络协议、Ip以及端口，构造一个Proxy对象
        Ice.ObjectPrx base = ic.stringToProxy("MyService");
        //通过checkedCast向下转型，获取MyService接口的远程，并同时监测根据传入的名称，
        //获取服务单元是否OnlineBook的代理接口，如果不是则返回null对象
        MyServicePrx prxy = MyServicePrxHelper.uncheckedCast(base);
        if(prxy==null){
          throw new Error("Invalid proxy");
        }
        //调用服务方法
        String rt=prxy.hellow();
        System.out.println("prxy.hellow()===="+rt);
        
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
