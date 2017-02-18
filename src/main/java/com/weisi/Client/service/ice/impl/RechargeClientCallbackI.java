package com.weisi.Client.service.ice.impl;

import org.apache.log4j.Logger;

import Ice.Current;
import RechargeIce._RechargeCallbackDisp;

public class RechargeClientCallbackI extends _RechargeCallbackDisp {

	private static Logger LOGGER = Logger.getLogger(RechargeClientCallbackI.class);
	private static final long serialVersionUID = 1L;

	public RechargeClientCallbackI() {
	}

    @Override
    public void message(String data, Current __current) {
      System.out.println("message方法,data="+data);
      LOGGER.info("message方法,data="+data);
    }
  
    @Override
    public boolean rechargeClient(String meterNo, String chargeMoney, String chargeValue,
        Current __current) {
      LOGGER.info("rechargeClient方法,meterNo="+meterNo+",chargeMoney="+chargeMoney+",chargeValue"+chargeValue);
      System.out.println("rechargeClient方法，meterNo="+meterNo+",chargeMoney="+chargeMoney+",chargeValue"+chargeValue);
      return false;
    }


}
