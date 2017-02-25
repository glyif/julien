package com.barry.julien.handler.ask;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by gaoqingyang on 2/24/17.
 */

@Getter
@RequiredArgsConstructor
public enum ASKArguments {

    /**
     * Represents an environment, from which the container is taken.
     */
    SOURCE_ENV("SourceEnv"),

    /**
     * Represents an environment, in which container is deployed, created, shut down etc.
     * Used in monitoring intents, such as {@code ListRunningIntent} and  {@code ListIntent}.
     */
    TARGET_ENV("TargetEnv"),

    /**
     * Represents the name of the container.
     */
    CONTAINER_NAME("Container");

    /**
     * Name of the conversation argument.
     */
    private final String name;

}
