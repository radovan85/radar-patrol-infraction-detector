package com.radovan.play.providers;

import com.google.inject.Provider;
import com.radovan.play.utils.NatsUtils;
import jakarta.inject.Inject;
import play.inject.ApplicationLifecycle;

import java.util.concurrent.CompletableFuture;

public class NatsUtilsProvider implements Provider<NatsUtils> {

    private final NatsUtils natsUtils;

    @Inject
    public NatsUtilsProvider(ApplicationLifecycle lifecycle) {
        this.natsUtils = new NatsUtils();
        this.natsUtils.init();

        // registrujemo shutdown hook
        lifecycle.addStopHook(() -> {
            natsUtils.closeConnection();
            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public NatsUtils get() {
        return natsUtils;
    }
}

