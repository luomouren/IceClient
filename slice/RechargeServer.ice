// **********************************************************************
//
// 充电Server
//
// **********************************************************************

#pragma once

#include <Glacier2/Session.ice>
#include "RechargeClient.ice"

module RechargeIce
{
	/**
	  * IceServer
	  */
	interface RechargeSession extends Glacier2::Session
	{
		void setCallback(RechargeCallback* callback);

		/**
		 * 微信公众号充电成功，调用【IceServer】写表
		 * @param meterNo 表号
		 * @param moneyAmount 充值金额，单位 元
		 * @param chargeValue 充值量，电为kWh,水为吨，燃气为立方米
		 * @return true/false 写表成功与否返回给【微信】 
		 */
		bool rechargeServer(string meterNo,string chargeMoney, string chargeValue);

		/**
		 * 判断【写表客户端】是否在线
		 * @return true/false 在线/不在线 
		 */
		bool isRechargeClientOnline();

	};

};
