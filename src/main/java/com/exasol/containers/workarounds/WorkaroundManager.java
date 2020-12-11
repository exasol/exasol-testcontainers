package com.exasol.containers.workarounds;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkaroundManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkaroundManager.class);
    private final Workaround[] workarounds;

    /**
     * Create a new instance of a {@link WorkaroundManager} with a list of workarounds it is responsible for.
     *
     * @param workarounds list of workarounds the manager needs to check and apply
     * @return new workaround manager
     */
    public static WorkaroundManager create(final Workaround... workarounds) {
        return new WorkaroundManager(workarounds);
    }

    private WorkaroundManager(final Workaround[] workarounds) {
        this.workarounds = workarounds;
    }

    /**
     * Apply workarounds.
     *
     * @throws WorkaroundException if any workaround could not be applied.
     */
    public void applyWorkarounds() throws WorkaroundException {
        final List<Workaround> appliedWorkarounds = new ArrayList<>();
        for (final Workaround workaround : this.workarounds) {
            applyWorkaround(workaround);
            appliedWorkarounds.add(workaround);
        }
        if (!appliedWorkarounds.isEmpty() && LOGGER.isInfoEnabled()) {
            LOGGER.info("Applied workarounds: {}", joinWorkaroundNames(appliedWorkarounds));
        }
    }

    private String joinWorkaroundNames(final List<Workaround> workarounds) {
        return workarounds.stream().map(Workaround::getName).collect(Collectors.joining(", "));
    }

    private void applyWorkaround(final Workaround workaround) throws WorkaroundException {
        if (workaround.isNecessary()) {
            workaround.apply();
        }
    }
}