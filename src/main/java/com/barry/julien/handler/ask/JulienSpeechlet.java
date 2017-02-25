package com.barry.julien.handler.ask;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.barry.julien.dialog.ASKCard;
import com.barry.julien.docker.tools.DockerApi;
import com.barry.julien.docker.Request;
import com.barry.julien.docker.Environment;
import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.barry.julien.dialog.Replies.WELCOME_MESSAGE;
import static com.barry.julien.dialog.Replies.WELCOME_PROMPT;


/**
 * Created by gaoqingyang on 2/25/17.
 */

@Slf4j
@val
@Getter
public class JulienSpeechlet implements SpeechletV2
{

    private static final ResponseFactory RESPONSE_FACTORY =
            ResponseFactory.getInstance();


    /**
     * {@link ASKLogic} object with lazy getter.
     */
    @Getter(lazy = true)
    private final ASKLogic strategy = new ASKLogic(new DockerApi());

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        return RESPONSE_FACTORY.newAskResponse(WELCOME_MESSAGE, WELCOME_PROMPT, ASKCard.WELCOME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        val request = requestEnvelope.getRequest();
        val session = requestEnvelope.getSession();

        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

        val intent = request.getIntent();

        val strategy = getStrategy(); //lazily get the strategy.

        val intentName = intent.getName();

        /* retrieve one of the supported intent types. */
        val askIntent = strategy.getIntentByName(intentName);

        val requestBuilder = Request.builder();

        provideSlotValue(intent, ASKArguments.CONTAINER_NAME)
                .ifPresent(requestBuilder::container);

        provideSlotValue(intent, ASKArguments.SOURCE_ENV)
                .flatMap(Environment::getByName)
                .ifPresent(requestBuilder::sourceEnvironment);

        provideSlotValue(intent, ASKArguments.TARGET_ENV)
                .flatMap(Environment::getByName)
                .ifPresent(requestBuilder::targetEnvironment);

        val dockerRequest = requestBuilder.build();

        return strategy.executeOperation(askIntent, dockerRequest);
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    private static Optional<String> provideSlotValue(Intent intent, ASKArguments slot) {
        return Optional.ofNullable(intent.getSlot(slot.getName()))
                .map(Slot::getValue);
    }




}
