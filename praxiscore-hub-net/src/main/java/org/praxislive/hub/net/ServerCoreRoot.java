/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2020 Neil C Smith.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this work; if not, see http://www.gnu.org/licenses/
 * 
 * 
 * Please visit https://www.praxislive.org if you need additional information or
 * have any questions.
 */
package org.praxislive.hub.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.praxislive.core.Call;
import org.praxislive.core.Clock;
import org.praxislive.core.ComponentAddress;
import org.praxislive.core.ControlAddress;
import org.praxislive.core.ExecutionContext;
import org.praxislive.core.PacketRouter;
import org.praxislive.core.Root;
import org.praxislive.core.services.LogService;
import org.praxislive.core.services.RootManagerService;
import org.praxislive.core.services.Service;
import org.praxislive.core.types.PMap;
import org.praxislive.core.types.PResource;
import org.praxislive.hub.Hub;
import org.praxislive.internal.osc.OSCListener;
import org.praxislive.internal.osc.OSCMessage;
import org.praxislive.internal.osc.OSCPacket;
import org.praxislive.internal.osc.OSCServer;

/**
 *
 */
class ServerCoreRoot extends NetworkCoreRoot {

    private final static Logger LOG = Logger.getLogger(ServerCoreRoot.class.getName());
    private final String SERVER_SYS_PREFIX = "/_remote";
    
    private final InetSocketAddress address;
    private final CIDRUtils clientValidator;
    private final PraxisPacketCodec codec;
    private final Dispatcher dispatcher;
    private final ResourceResolver resourceResolver;

    private OSCServer server;
    private SocketAddress master;
    private long lastPurgeTime;
    private URI remoteUserDir;
    private URI remoteFileServer;
    private CompletableFuture<NetworkCoreFactory.Info> futureInfo;

    ServerCoreRoot(Hub.Accessor hubAccess,
            List<Root> exts,
            List<Class<? extends Service>> services,
            ChildLauncher childLauncher,
            HubConfiguration configuration,
            InetSocketAddress address,
            CIDRUtils clientValidator,
            CompletableFuture<NetworkCoreFactory.Info> futureInfo) {
        super(hubAccess, exts, services, childLauncher, configuration);
        this.address = address;
        this.clientValidator = clientValidator;
        this.codec = new PraxisPacketCodec();
        this.dispatcher = new Dispatcher(codec);
        this.resourceResolver = new ResourceResolver();
        this.futureInfo = futureInfo;
    }

