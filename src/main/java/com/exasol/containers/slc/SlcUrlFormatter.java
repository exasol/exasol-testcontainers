package com.exasol.containers.slc;

/**
 * This class formats the URL for the Script Language Container (SLC) configuration.
 */
class SlcUrlFormatter {

    /**
     * Format the URL for the given Script Language Container (SLC).
     * <p>
     * This assumes that the SLC is already unpacked to {@code /bfsdefault/default/} and has the given name.
     * 
     * @param slc                   the Script Language Container
     * @param unpackedContainerName the name of the unpacked container
     * @return the formatted URL
     */
    String format(final ScriptLanguageContainer slc, final String unpackedContainerName) {
        final String containerPath = "/bfsdefault/default/" + unpackedContainerName;
        return "localzmq+protobuf://" + containerPath + "?lang=" + slc.getLanguage().getName() + "#buckets"
                + containerPath + slc.getUdfEntryPoint();
    }
}
