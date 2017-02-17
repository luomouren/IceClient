package com.weisi.Client.service.ice.impl;

import org.apache.log4j.Logger;

import com.weisi.Server.switcher.SwitchException;
import com.weisi.Server.switcher._ISwitchCallbackDisp;

import Ice.Current;

/**
 * 回调客户端接口定义(服务端调用客户端的接口)
 */
public class SwitchCallbackI extends _ISwitchCallbackDisp {

	private static Logger LOGGER = Logger.getLogger(SwitchCallbackI.class);
	private static final long serialVersionUID = 1L;

	public SwitchCallbackI() {
	}

	@Override
	public boolean send(byte[] byteSeq, Current __current) throws SwitchException {
		// 客户端打印会打印以下信息
		LOGGER.info("服务端回调客户端--发送二进制消息--send() byteSeq = " + new String(byteSeq));
		return true;
	}

	@Override
	public boolean sendMsg(String msg, Current __current) throws SwitchException {
		// 客户端打印会打印以下信息
		LOGGER.info("服务端回调客户端--发送String消息--sendMsg() msg = " + msg);
		return true;
	}

	@Override
	public boolean callWriteBaseMeter(String meterNo, String moneyAmount, Current __current) throws SwitchException {
		// 客户端打印会打印以下信息
		LOGGER.info("微信公众号充值成功,表号为:" + meterNo+",充值金额为:" + moneyAmount);
		return false;
	}

}
