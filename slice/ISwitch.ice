[["java:package:com.weisi.Server"]]
#include <Ice/BuiltinSequences.ice>
#include <Ice/Identity.ice>
#include "ICommon.ice"
module switcher
{
	/**
	 * 回调客户端接口定义(服务端调用客户端的接口)
	 */
	interface ISwitchCallback
	{	 
		/**
		 * 发送二进制数组
		 * @param byteSeq 二进制数组
		 * @return true/false
		 */
		bool send(Ice::ByteSeq byteSeq) throws SwitchException;
		
		/**
		 * 发送字符串
		 * @param msg 字符串
		 * @return true/false
		 */
		bool sendMsg(string msg) throws SwitchException;
		
		/**
		 * 充值成功后调用客户端回写表字
		 * @param meterNo 表号
		 * @param moneyAmount 充值金额
		 * @return true/false
		 */
		bool callWriteBaseMeter(string meterNo,string moneyAmount) throws SwitchException;
	};
	
	/**
	 * 服务端接口定义(客户端调用服务端的接口)
	 */
	interface ISwitch
	{
		/**
		 * 对服务端进行心跳（无异常则表示成功）
		 * @param sn 设备串号
		 * @param time 请求时间戳，13位 毫秒级别
		 */
		bool heartbeat(Ice::Identity id, string sn, long time) throws SwitchException;
		
		/**
		 * 向otherSn客户端发送消息
		 * @param otherSn 设备串号
		 * @param msg 字符串
		 */
		bool sendMsgToOtherClient(string otherSn, string msg) throws SwitchException;
		
		/**
		 * 微信公众号充值成功，向sn客户端--调用写表字
		 * @param sn 设备串号
		 * @param meterNo 表号
		 * @param moneyAmount 充值金额
		 */
		bool callWriteBaseMeter(string sn,string meterNo,string moneyAmount) throws SwitchException;
		
		/**
		 * 设备回调
		 * @param byteSeq 二进制数组
		 * @return true/false
		 */
		bool callBack(string msg) throws SwitchException;
	};
};