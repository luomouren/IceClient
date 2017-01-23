// **********************************************************************
//
// Copyright (c) 2003-2015 ZeroC, Inc. All rights reserved.
//
// **********************************************************************
package com.weisi.Client.frame.init;
import com.weisi.Server.serverIce.MyServicePrx;
import com.weisi.Server.serverIce.MyServicePrxHelper;

/**
 * 客户端IceSSL连接服务端为IceBox IceSSL启动的
 * @author Amy
 */
public class Client extends Ice.Application
{
    @Override
    public int
    run(String[] args)
    {
        if(args.length > 0)
        {
            System.err.println(appName() + ": too many arguments");
            return 1;
        }
        
        MyServicePrx twoway = MyServicePrxHelper.uncheckedCast(
            communicator().propertyToProxy("MyService.Proxy"));
        if(twoway == null)
        {
            System.err.println("invalid object reference");
            return 1;
        }
        System.out.println("twoway.hellow()===="+twoway.hellow());
        
        return 0;
    }

    public static void
    main(String[] args)
    {
        Client app = new Client();
        int status = app.main("Client", args/*, "config.client"*/);
        System.exit(status);
    }
}
