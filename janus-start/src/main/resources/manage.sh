#!/bin/bash
source /etc/profile
ulimit -s 20480
ulimit -c unlimited
export PATH=$PATH:/usr/sbin

PRG="$0"
PRGDIR=`dirname "$PRG"`
BASEDIR=`cd "$PRGDIR/.." >/dev/null; pwd`

USAGE()
{
  echo "usage: $0 start|stop|restart|offline|online"
}

if [ $# -lt 1 ]; then
  USAGE
  exit -1
fi

CMD="$1"
shift

OPTS=$*;

janusProcessNum(){
    echo `ps -ef | grep -v grep | grep -i "janus"|grep java|wc -l`;
}

signal(){
    for pid in `ps -ef | grep -v grep | grep -i "janus"|grep java | awk '{print $2}'`
    do
        echo "signal janus : ${pid} -> $1"
        kill -s $1 $pid
    done
}

stopJanus()
{
    num=`janusProcessNum`
    if [ $num -eq 0 ];then
        echo "janus already stop"
        return 0
    else
        echo "$num janus process running,stop now..."
    fi

    for pid in `ps -ef | grep -v grep | grep -i "janus"|grep java | awk '{print $2}'`
    do
        echo "stop janus : ${pid}"
        kill $pid
    done
    echo -n "waiting 60s(max)"
    waitRemain=60
    while [ ${waitRemain} -gt 0 -a `janusProcessNum` -ne 0 ]
    do
        sleep 1
        echo -n "."
        waitRemain=`expr ${waitRemain} - 1`
    done
    echo ""
    if [ `janusProcessNum` -ne 0 ];then
        for pid in `ps -ef | grep -v grep | grep -i "janus"|grep java | awk '{print $2}'`
        do
            echo "stop fail,force stop janus : ${pid}"
            kill -9 $pid
        done
    fi
    echo "janus stopped."
}

startJanus()
{
    num=`janusProcessNum`
    if [ $num -ne 0 ];then
        echo "janus already started,stop it first"
        exit -1
    fi
    echo "start janus..."
    nohup ./janus $OPTS > /dev/null  &
    pid=$!
    echo "waiting 5s"
    sleep 5
    if [ `janusProcessNum` -eq 0 ];then
        echo "ERROR: janus start failed,pls check application log"
        exit -1;
    fi
    echo "janus start success.pid $pid"
    exit 0
}

case "$CMD" in
  stop) stopJanus;;
  start) startJanus;;
  restart) stopJanus;sleep 3;startJanus;;
  offline) signal SIGBUS;;
  online) signal SIGUSR2;;
  help) USAGE;;
  *) USAGE;;
esac