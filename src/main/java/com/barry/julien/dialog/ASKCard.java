package com.barry.julien.dialog;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import lombok.RequiredArgsConstructor;

/**
 * Created by gaoqingyang on 2/25/17.
 */
@RequiredArgsConstructor
public enum ASKCard
{

    WELCOME("Welcome, Bobby"),

    EXIT("Bye!"),

    GET_ALL_CONTAINERS("Getting all containers"),

    GET_RUNNING_CONTAINERS("Get running containers"),

    SHUTDOWN("Shutting down a container"),

    DEPLOY("Deploying a container"),

    RUN("Running a container"),

    CREATE("Creating a container"),

    INCOMPLETE_REQUEST("Request information is insufficient"),

    HELP("Help");


    private final String text;


    public String get() {
        return text;
    }

}
