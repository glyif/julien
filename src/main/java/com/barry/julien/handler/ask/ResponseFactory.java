package com.barry.julien.handler.ask;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.barry.julien.dialog.ASKCard;
import com.barry.julien.dialog.Replies;

import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

import lombok.Getter;
import lombok.val;

import static com.barry.julien.dialog.Replies.*;

/**
 * Created by gaoqingyang on 2/25/17.
 */
public enum ResponseFactory
{
    INSTANCE;


    public static ResponseFactory getInstance() {
        return INSTANCE;
    }


    @Getter
    private final com.amazon.speech.speechlet.SpeechletResponse help =
            newAskResponse(HELP_MESSAGE.get(), HELP_REPROMPT.get(), ASKCard.HELP);

    @Getter
    private final com.amazon.speech.speechlet.SpeechletResponse goodbye =
            newTellResponse(GOODBYE, ASKCard.EXIT);

    @Getter
    private final SpeechletResponse checker =
            newTellResponse(CHECKER, ASKCard.EXIT);

    /**
     * newAskResponse - Method for creating the Ask response with default prompt
     *
     * @param output    the output to be spoken
     * @param cardTitle the title of the UI card.
     * @return ResponseFactory the speechlet response
     */
    public com.amazon.speech.speechlet.SpeechletResponse newAskResponse(Replies output, ASKCard cardTitle) {
        return newAskResponse(output.get(), cardTitle);
    }

    /**
     * newAskResponse - Method for creating the Ask response with default prompt
     *
     * @param output    the output to be spoken
     * @param cardTitle the title of the UI card.
     * @return ResponseFactory the speechlet response
     */
    public com.amazon.speech.speechlet.SpeechletResponse newAskResponse(String output, ASKCard cardTitle) {
        return newAskResponse(output, REPROMPT.get(), output, cardTitle);
    }


    /**
     * newAskResponse - Method for creating the Ask response. The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param output    the output to be spoken
     * @param reprompt  the reprompt for if the user doesn't reply or is misunderstood.
     * @param cardTitle the title of the UI card.
     * @return ResponseFactory the speechlet response
     */
    public com.amazon.speech.speechlet.SpeechletResponse newAskResponse(Replies output, Replies reprompt, ASKCard cardTitle) {
        return newAskResponse(output.get(), reprompt.get(), cardTitle);
    }

    /**
     * newAskResponse - Method for creating the Ask response. The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param stringOutput the output to be spoken
     * @param repromptText the reprompt for if the user doesn't reply or is misunderstood.
     * @param cardTitle    the title of the UI card.
     * @return ResponseFactory the speechlet response
     */
    public com.amazon.speech.speechlet.SpeechletResponse newAskResponse(String stringOutput, String repromptText, ASKCard cardTitle) {
        return newAskResponse(stringOutput, repromptText, stringOutput, cardTitle);
    }

    /**
     * newAskResponse - Method for creating the Ask response. The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param stringOutput the output to be spoken
     * @param repromptText the reprompt for if the user doesn't reply or is misunderstood.
     * @param cardContent  the content of the UI card.
     * @param cardTitle    the title of the UI card.
     * @return ResponseFactory the speechlet response
     */
    public com.amazon.speech.speechlet.SpeechletResponse newAskResponse(String stringOutput, String repromptText, String cardContent, ASKCard cardTitle) {
        val outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);

        val repromptOutputSpeech = new PlainTextOutputSpeech();
        repromptOutputSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        val card = new SimpleCard();
        card.setTitle(cardTitle.get());
        card.setContent(cardContent);

        return com.amazon.speech.speechlet.SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    /**
     * newTellResponse - Method for creating the Tell response.
     * Returning such a response will stop the session.
     * The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param output    the output to be spoken
     * @param cardTitle the title of the UI card.
     * @return ResponseFactory the speechlet response
     */
    public com.amazon.speech.speechlet.SpeechletResponse newTellResponse(Replies output, ASKCard cardTitle) {
        return newTellResponse(output.get(), cardTitle);
    }

    /**
     * newTellResponse - Method for creating the Tell response.
     * Returning such a response will stop the session.
     * The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param stringOutput the output to be spoken
     * @param cardTitle    the title of the UI card.
     * @return ResponseFactory the speechlet response
     */
    public com.amazon.speech.speechlet.SpeechletResponse newTellResponse(String stringOutput, ASKCard cardTitle) {
        val outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);

        val card = new SimpleCard();
        card.setTitle(cardTitle.get());
        card.setContent(stringOutput);

        return com.amazon.speech.speechlet.SpeechletResponse.newTellResponse(outputSpeech, card);
    }
}
