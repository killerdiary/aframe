// IProxyControl.aidl
package com.hy.demo2;

// Declare any non-default types here with import statements

interface IProxyControl {
  	boolean start();

  	boolean stop();

  	boolean isRunning();

  	int getPort();
}
