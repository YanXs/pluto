# pluto

pluto是基于Xtrabackup备份恢复管理工具  
Xtrabackup是Percona开源的mysql备份管理工具，支持全量备份，增量备份和部分库表的备份全，并且支持在线热备  
pluto提供了全备、增量备份和部分备份的功能，pluto目前使用全量备份和恢复实现功能  

# 启动方法
1.打包编译 mvn clean package -Dmaven.test.skip=true  
2.在linux下解压到相应的目录，修改配置文件 cd pluto/conf  


  * pluto.conf修改如下内容
  #tomcat监听端口  
  pluto.server.port = 9092  
  #zk地址和dubbo端口  
  zookeeper.address = 127.0.0.1:2181  
  dubbo.port = 20880  
  #超时时间  
  backup.timeout = 600000  
  #备份目录和备份日志  
  backup.dir=/backup  
  backup.log=/backup/backup.log  
  backup.log.bak=/backup/backup.log.bak  
  
  * backup-environment.json
  ` ``
  [
    {
      "instance": "mutual", //数据库实例(schema)
      "username": "root",
      "password": "your password",
      "port": 3306,
      "mysqlGroup": "amcuser", //mysql group
      "mysqlUser": "amcuser",  // mysql user
      "dataDir": "/data/mutual",  //mysql实例对应的data目录
      "dataBakDir": "/data/mutaul_bak",
      "xtrabackupMemory": "4G",
      "xtrabackupParallel": "8",
      "xtrabackupLogInfo": "xtrabackup_log_info",
      "defaultsFile":"/etc/my-mutual.cnf",  //mysql配置文件
      "startupCommand": "/etc/init.d/mysqld-mutual start", // mysql启动命令
      "shutdownCommand": "/etc/init.d/mysqld-mutual stop"  // mysql关闭命令
    },
    #多实例配置
    {
      "instance": "hxzbta",
      "username": "root",
      "password": "your password",
      "port": 3307,
      "mysqlGroup": "amcuser",
      "mysqlUser": "amcuser",
      "dataDir": "/data/hxzbta",
      "dataBakDir": "/data/hxzbta_bak",
      "xtrabackupMemory": "4G",
      "xtrabackupParallel": "8",
      "xtrabackupLogInfo": "xtrabackup_log_info",
      "defaultsFile":"/etc/my-hxzbta.cnf",
      "startupCommand": "/etc/init.d/mysqld-hxzbta start",
      "shutdownCommand": "/etc/init.d/mysqld-hxzbta stop"
    }
  ]
  `
3.cd pluto/bin   ./pluto.sh (jpda调试) start  
4.登陆控制台 localhost:9092注册登陆到主页面进行备份恢复或者删除  
![image]()

# REST API
1. 全量备份
http://localhost:9092/pluto/full/backup?name=test&instance=xxx
2. 增量备份 部分备份(不支持)
3. 恢复
http://localhost:9092/pluto/restore?id=xxxx
4. 删除
http://localhost:9092/pluto/delete?ids=123,456
5. 查询
http://localhost:9092/pluto/backups
6. 查询数据库实例名称
http://localhost:9092/pluto/instances

# 注意事项
1. 数据库权限，数据目录权限和备份目录权限  
例如，以amcuser登陆，建议数据库使用(amcuser:amcuser)初始化，并且使amcuser对数据目录和备份文件目录有读写的权限  

2. backup.log如果误删，可以使用back.log.bak恢复，但最近的备份无效
3. 由于恢复会先关闭再重启mysql，注意配置数据库连接池的失效扫描间隔，以dbcp为例配置timeBetweenEvictionRunsMillis，
或者开启testOnBorrow

# xtrabackup使用方法：
[wiki]()