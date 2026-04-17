package com.ilovecats4606.ADBPortDetector;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import java.util.Objects;

public class AdbPortDetector {

    private final Context context;
    private final String serviceType;
    private final AdbPortCallback callback;
    private final NsdManager nsdManager;
    private final NsdManager.DiscoveryListener discoveryListener;
    private boolean isDiscovering = false;
    private boolean isStarted = false;

    public AdbPortDetector(Context context, AdbPortCallback callback) {
        this.context = context;
        this.serviceType = "_adb-tls-connect._tcp";
        this.callback = callback;
        this.nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        this.discoveryListener = new DiscoveryListenerImpl();
    }

    public void startDiscovery() {
        if (isDiscovering) return;
        isDiscovering = true;
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void stopDiscovery() {
        if (isDiscovering) {
            isDiscovering = false;
            nsdManager.stopServiceDiscovery(discoveryListener);
        }
    }

    private class DiscoveryListenerImpl implements NsdManager.DiscoveryListener {

        @Override
        public void onDiscoveryStarted(String serviceType) {
            isStarted = true;
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            isStarted = false;
        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            nsdManager.resolveService(serviceInfo, new ResolveListenerImpl());
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
            // Handle service lost if needed
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            // Handle failure
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            // Handle failure
        }
    }

    private class ResolveListenerImpl implements NsdManager.ResolveListener {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Handle resolve failure
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            if (isDiscovering) {
                // Check if host is local and port is in use
                if (isLocalHost(serviceInfo.getHost()) && isPortInUse(serviceInfo.getPort())) {
                    callback.onPortDetected(serviceInfo.getHost(), serviceInfo.getPort());
                    stopDiscovery();
                }
            }
        }

        private boolean isLocalHost(java.net.InetAddress host) {
            try {
                java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    java.net.NetworkInterface iface = interfaces.nextElement();
                    java.util.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        java.net.InetAddress addr = addresses.nextElement();
                        if (addr.getHostAddress().equals(host.getHostAddress())) {
                            return true;
                        }
                    }
                }
            } catch (java.net.SocketException e) {
                e.printStackTrace();
            }
            return false;
        }

        private boolean isPortInUse(int port) {
            try {
                java.net.ServerSocket socket = new java.net.ServerSocket();
                socket.bind(new java.net.InetSocketAddress(port));
                socket.close();
                return false; // Port is free
            } catch (java.io.IOException e) {
                return true; // Port is in use
            }
        }
    }
}