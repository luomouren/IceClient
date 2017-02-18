// **********************************************************************
//
// 充电Client
//
// **********************************************************************

#pragma once

#include <Glacier2/Session.ice>

module RechargeIce
{
	/**
	  * 写表客户端
	  */
	interface RechargeCallback
	{
		void message(string data);
		/**
		 * 【IceServer】调用【写表客户端】回写表字
		 * @param meterNo 表号
		 * @param moneyAmount 充值金额
		 * @param chargeValue 充值量，电为kWh,水为吨，燃气为立方米
		 * @return true/false 写表成功与否返回给【IceServer】
		 */
		bool rechargeClient(string meterNo,string chargeMoney, string chargeValue);
	};
	

};