    @Override
    protected void starting() {
        try {
            server = OSCServer.newUsing(codec, OSCServer.TCP, address);
            server.setBufferSize(65536);
            server.addOSCListener(new OSCListener() {

                @Override
                public void messageReceived(final OSCMessage msg, final SocketAddress sender, final long time) {
                    invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            ServerCoreRoot.this.messageReceived(msg, sender, time);
                        }
                    });
                }
            });
            server.start();
            if (futureInfo != null) {
                futureInfo.complete(new NetworkCoreFactory.Info(server.getLocalAddress()));
                futureInfo = null;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerCoreRoot.class.getName()).log(Level.SEVERE, null, ex);
            if (futureInfo != null) {
                futureInfo.completeExceptionally(ex);
                futureInfo = null;
            }
            forceTermination();
            throw new RuntimeException(ex);
        }
        getLookup().find(ExecutionContext.class)
                .orElseThrow(IllegalStateException::new)
                .addClockListener(new ExecutionContext.ClockListener() {

                    @Override
                    public void tick(ExecutionContext source) {
                        ServerCoreRoot.this.tick(source);
                    }
                });
        super.starting();
    }

    @Override
    protected void terminating() {
        super.terminating();
        try {
            if (server != null) {
                server.stop();
                server.dispose();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "", ex);
        } finally {
            server = null;
        }
    }

    @Override
    protected void processCall(Call call, PacketRouter router) {
        if (call.to().component().equals(getAddress())) {
            super.processCall(call, router);
        } else {
            dispatcher.handleCall(call);
        }
    }
    
    

    PResource.Resolver getResourceResolver() {
        return resourceResolver;
    }

    private void tick(ExecutionContext source) {
        if ((source.getTime() - lastPurgeTime) > TimeUnit.SECONDS.toNanos(1)) {
//            LOG.fine("Triggering dispatcher purge");
            dispatcher.purge(10, TimeUnit.SECONDS);
            lastPurgeTime = source.getTime();
        }
    }

    private void messageReceived(OSCMessage msg, SocketAddress sender, long time) {
        if (master == null || !master.equals(sender)) {
            if (!"/HLO".equals(msg.getName())) {
                LOG.log(Level.WARNING, "Received unexpected message from {0}", sender);
                return;
            }
            // otherwise fall through and handle HLO
        }
        switch (msg.getName()) {
            case "/HLO":
                handleHLO(sender, msg);
                break;
            case "/BYE":
                master = null;
                forceTermination();
                break;
            default:
                dispatcher.handleMessage(msg, time);

        }
    }

    private void handleHLO(SocketAddress sender, OSCMessage msg) {
        if (validate(sender) && handleHLOParams((InetSocketAddress) sender, msg)) {
            master = sender;
            try {
                server.send(new OSCMessage("/HLO", new Object[]{"OK"}), sender);
            } catch (IOException ex) {
                Logger.getLogger(ServerCoreRoot.class.getName()).log(Level.SEVERE, null, ex);
                master = null;
            }
        }
    }

    private boolean validate(SocketAddress sender) {
        if (clientValidator == null) {
            // server forced local only
            return true;
        }
        if (sender instanceof InetSocketAddress) {
            InetSocketAddress inet = (InetSocketAddress) sender;
            try {
                return clientValidator.isInRange(inet.getHostString());
            } catch (UnknownHostException ex) {
                Logger.getLogger(ServerCoreRoot.class.getName()).log(Level.SEVERE, null, ex);
                // fall through
            }
        }
        return false;
    }

    private boolean handleHLOParams(InetSocketAddress sender, OSCMessage msg) {
        if (msg.getArgCount() < 1) {
            return true; // assume defaults???
        }
        try {
            PMap params = PMap.parse(msg.getArg(0).toString());
            String masterUserDir = params.getString(Utils.KEY_MASTER_USER_DIRECTORY, null);
            if (masterUserDir != null) {
                remoteUserDir = URI.create(masterUserDir);
            }
            
            PMap services = PMap.parse(params.getString(Utils.KEY_REMOTE_SERVICES, ""));
            if (!services.isEmpty()) {
                for (String serviceName : services.keys()) {
                    // @TODO temporary workaround for v4 - v5 change
                    Class<? extends Service> service;
                    if ("org.praxislive.logging.LogService".equals(serviceName)) {
                        service = LogService.class;
                    } else {
                        service = (Class<? extends Service>)
                                Class.forName(serviceName, true,
                                Thread.currentThread().getContextClassLoader());
                    }
                    ComponentAddress serviceAddress = ComponentAddress.of(
                            SERVER_SYS_PREFIX + services.getString(serviceName, null));
                    getHubAccessor().registerService(service, serviceAddress);
                }
            }
            
            int fileServerPort = params.getInt(Utils.KEY_FILE_SERVER_PORT, 0);
            if (fileServerPort > 0) {
                remoteFileServer = URI.create("http://" + sender.getAddress().getHostAddress() + ":" + fileServerPort);
            }
            
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ServerCoreRoot.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private class Dispatcher extends OSCDispatcher {

        private Dispatcher(PraxisPacketCodec codec) {
            super(codec, new Clock() {
                @Override
                public long getTime() {
                    return getExecutionContext().getTime();
                }
            });
        }

        @Override
        void send(OSCPacket packet) {
            try {
                server.send(packet, master);
            } catch (IOException ex) {
                Logger.getLogger(ServerCoreRoot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        void send(Call call) {
            getRouter().route(call);
        }

        @Override
        String getRemoteSysPrefix() {
            return SERVER_SYS_PREFIX;
        }

        @Override
        ControlAddress getAddRootAddress() {
            return ControlAddress.of(getAddress(), RootManagerService.ADD_ROOT);
        }

        @Override
        ControlAddress getRemoveRootAddress() {
            return ControlAddress.of(getAddress(), RootManagerService.REMOVE_ROOT);
        }

    }

    private class ResourceResolver implements PResource.Resolver {

        @Override
        public List<URI> resolve(PResource resource) {
            URI dir = remoteUserDir;
            URI srv = remoteFileServer;
            URI res = resource.value();
            if (dir == null && srv == null) {
                return Collections.singletonList(res);
            }

            if (!"file".equals(res.getScheme())) {
                return Collections.singletonList(res);
            }
            
            List<URI> uris = new ArrayList<>(2);
            
            if (dir != null) {
                uris.add(Utils.getUserDirectory().toURI().resolve(dir.relativize(res)));
            }
            
            if (srv != null) {
                uris.add(srv.resolve(res.getRawPath()));
            }
            
            return uris;
            
        }

    }

}
