#!/bin/bash
#一、制作zabbix yum库
yum update -y
yum install redhat-lsb wget links createrepo -y

Z_VER=2.2
PLATFORM=$(uname -m)
case $PLATFORM in
 i?86) PLATFORM=i386;;
 x86_64|amd64) PLATFORM=x86_64;
esac
OS_REL=$(lsb_release -r | awk '{print $NF}')
OS_REL_M=$(echo "$OS_REL" |awk -F. '{print $1}')
OS_DIST=$(lsb_release -i | awk '{print tolower($NF)}')

case $OS_DIST in
  centos*|redhat*) DISTRO_TYPE=rhel ;;
esac

REPO_NAME="Zabbix $Z_VER for $DISTRO_TYPE $OS_REL_M $PLATFORM based systems"
mkdir -p /var/zabbix
cd /var/zabbix
rm -rf ./*
wget -q -nd -r -l 1 -L -X '' -X ..,Parent\ Directory,index\*  http://repo.zabbix.com/zabbix/$Z_VER/$DISTRO_TYPE/$OS_REL_M/${PLATFORM}/
EXTRA_PACKAGES=$(links -dump http://dl.fedoraproject.org/pub/epel/$OS_REL_M/$PLATFORM/ |grep -E "/iksemel|/fping" |awk '{printf("%s ",$NF)}')
wget -q -nd -r -l 1 -L -X '' -X ..,Parent\ Directory,index\*  $EXTRA_PACKAGES
rm -f index.html*
createrepo -S --content "$REPO_NAME" --repo zabbix .

#二、安装Zabbix
cat << EOF > /etc/yum.repos.d/Zabbix.repo
# Name: Zabbix RPM Repository
# URL: http://repo.zabbix.com/zabbix/$Z_VER/$DISTRO_TYPE/$OS_REL_M/${PLATFORM}/
[zabbix]
name = $REPO_NAME
#baseurl=file:///var/zabbix
baseurl=http://repo.zabbix.com/zabbix/2.2/rhel/6/x86_64/
gpgcheck=0
enabled=1
EOF

yum install -y http://dl.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm
yum install mysql-server zabbix-server-mysql zabbix-web-mysql zabbix-agent zabbix-java-gateway -y
yum install zabbix-get zabbix-java-gateway zabbix-sender -y
yum install net-snmp* -y

service mysqld restart
mysql_secure_installation
mysql -u root -p
create database zabbix character set utf8;
grant all privileges on *.* to 'zabbix'@'localhost' identified by 'zabbix';
grant all privileges on *.* to 'zabbix'@'%' identified by 'zabbix';
grant all privileges on *.* to 'root'@'%' identified by 'root';
grant all privileges on *.* to 'root'@'localhost' identified by 'root';
flush privileges;
use zabbix;
source /usr/share/doc/zabbix-server-mysql-2.2.4/create/schema.sql
source /usr/share/doc/zabbix-server-mysql-2.2.4/create/images.sql
source /usr/share/doc/zabbix-server-mysql-2.2.4/create/data.sql
exit

sed -i 's/post_max_size = 8M/post_max_size = 16M/g' /etc/php.ini
sed -i 's/max_execution_time = 30/max_execution_time = 300/g' /etc/php.ini
sed -i 's/max_input_time = 60/max_input_time = 300/g' /etc/php.ini
sed -i 's/;date.timezone =/date.timezone =Asia\/Shanghai/g' /etc/php.ini
sed -i 's/www.example.com:80/192.168.137.150:80/g' /etc/httpd/conf/httpd.conf
sed -i 's/#ServerName/ServerName/g' /etc/httpd/conf/httpd.conf
service httpd restart


chkconfig zabbix-server on
chkconfig zabbix-agent on
chkconfig mysqld on
chkconfig httpd on

service mysqld restart
service zabbix-server restart
service zabbix-agent restart
service httpd restart
