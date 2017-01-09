// **********************************************************************
//
// Copyright (c) 2003-2015 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.6.1
//
// <auto-generated>
//
// Generated from file `myservice.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package com.weisi.Server.serverIce;

public final class MyServiceHolder extends Ice.ObjectHolderBase<MyService>
{
    public
    MyServiceHolder()
    {
    }

    public
    MyServiceHolder(MyService value)
    {
        this.value = value;
    }

    public void
    patch(Ice.Object v)
    {
        if(v == null || v instanceof MyService)
        {
            value = (MyService)v;
        }
        else
        {
            IceInternal.Ex.throwUOE(type(), v);
        }
    }

    public String
    type()
    {
        return _MyServiceDisp.ice_staticId();
    }
}
