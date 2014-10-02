package net.qiujuer.sample;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.command.Command;
import net.qiujuer.genius.nettool.DnsResolve;
import net.qiujuer.genius.nettool.Ping;
import net.qiujuer.genius.nettool.SpeedRoad;
import net.qiujuer.genius.nettool.Telnet;
import net.qiujuer.genius.nettool.TraceRoute;
import net.qiujuer.genius.util.HashUtils;
import net.qiujuer.genius.util.Log;
import net.qiujuer.genius.util.ToolUtils;


/**
 * Created by QiuJu
 * on 2014/10/2.
 * 测试单元
 */
public class TestCase {
    private static final String TAG = TestCase.class.getSimpleName();

    /**
     * 日志测试
     */
    public void testLog() {
        //是否调用系统Android Log，发布时可设置为false
        Log.setCallLog(true);

        //清理存储的文件
        Log.clearLogFile();

        //是否开启写入文件，存储最大文件数量，单个文件大小（Mb），重定向地址（默认包名目录）
        Log.setSaveLog(Genius.getApplication(), true, 10, 1, null);

        //设置是否监听外部存储插入操作
        //开启时插入外部设备（SD）时将拷贝存储的日志文件到外部存储设备
        //此操作依赖于是否开启写入文件功能，未开启则此方法无效
        //是否开启，SD卡目录
        Log.setCopyExternalStorage(true, "Test/Logs");

        //设置日志等级
        //VERBOSE为5到ERROR为1依次递减
        Log.setLevel(Log.ALL);

        Log.v(TAG, "测试日志 VERBOSE 级别。");
        Log.d(TAG, "测试日志 DEBUG 级别。");
        Log.i(TAG, "测试日志 INFO 级别。");
        Log.w(TAG, "测试日志 WARN 级别。");
        Log.e(TAG, "测试日志 ERROR 级别。");

        Log.setLevel(Log.INFO);
        Log.v(TAG, "二次测试日志 VERBOSE 级别。");
        Log.d(TAG, "二次测试日志 DEBUG 级别。");
        Log.i(TAG, "二次测试日志 INFO 级别。");
        Log.w(TAG, "二次测试日志 WARN 级别。");
        Log.e(TAG, "二次测试日志 ERROR 级别。");

        Log.setLevel(Log.ALL);
    }

    /**
     * 测试命令行执行
     */
    public void testCommand() {
        //同步
        Thread thread = new Thread() {
            public void run() {
                //调用方式与ProcessBuilder传参方式一样
                Command command = new Command("/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");
                //同步方式执行
                String res = Command.command(command);
                Log.i(TAG, "Command 同步：" + res);
            }
        };
        thread.setDaemon(true);
        thread.start();

        //异步
        Command command = new Command("/system/bin/ping",
                "-c", "4", "-s", "100",
                "www.baidu.com");

        //异步方式执行
        //采用回调方式，无需自己建立线程
        //传入回调后自动采用此种方式
        Command.command(command, new Command.CommandListener() {
            @Override
            public void onCompleted(String str) {
                Log.i(TAG, "Command onCompleted：\n" + str);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Command onCancel");
            }

            @Override
            public void onError() {
                Log.i(TAG, "Command onError");
            }
        });
    }

    /**
     * 测试MD5
     */
    public void testHashUtils() {
        Log.i(TAG, "QIUJUER的MD5值为：" + HashUtils.getStringMd5("QIUJUER"));
        //文件MD5不做演示，传入file类即可
    }

    /**
     * 测试MD5
     */
    public void testToolUtils() {
        Log.i(TAG, "getAndroidId：" + ToolUtils.getAndroidId(Genius.getApplication()));
        Log.i(TAG, "getDeviceId：" + ToolUtils.getDeviceId(Genius.getApplication()));
        Log.i(TAG, "getSerialNumber：" + ToolUtils.getSerialNumber());
        Log.i(TAG, "安装（net.qiujuer.sample）：" + ToolUtils.isAvailablePackage(Genius.getApplication(), "net.qiujuer.sample"));
    }

    /**
     * 基本网络功能测试
     */
    public void testNetTool() {
        //所有目标都可为IP地址
        Thread thread = new Thread() {
            public void run() {
                //包数，包大小，目标，是否解析IP
                Ping ping = new Ping(4, 32, "www.baidu.com", true);
                ping.start();
                Log.i(TAG, "Ping：" + ping.toString());
                //目标，可指定解析服务器
                DnsResolve dns = new DnsResolve("www.baidu.com");
                dns.start();
                Log.i(TAG, "DnsResolve：" + dns.toString());
                //目标，端口
                Telnet telnet = new Telnet("www.baidu.com", 80);
                telnet.start();
                Log.i(TAG, "Telnet：" + telnet.toString());
                //目标
                TraceRoute traceRoute = new TraceRoute("www.baidu.com");
                traceRoute.start();
                Log.i(TAG, "TraceRoute：" + traceRoute.toString());

                //测速
                //下载目标，下载大小
                SpeedRoad speedRoad = new SpeedRoad("http://down.360safe.com/se/360se_setup.exe", 1024 * 32);
                speedRoad.start();
                Log.i(TAG, "SpeedRoad：" + speedRoad.getSpeed());
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
