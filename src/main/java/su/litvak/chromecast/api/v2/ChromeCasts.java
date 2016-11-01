/*
 * Copyright 2014 Vitaly Litvak (vitavaque@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package su.litvak.chromecast.api.v2;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that discovers ChromeCast devices and holds references to all of them.
 */
public class ChromeCasts extends ArrayList<ChromeCast> {

    private static ChromeCasts INSTANCE = null;

    public static void setInstance(ChromeCasts instance) {
        INSTANCE = instance;
    }

    private List<ChromeCastsListener> listeners = new ArrayList<ChromeCastsListener>();
    private Context context;
    private NsdManager nsdManager;
    private NsdManager.DiscoveryListener discoveryListener;

    public ChromeCasts(Context context) {
        this.context = context;
    }

    private void _startDiscovery(InetAddress addr) throws IOException {
        nsdManager = (NsdManager) this.context.getSystemService(Context.NSD_SERVICE);
        discoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {

            }

            @Override
            public void onServiceFound(final NsdServiceInfo service) {
                nsdManager.resolveService(service, new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                        ChromeCast device = new ChromeCast(serviceInfo, serviceInfo.getServiceName());
                        add(device);
                        for (ChromeCastsListener listener : listeners) {
                            listener.newChromeCastDiscovered(device);
                        }
                    }
                });
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                if (ChromeCast.SERVICE_TYPE.equals(service.getServiceName())) {
                    // We have a ChromeCast device unregistering
                    List<ChromeCast> copy = new ArrayList<ChromeCast>(ChromeCasts.this);
                    ChromeCast deviceRemoved = null;
                    // Probably better keep a map to better lookup devices
                    for (ChromeCast device : copy) {
                        if (device.getName().equals(service.getServiceName())) {
                            deviceRemoved = device;
                            ChromeCasts.this.remove(device);
                            break;
                        }
                    }
                    if (deviceRemoved != null) {
                        for (ChromeCastsListener listener : listeners) {
                            listener.chromeCastRemoved(deviceRemoved);
                        }
                    }
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {

            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {

            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {

            }
        };

        nsdManager.discoverServices("_googlecast._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    private void _stopDiscovery() throws IOException {
        nsdManager.stopServiceDiscovery(discoveryListener);
    }

    /**
     * Starts ChromeCast device discovery
     */
    public static void startDiscovery() throws IOException {
        INSTANCE._startDiscovery(null);
    }
    
    /**
     * Starts ChromeCast device discovery
     *
     * @param addr the address of the interface that should be used for discovery
     */
    public static void startDiscovery(InetAddress addr) throws IOException {
        INSTANCE._startDiscovery(addr);
    }

    /**
     * Stops ChromeCast device discovery
     */
    public static void stopDiscovery() throws IOException {
        INSTANCE._stopDiscovery();
    }

    /**
     * Restarts discovery by sequentially calling 'stop' and 'start' methods
     */
    public static void restartDiscovery() throws IOException {
        stopDiscovery();
        startDiscovery(null);
    }
    
    /**
     * Restarts discovery by sequentially calling 'stop' and 'start' methods
     *
     * @param addr the address of the interface that should be used for discovery
     */
    public static void restartDiscovery(InetAddress addr) throws IOException {
        stopDiscovery();
        startDiscovery(addr);
    }

    /**
     * @return singleton container holding all discovered devices
     */
    public static ChromeCasts get() {
        return INSTANCE;
    }

    public static void registerListener(ChromeCastsListener listener) {
        if (listener != null) {
            INSTANCE.listeners.add(listener);
        }
    }

    public static void unregisterListener(ChromeCastsListener listener) {
        if (listener != null) {
            INSTANCE.listeners.remove(listener);
        }
    }
}
