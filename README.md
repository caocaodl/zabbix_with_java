|--C													服务器端相关程序源码
|	\
|	|--iradar.preenv						编译环境搭建脚本
|	|--iradar.predeps						编译和运行时所需的RPM依赖包
|	|--iradar.server.release		监控Server、Proxy、Agent等相关C程序源码
|	|--iradar.deploy.release		发布run安装包打包源码
|	|--iradar.agent							监控Agent程序在不同操作系统下的打包源码
|
|--Java												Java相关源码，包括Web服务和Agent所需的功能Jar包
|	\
|	|--iRadar.Portal						Web服务的源码
|	|
|	|--lib											Web服务下定制Jar包相关源码
|	|	\
|	|	|--IaaS-Jasdk							isoft-jasdk-v2.0.jar——云服务数据获取SDK源码
|	|	|--iRadar									isoft-iradar-2014-02-07.jar——监控底层API库源码
|	|	|--iSoft-Core							isoft-core.jar——Web服务框架源码
|	|	|--iSoft-Zend							isoft-zend-v0.4.jar——PHP Zend引擎相关Java实现源码
| |
|	|--agent										Agent所需功能Jar包源码
|	|	\
|	|	|--iRadar.DB							Agent下数据库采集组件db.jar源码
|	|	|--iRadar.JMX							Agent下中间件采集组件jmx.jar源码
