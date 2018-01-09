# scraj-common

爬虫的基础依赖包

包含三个模块服务

### scraj-dedup

启动主类路径 com.hiekn.scraj.common.dedup.DedupNode

### scraj-meta

启动主类路径 com.hiekn.scraj.common.meta.MetaNode

### scraj-monitor

启动主类路径 com.hiekn.scraj.common.monitor.MonitorNode


## 配置项

服务调用通过RMI

三个服务绑定的端口在配置文件中设置

打包的时候打成一个scraj-common包，启动时指定不同的主类启动相关服务

启动的shell在bin目录下 run.sh
