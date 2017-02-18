// **********************************************************************
//
// ���
//
// **********************************************************************

#pragma once

#include <Glacier2/Session.ice>

module RechargeIce
{
	/**
	  * д��ͻ���
	  */
	interface RechargeCallback
	{
		void message(string data);
		/**
		 * ��IceServer�����á�д��ͻ��ˡ���д����
		 * @param meterNo ���
		 * @param moneyAmount ��ֵ���
		 * @param chargeValue ��ֵ������ΪkWh,ˮΪ�֣�ȼ��Ϊ������
		 * @return true/false д��ɹ���񷵻ظ���IceServer��
		 */
		bool rechargeClient(string meterNo,string chargeMoney, string chargeValue);
	};
	

};
