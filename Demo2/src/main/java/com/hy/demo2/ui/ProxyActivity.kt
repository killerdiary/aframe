package com.hy.demo2.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.view.View
import android.widget.Toast
import com.hy.demo2.IProxyControl
import com.hy.demo2.R
import com.hy.frame.bean.MenuInfo
import com.hy.frame.util.MyLog
import me.dawson.proxyserver.ui.ProxyService


/**
 * 代理测试
 */
class ProxyActivity : MenuActivity(), ServiceConnection {
    private var isBreak: Boolean = false
    private var proxyControl: IProxyControl? = null

    override fun initData() {
        intent.putExtra(BUNDLE, MenuActivity.newArguments(R.xml.menu_proxy, R.string.menu_proxy))
        super.initData()
        getTemplateControl()?.getHeaderLeft()?.visibility = View.GONE
    }

    override fun onViewClick(v: View, item: MenuInfo, position: Int) {
        when (position + 1) {
            1 -> {
                //开启
                isBreak = false
                updateProxy()
            }
            2 -> {
                //关闭
                isBreak = true
                updateProxy()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, ProxyService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(this)
    }

    override fun onServiceConnected(cn: ComponentName, binder: IBinder) {
        proxyControl = binder as IProxyControl
        if (proxyControl != null) {
            updateProxy()
        }
    }

    override fun onServiceDisconnected(cn: ComponentName) {
        proxyControl = null
    }

    private fun updateProxy() {
        if (proxyControl == null) {
            return
        }

        var isRunning = false
        try {
            isRunning = proxyControl!!.isRunning
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        val shouldRun = !isBreak

        if (shouldRun && !isRunning) {
            startProxy()
        } else if (!shouldRun && isRunning) {
            stopProxy()
        }

        try {
            isRunning = proxyControl!!.isRunning
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        if (isRunning) {
            MyLog.d(javaClass, getString(R.string.proxy_on))
//            tvInfo.setText(R.string.proxy_on)
//            cbEnable.setChecked(true)
        } else {
//            tvInfo.setText(R.string.proxy_off)
//            cbEnable.setChecked(false)
            MyLog.d(javaClass, getString(R.string.proxy_off))
        }
    }

    private fun startProxy() {
        var started = false
        if (proxyControl == null) {
            return
        }
        try {
            started = proxyControl!!.start()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        if (!started) {
            return
        }


        Toast.makeText(this, resources.getString(R.string.proxy_started), Toast.LENGTH_SHORT).show()
    }

    private fun stopProxy() {
        var stopped = false
        if (proxyControl == null) {
            return
        }
        try {
            stopped = proxyControl!!.stop()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        if (!stopped) {
            return
        }

        //tvInfo.setText(R.string.proxy_off)

        Toast.makeText(this, resources.getString(R.string.proxy_stopped), Toast.LENGTH_SHORT).show()
    }


//    private fun startProxy() {
//        Thread(Runnable {
//            try {
//                val serverSocket = ServerSocket(8888) //这里随机选择了一个端口，需与proxydroid中设置的端口一致
//                MyLog.d(ProxyActivity::class.java, "Port=" + serverSocket.localPort)
//                while (true) {
//                    val socket = serverSocket.accept()//若获取不到会一直阻塞
//                    Thread(ServerThread(socket)).start()//触发服务器线程
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }).start()
//    }


//    inner class ServerThread(private val socket: Socket?) : Runnable {
//        private val TAG = this.javaClass.name
//        private val BUFF_SIZE = 1024 * 100
//
//        override fun run() {
//            try {
//                val innerInputStream = socket!!.getInputStream()
//                val innerOutputStream = socket.getOutputStream()
//                val buff = ByteArray(BUFF_SIZE)
//                var rc: Int
//                var byteArrayOutputStream: ByteArrayOutputStream?
//
//                /**
//                 * client会向proxy发送510，所以这里执行的结果是buff={5,1,0}
//                 * Caution: 这里不能跟下面的innerInputStream.read(buff, 0, 10);合并成innerInputStream.read(buff, 0, 13);
//                 * 我试过，大部分情况没影响，但是偶尔会出现重大bug（读不出外网ip），至于原因暂不详
//                 * 看来这种input和output类型的操作还是稳重一点，不要太心急
//                 */
//                innerInputStream.read(buff, 0, 3)
//
//                /**
//                 * proxy向client发送应答{5,0}
//                 */
//                val firstAckMessage = byteArrayOf(5, 0)
//                val secondAckMessage = ByteArray(10)
//                innerOutputStream.write(firstAckMessage)
//                innerOutputStream.flush()
//
//                /**
//                 * client发送命令5101+目的地址（4Bytes）+目的端口（2Bytes)
//                 * 即{5,1,0,1,IPx1,IPx2,IPx3,IPx4,PORTx1,PORTx2} 一共10位
//                 * 例如发送给52.88.216.252服务器的80端口，那么这里buff就是{5,1,0,1,52,88,-40,-4,0,80}（这里每位都是byte，所以在-128~127之间，可以自己换算成0~255）
//                 */
//                innerInputStream.read(buff, 0, 10)
//
//                val IP = byte2Int(buff[4]).toString() + "." + byte2Int(buff[5]) + "." + byte2Int(buff[6]) + "." + byte2Int(buff[7])
//                val port = byte2Int(buff[8]) * 256 + byte2Int(buff[9])
//
//                MyLog.e("ServerThread", "Connected to $IP:$port")
//                val outerSocket = Socket(IP, port)
//                val outerInputStream = outerSocket.getInputStream()
//                val outerOutputStream = outerSocket.getOutputStream()
//
//                /**
//                 * proxy 向 client 返回应答5+0+0+1+因特网套接字绑定的IP地址（4字节的16进制表示）+因特网套接字绑定的端口号（2字节的16进制表示）
//                 */
//                var ip1 = ByteArray(4)
//                var port1 = 0
//                ip1 = outerSocket.localAddress.address
//                port1 = outerSocket.localPort
//
//                secondAckMessage[0] = 5
//                secondAckMessage[1] = 0
//                secondAckMessage[2] = 0
//                secondAckMessage[3] = 1
//                secondAckMessage[4] = ip1[0]
//                secondAckMessage[5] = ip1[1]
//                secondAckMessage[6] = ip1[2]
//                secondAckMessage[7] = ip1[3]
//                secondAckMessage[8] = (port1 shr 8).toByte()
//                secondAckMessage[9] = (port1 and 0xff).toByte()
//                innerOutputStream.write(secondAckMessage, 0, 10)
//                innerOutputStream.flush()
//
//                /**
//                 * 应答线程：从外网不断读数据发到client
//                 */
//                val responseThread = SocksResponseThread(outerInputStream, innerOutputStream)
//                responseThread.start()
//
//                /**
//                 * 本线程：从client不断读数据发到外网
//                 */
//                byteArrayOutputStream = ByteArrayOutputStream()
//                rc = innerInputStream.read(buff, 0, BUFF_SIZE)
//                while (rc > 0) {
//                    outerOutputStream.write(buff, 0, rc)
//                    byteArrayOutputStream.write(buff, 0, rc)
//                    outerOutputStream.flush()
//                    rc = innerInputStream.read(buff, 0, BUFF_SIZE)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                try {
//                    socket?.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//            }
//
//        }
//
//        fun byte2Int(b: Byte): Int {
//            return b.toInt() and 0xff
//        }
//
//    }
//
//    inner class SocksResponseThread(private val `in`: InputStream, private val out: OutputStream) : Thread() {
//        private val BUFF_SIZE = 1024 * 100
//
//        override fun run() {
//            var readbytes = 0
//            val buf = ByteArray(BUFF_SIZE)
//            while (true) {
//                try {
//                    if (readbytes == -1) break
//                    readbytes = `in`.read(buf, 0, BUFF_SIZE)
//                    if (readbytes > 0) {
//                        out.write(buf, 0, readbytes)
//                    }
//                    out.flush()
//                } catch (e: Exception) {
//                    break
//                }
//
//            }
//        }
//    }
}
