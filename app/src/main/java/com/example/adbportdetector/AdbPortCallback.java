package com.ilovecats4606.ADBPortDetector;

import java.net.InetAddress;

public interface AdbPortCallback {
    void onPortDetected(InetAddress host, int port);
}