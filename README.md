# Client  框架：SpringMVC+Spring+mybatis+maven

# IceServer 代码地址为:https://github.com/luomouren/IceServer.
# 一、Server  框架：SpringMVC+Spring+mybatis+maven

# 二、IceSSL--生成ice CA证书
参考网址: https://my.oschina.net/u/1787735/blog/488942
摘要: ice CA制作和使用详细过程
制作过程
1->安装Python2.7.3。 
2->配置Python的环境变量，即把Python的安装目录配置到path里面。
3->分别执行pip install --upgrade pip和pip install zeroc-icecertutils
4->命令执行完成之后会在Python安装目录下面生成Scripts，配置到环境变量path下面
5->到ice3.6安装目录..\Ice-3.6.3-demos\certs实例下面有个makecerts.py，把这个脚本copy到一个新的文件夹里面，执行这个Python脚本。
会生成如下图见附件(其中包括CA和证书文件以及客户端证书都会生成到执行脚本所在目录下面)。

# 三、IceGrid--添加IceSSL，注册器配置文件registry.cfg，详细如下：
# 配置文件可以参考ZeroC\Ice-3.6.3-demos\cpp\IceGrid\secure

######registry config for icegrid 实例名称，不能重复
IceGrid.InstanceName=IceGrid

######Registry进程不依赖其他进程，相反，每个Node上的服务IceGrid进程及客户端都需要和Registry通讯，在配置文件中需要定义Registry的Service Locator访问地址
######另外，如果有从注册服务器，从注册服务器的配置文件中属性Ice.Default.Locator应调整为使用ssl端点。
Ice.Default.Locator=IceGrid/Locator:ssl -h 192.168.5.1 -p 4062:tcp -h 192.168.5.1 -p 4061

###### 指定主注册服务的名称
Ice.ProgramName=Master
###### 指定主注册鼓舞的跟踪节点信息的级别(0~3),默认为0
IceGrid.Registry.Trace.Node=3
###### 指定主/从热备注册服务的跟踪级别(0~3)，默认为0
IceGrid.Registry.Trace.Replica=3

###### 定义节点是否和注册服务并置在一起，1为并置，0为不并置
IceGrid.Node.CollocateRegistry=1

###### 激活器跟踪级别，通常为0/1/2/3级，默认是0
IceGrid.Node.Trace.Activator=1
###### 对象适配器跟踪级别，通常为0/1/2/3级，默认是0
###### IceGrid.Node.Trace.Adapter=2
###### 服务跟踪级别，通常为0/1/2/3级，默认是0
###### IceGrid.Node.Trace.Server=3

######各个端点配置为ssl端点
######保留tcp端点，允许客户端用tcp连接注册服务器
IceGrid.Registry.Client.Endpoints=ssl -h 192.168.5.1 -p 4062:tcp -h 192.168.5.1 -p 4061
IceGrid.Registry.Server.Endpoints=ssl -h 192.168.5.1
IceGrid.Registry.Internal.Endpoints=ssl -h 192.168.5.1

###### registry 持久化文件存放目录 需要手工创建目录
###### IceGrid.Registry.Data=./registry
###### 注册中心数据保存路径，需要手动创建文件夹
IceGrid.Registry.Data=G:\07ice\data\registry
######是否允许动态注册  生产关闭
IceGrid.Registry.DynamicRegistration=1

Ice.LogFile=G:\07ice\logs\registry\registry.log

######标识为主服务
######IceGrid.Registry.ReplicaName=Master

######设定防火墙安全代理，从而控制客户端访问注册表时可用的权限
IceGrid.Registry.PermissionsVerifier=IceGrid/NullPermissionsVerifier
######设定防火墙代理，从而控制注册表管理者可用的权限 
IceGrid.Registry.AdminPermissionsVerifier=IceGrid/NullPermissionsVerifier
######设定SSL安全代理，从而设定客户端访问注册表时的SSL安全访问机制   
IceGrid.Registry.SSLPermissionsVerifier=IceGrid/NullSSLPermissionsVerifier
######设定SSL安全代理，从而设定注册表管理者的SSL安全访问机制 
IceGrid.Registry.AdminSSLPermissionsVerifier=IceGrid/NullSSLPermissionsVerifier  
 
######配置管理授权的帐号和密码
IceGridAdmin.Username=gyadmin
IceGridAdmin.Password=guangyuan 

###### IceSSL properties
######使用的是icegridregistry.exe可执行程序，所以配置IceSSL插件使用C++配置IceSSL:createIceSSL  
Ice.Plugin.IceSSL=IceSSL:createIceSSL
######证书所在目录
IceSSL.DefaultDir=certs1
######数字证书文件-PKCS12 files
IceSSL.CertFile=server.p12
IceSSL.Keystore=server.jks
######数字证书签名的根证书
IceSSL.CAs=cacert.pem
######读取私钥时使用的密码
IceSSL.Password=password
######Log properties
IceGrid.Registry.Trace.Adapter=1

