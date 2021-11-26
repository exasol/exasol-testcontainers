package com.exasol.containers.workarounds;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for automatically applied workarounds for container version quirks.
 */
public class WorkaroundManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkaroundManager.class);
    private final Workaround[] workarounds;
    private final Set<String> previouslyAppliedWorkarounds;

    /**
     * Create a new instance of a {@link WorkaroundManager} with a list of workarounds it is responsible for.
     *
     * @param previouslyAppliedWorkarounds set of workarounds that have already been applied
     * @param workarounds                  list of workarounds the manager needs to check and apply
     * @return new workaround manager
     */
    public static WorkaroundManager create(final Set<String> previouslyAppliedWorkarounds,
            final Workaround... workarounds) {
        return new WorkaroundManager(previouslyAppliedWorkarounds, workarounds);
    }

    private WorkaroundManager(final Set<String> previouslyAppliedWorkarounds, final Workaround[] workarounds) {
        this.previouslyAppliedWorkarounds = previouslyAppliedWorkarounds;
        this.workarounds = workarounds;
    }

    /**
     * Apply workarounds.
     *
     * @return list of applied workarounds
     *
     * @throws WorkaroundException if any workaround could not be applied.
     */
    // [impl->dsn~workaround-manager-applies-multiple-of-workarounds~1]]
    public Set<Workaround> applyWorkarounds() throws WorkaroundException {
        final Set<Workaround> appliedWorkarounds = new HashSet<>();
        for (final Workaround workaround : this.workarounds) {
            if (applyWorkaround(workaround)) {
                appliedWorkarounds.add(workaround);
            }
        }
        if (!appliedWorkarounds.isEmpty() && LOGGER.isInfoEnabled()) {
            LOGGER.info("Applied workarounds: {}", joinWorkaroundNames(appliedWorkarounds));
        }
        return appliedWorkarounds;
    }

    // [impl->dsn~workaround-manager-checks-criteria~1]
    private boolean applyWorkaround(final Workaround workaround) throws WorkaroundException {
        if (isWorkaroundAlreadyApplied(workaround)) {
            LOGGER.trace("Workaround \"{}\" has previously been applied. Skipping application.", workaround.getName());
            return false;
        } else if (workaround.isNecessary()) {
            workaround.apply();
            return true;
        } else {
            return false;
        }
    }

    private boolean isWorkaroundAlreadyApplied(final Workaround workaround) {
        return this.previouslyAppliedWorkarounds.contains(workaround.getName());
    }

    private String joinWorkaroundNames(final Set<Workaround> workarounds) {
        return workarounds.stream().map(Workaround::getName).collect(Collectors.joining(", "));
    }
}