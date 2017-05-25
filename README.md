# pluto

pluto�ǻ���Xtrabackup���ݻָ�������  
Xtrabackup��Percona��Դ��mysql���ݹ����ߣ�֧��ȫ�����ݣ��������ݺͲ��ֿ��ı���ȫ������֧�������ȱ�  
pluto�ṩ��ȫ�����������ݺͲ��ֱ��ݵĹ��ܣ�plutoĿǰʹ��ȫ�����ݺͻָ�ʵ�ֹ���  

# ��������
1.������� mvn clean package -Dmaven.test.skip=true  
2.��linux�½�ѹ����Ӧ��Ŀ¼���޸������ļ� cd pluto/conf  


  * pluto.conf�޸���������
  #tomcat�����˿�  
  pluto.server.port = 9092  
  #zk��ַ��dubbo�˿�  
  zookeeper.address = 127.0.0.1:2181  
  dubbo.port = 20880  
  #��ʱʱ��  
  backup.timeout = 600000  
  #����Ŀ¼�ͱ�����־  
  backup.dir=/backup  
  backup.log=/backup/backup.log  
  backup.log.bak=/backup/backup.log.bak  
  
  * backup-environment.json
  ` ``
  [
    {
      "instance": "mutual", //���ݿ�ʵ��(schema)
      "username": "root",
      "password": "your password",
      "port": 3306,
      "mysqlGroup": "amcuser", //mysql group
      "mysqlUser": "amcuser",  // mysql user
      "dataDir": "/data/mutual",  //mysqlʵ����Ӧ��dataĿ¼
      "dataBakDir": "/data/mutaul_bak",
      "xtrabackupMemory": "4G",
      "xtrabackupParallel": "8",
      "xtrabackupLogInfo": "xtrabackup_log_info",
      "defaultsFile":"/etc/my-mutual.cnf",  //mysql�����ļ�
      "startupCommand": "/etc/init.d/mysqld-mutual start", // mysql��������
      "shutdownCommand": "/etc/init.d/mysqld-mutual stop"  // mysql�ر�����
    },
    #��ʵ������
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
3.cd pluto/bin   ./pluto.sh (jpda����) start  
4.��½����̨ localhost:9092ע���½����ҳ����б��ݻָ�����ɾ��  
![image]()

# REST API
1. ȫ������
http://localhost:9092/pluto/full/backup?name=test&instance=xxx
2. �������� ���ֱ���(��֧��)
3. �ָ�
http://localhost:9092/pluto/restore?id=xxxx
4. ɾ��
http://localhost:9092/pluto/delete?ids=123,456
5. ��ѯ
http://localhost:9092/pluto/backups
6. ��ѯ���ݿ�ʵ������
http://localhost:9092/pluto/instances

# ע������
1. ���ݿ�Ȩ�ޣ�����Ŀ¼Ȩ�޺ͱ���Ŀ¼Ȩ��  
���磬��amcuser��½���������ݿ�ʹ��(amcuser:amcuser)��ʼ��������ʹamcuser������Ŀ¼�ͱ����ļ�Ŀ¼�ж�д��Ȩ��  

2. backup.log�����ɾ������ʹ��back.log.bak�ָ���������ı�����Ч
3. ���ڻָ����ȹر�������mysql��ע���������ݿ����ӳص�ʧЧɨ��������dbcpΪ������timeBetweenEvictionRunsMillis��
���߿���testOnBorrow

# xtrabackupʹ�÷�����
[wiki]()