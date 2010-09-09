#!/bin/bash
if [ $# -ne 2 ];then
        echo "usage: sshxcute_test.sh username password"
        exit 1
fi
export USERNAME=$1
export PASSWORD=$2

if [ "$USERNAME" = "hello" -a "$PASSWORD" = "world" ];then
	echo "Login success"
	exit 0
fi
echo "Login falied"
exit 2