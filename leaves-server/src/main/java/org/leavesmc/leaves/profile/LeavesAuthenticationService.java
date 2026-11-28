package org.leavesmc.leaves.profile;

import com.destroystokyo.paper.profile.PaperServicesDiscoveryService;
import com.mojang.authlib.minecraft.SessionService;
import com.mojang.authlib.services.response.discovery.DiscoveryResponse;

import java.net.Proxy;
import java.util.function.Supplier;

// Leaves - 26.3 compat: authlib 10 replaced PaperAuthenticationService/MinecraftSessionService with
// PaperServicesDiscoveryService/SessionService (com.mojang.authlib.services). The Extra Yggdrasil feature
// patch that wires this class in is deferred for 26.3, so the discovery supplier is supplied by the caller
// (Paper's own PaperServicesDiscoveryService.create(proxy) builds the Mojang one).
public class LeavesAuthenticationService extends PaperServicesDiscoveryService {

    public LeavesAuthenticationService(Proxy proxy, boolean servicesKeySetEnabled, Supplier<DiscoveryResponse> discoverySupplier) {
        super(proxy, servicesKeySetEnabled, discoverySupplier);
    }

    public static LeavesAuthenticationService create(Proxy proxy, boolean servicesKeySetEnabled, Supplier<DiscoveryResponse> discoverySupplier) {
        return new LeavesAuthenticationService(proxy, servicesKeySetEnabled, discoverySupplier);
    }

    @Override
    public SessionService createMinecraftSessionService() {
        return new LeavesMinecraftSessionService(this.getServicesKeySet(), this.getProxy(), this);
    }
}
