package com.exasol.containers;

/**
 * Types of container exits.
 */
public enum ExitType {
    /** Container exited normally */
    EXIT_SUCCESS,
    /** Container exited with an error */
    EXIT_ERROR,
    /** Any exit, with or without error */
    EXIT_ANY,
    /** None */
    EXIT_NONE
}