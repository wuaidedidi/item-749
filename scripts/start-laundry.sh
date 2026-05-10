#!/usr/bin/env bash
set -euo pipefail

echo "[laundry] starting all-in-one runtime"
echo "[laundry] exposed ports: frontend 3000, backend 8080, mysql 3306"

install -d -o mysql -g mysql /run/mysqld /var/run/mysqld

if [ ! -d /var/lib/mysql/mysql ]; then
  echo "[laundry] initializing mariadb data directory"
  mariadb-install-db --user=mysql --datadir=/var/lib/mysql --auth-root-authentication-method=normal >/tmp/mariadb-install.log
fi

mysqld_safe --datadir=/var/lib/mysql --bind-address=0.0.0.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --skip-networking=0 &

for _ in $(seq 1 60); do
  if mysqladmin ping -uroot --silent >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

if ! mysqladmin ping -uroot --silent >/dev/null 2>&1; then
  echo "[laundry] mariadb failed to start" >&2
  exit 1
fi

mysql -uroot -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root123456'; CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED BY 'root123456'; CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'root123456'; GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION; GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; FLUSH PRIVILEGES;"

mysql -h 127.0.0.1 -uroot -proot123456 --default-character-set=utf8mb4 < /app/database/init.sql
echo "[laundry] database ready on 3306"

touch /usr/local/tomcat/logs/catalina.out /var/log/nginx/access.log /var/log/nginx/error.log
cd /usr/local/tomcat
catalina.sh start
nginx

echo "[laundry] tomcat backend ready on 8080"
echo "[laundry] nginx frontend ready on 3000"
echo "[laundry] open http://localhost:3000 or http://localhost:8000/api/health when mapped with -p 8000:8080"

tail -F /usr/local/tomcat/logs/catalina.out /var/log/nginx/access.log /var/log/nginx/error.log
