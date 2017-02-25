package com.barry.julien.handler.ask;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gaoqingyang on 2/25/17.
 */
public class JulienSpeechletStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds = new HashSet<String>();
    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds.add("amzn1.ask.skill.8e8abb05-380f-411e-aec3-7a0a2ab77d0a");
    }

    public JulienSpeechletStreamHandler() {
        super(new JulienSpeechlet(), supportedApplicationIds);
    }


}
