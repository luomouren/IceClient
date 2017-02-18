// **********************************************************************
//
// ���
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
		//void say(string data);

		/**
		 * ΢�Ź��ںų��ɹ������á�IceServer��д��
		 * @param meterNo ���
		 * @param moneyAmount ��ֵ����λ Ԫ
		 * @param chargeValue ��ֵ������ΪkWh,ˮΪ�֣�ȼ��Ϊ������
		 * @return true/false д��ɹ���񷵻ظ���΢�š� 
		 */
		bool rechargeServer(string meterNo,string chargeMoney, string chargeValue);

	};

};