######如果允许管理客户端icegrid admin在不用证书情况下依然可以进行ssl连接到注册服务器，则需设置如下属性：
######注：IceSSL.VerifyPeer 在ssl握手过程中这个配置有3种认证方式，分别为0 是客户端和服务端都不认证。1 单向认证.2 双向认证，是ice的默认ssl配置。
IceSSL.VerifyPeer=1

###### Required for a Java 1.8 client to talk to a C++ server on OSX
IceSSL.ProtocolVersionMax=tls1_1

#四、应用程序配置--添加IceSSL。配置文件为grid.xml，详细如下：


<icegrid>
	<application name="gyApp">
		<properties id="MultiThreaded">
			<property name="Ice.PrintStackTraces" value="1" />
			<!-- #开启SSL安全连接跟踪功能 -->
	      	<property name="IceSSL.Trace.Security" value="1" />
	      	<property name="Ice.ThreadPool.Client.Size" value="2" />
	      	<property name="Ice.ThreadPool.Client.SizeMax" value="50" />
	      	<property name="Ice.ThreadPool.Server.Size" value="10" />
	      	<property name="Ice.ThreadPool.Server.SizeMax" value="100" />
	      	<property name="IceBox.InheritProperties" value="1" />
	      	<property name="Ice.Override.ConnectTimeout" value="5000" />
	      	<property name="Ice.Override.Timeout" value="10000" />
	      	<property name="IceBox.Trace.ServiceObserver" value="1" />
	      	<property name="Ice.Default.LocatorCacheTimeout" value="300" />
	      	<property name="Ice.BackgroundLocatorCacheUpdates" value="1" /> 
		</properties>		
		<properties id="props">
           	<property name="Ice.StdErr" value="G:\07ice\logs\ice\ice_stderr.log"/>
            <property name="Ice.StdOut" value="G:\07ice\logs\ice\ice_stdout.log"/>
        </properties>
        
       <!--  配置IceSSL时添加 -->
       <properties id="Secure-Ice.Admin">
	        <property name="Ice.Plugin.IceSSL" value="IceSSL.PluginFactory"/>
			<property name="IceSSL.CAs" value="cacert.pem"/>
			<property name="IceSSL.CertFile" value="server.p12"/>
			<property name="IceSSL.Keystore" value="server.jks"/>
			<property name="IceSSL.Password" value="password"/>
			<property name="IceSSL.DefaultDir" value="certs1"/>
			
			<property name="Ice.Admin.Endpoints" value="ssl -h 192.168.5.1"/>
	    </properties>
        
        <properties id="mx">
	        <property name="IceMX.Metrics.Debug.GroupBy" value="id"/>
	        <property name="IceMX.Metrics.Debug.Disabled" value="1"/>
	        <property name="IceMX.Metrics.ByParent.GroupBy" value="parent"/>
	        <property name="IceMX.Metrics.ByParent.Disabled" value="1"/>
        </properties>
        
		<server-template id="MyServerTemplate">
			<parameter name="id" />
			<!-- exe指定java启动 -->
			<icebox id="MyServer${id}" exe="java" activation="on-demand">
				<properties>
					<properties refid="MultiThreaded" />
					<properties refid="props" />
					<properties refid="Secure-Ice.Admin" />
					<properties refid="mx" />
				</properties>
				<option>-Xmx512M</option>
				<!-- iceBox启动main方法 -->
				<option>IceBox.Server</option>
				<!-- 开发的代码路径 -->
				<!-- 检查<env></env>标签配置是否错误,env标签不能有换行,最后一个env的变量不能使用;结尾,如果还是不行将classpatch加入到系统环境变量或是每个依赖的jar都写到env标签中。  -->
				<env>CLASSPATH=G:\01Install\ZeroC\Ice-3.6.3\lib\*;G:\00workspace\00workspace\01JCXT\IceServer\target\Server\WEB-INF\classes;G:\00workspace\00workspace\01JCXT\IceServer\target\Server\WEB-INF\lib\*</env>
				<!-- entry指定iceBox启动的service，replica-group指定endpoints组 -->
				<service name="MyService" entry="com.weisi.Server.frame.ice.GenIceBoxService">
					<property name="servantClassName" value="com.weisi.Server.service.serverIce.Impl.MyServiceImpl"/>
					<!-- 如需要客户端与服务程序使用ssl通信 endpoints为ssl-->
					<adapter name="MyService" id="MyService${id}" endpoints="ssl -h 192.168.5.1"
						replica-group="MyServiceRep">
					</adapter>
				</service>
				
				<service name="MyService2" entry="com.weisi.Server.frame.ice.GenIceBoxService">
					<property name="servantClassName" value="com.weisi.Server.service.iceGrid.impl.MyServiceImpl2"/>
					<!-- 如需要客户端与服务程序使用ssl通信 endpoints为ssl-->
					<adapter name="MyService2" id="MyService2${id}" endpoints="ssl -h 192.168.5.1"
						replica-group="MyService2Rep">
					</adapter>
				</service>
				
				<service name="SwitchService" entry="com.weisi.Server.frame.ice.GenIceSwitcherBoxService">
					<property name="servantClassName" value="com.weisi.Server.service.switcher.impl.SwitchI"/>
					<!-- 如需要客户端与服务程序使用ssl通信 endpoints为ssl-->
					<adapter name="SwitchService" id="SwitchService${id}" endpoints="ssl -h 192.168.5.1"
						replica-group="SwitchServiceRep">
					</adapter>
				</service>
			</icebox>
		</server-template>
		
		<replica-group id="MyServiceRep">
			<load-balancing type="round-robin" n-replicas="0" />
			<!-- 指定servant的名字 -->
			<object identity="MyService" type="::serverIce::MyService" />
		</replica-group>
		<replica-group id="MyService2Rep">
			<load-balancing type="round-robin" n-replicas="0" />
			<object identity="MyService2" type="::gridServer::MyService" />
		</replica-group>
		<replica-group id="SwitchServiceRep">
			<load-balancing type="round-robin" n-replicas="0" />
			<object identity="SwitchService" type="::switcher::ISwitchCallback" />
		</replica-group><!-- module:interface -->
		
		<!-- 每个node定义2个icebox -->
		<node name="node1">
			<server-instance template="MyServerTemplate" id="Node1_1" />
			<server-instance template="MyServerTemplate" id="Node1_2" />
		</node>
		
		<node name="node2">
			<server-instance template="MyServerTemplate" id="Node2_1" />
			<server-instance template="MyServerTemplate" id="Node2_2" />
		</node>
	</application>
