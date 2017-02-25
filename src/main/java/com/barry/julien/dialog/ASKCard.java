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

    HELP("Help");


    private final String text;


    public String get() {
        return text;
    }

}