</icegrid>


# 五、IceNode节点配置--添加配置IceSSL，配置文件为node1.cfg、node2.cfg,其中node1.cfg如下：

######## 注册服务的端点信息(注册服务和所有的从注册服务)，节点注册时要用到
######## 配置定位器的地址为 定位主服务的地址和从服务的地址，由node节点主动向定位服务进行注册
########Ice.Default.Locator=IceGrid/Locator:default -h 192.168.10.11 -p 4061:default -h 192.168.10.12 -p 4061 
######## 指向注册服务器的代理调整为使用ssl，如下
Ice.Default.Locator=IceGrid/Locator:ssl -h 192.168.5.1 -p 4062

######## 指定节点1的名称,必须唯一      
IceGrid.Node.Name=node1

######## 节点被访问的端口信息，注册服务使用这个端点和节点通信，通常设置为default。节点端点调整为ssl，如下
IceGrid.Node.Endpoints=ssl -h 192.168.5.1

########设置节点1相关数据的存储目录
IceGrid.Node.Data=G:\07ice\data\node\node1
IceGrid.Node.Output=G:\07ice\data\node\node1

######## 节点上的服务程序的标准错误重定向到标准输出
IceGrid.Node.RedirectErrToOut=1

######## 激活器跟踪级别，通常为0/1/2/3级，默认是0
IceGrid.Node.Trace.Activator=3
######## 对象适配器跟踪级别，通常为0/1/2/3级，默认是0
######## IceGrid.Node.Trace.Adapter=3
######## 服务跟踪级别，通常为0/1/2/3级，默认是0
######## IceGrid.Node.Trace.Server=3

IceGrid.Node.CollocateRegistry=0
########指定错误日志文件
Ice.StdErr=G:\07ice\logs\node\node1\node.stderr.log  
Ice.StdOut=G:\07ice\logs\node\node1\node.stdout.log

######## IceSSL properties
########使用的是icegridregistry.exe可执行程序，所以配置IceSSL插件使用C++配置IceSSL:createIceSSL。JAVA配置则是Ice.Plugin.IceSSL=IceSSL.PluginFactory   
Ice.Plugin.IceSSL=IceSSL:createIceSSL
########证书所在目录
IceSSL.DefaultDir=certs1
########数字证书文件-PKCS12 files
IceSSL.CertFile=server.p12
IceSSL.Keystore=server.jks
########数字证书签名的根证书
IceSSL.CAs=cacert.pem
########读取私钥时使用的密码
IceSSL.Password=password

########IceSSL.TrustOnly.Client=CN="Server";CN="Master";CN="Slave"
########IceSSL.TrustOnly.Server=CN="Master";CN="Slave"


# 六、Client客户端访问IceSSL服务端，代码地址为https://github.com/luomouren/IceClient 其中测试java类如下：

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




