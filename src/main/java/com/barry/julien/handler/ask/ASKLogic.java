package com.barry.julien.handler.ask;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.barry.julien.dialog.ASKCard;
import com.barry.julien.docker.DockerConnection;
import com.barry.julien.docker.Request;
import lombok.Getter;
import lombok.val;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.barry.julien.dialog.ASKCard.*;
import static com.barry.julien.dialog.Replies.*;
import static com.barry.julien.handler.ask.ASKArguments.*;
import static java.util.EnumSet.of;

/**
 * Created by gaoqingyang on 2/25/17.
 */
class ASKLogic
{

    private static final ResponseFactory RESPONSE_FACTORY =
            ResponseFactory.getInstance();

    private final DockerConnection api;

    ASKLogic(DockerConnection api) {
        this.api = api;
    }

    Intent getIntentByName(String name) {
        return EnumSet.allOf(Intent.class).stream()
                .filter(intent -> intent.getIntentName().equals(name))
                .findAny()
                .orElse(Intent.AMAZON_HELP);
    }

    /**
     * Method for passing a request to {@link #api}.
     *
     * @param intent  intent of the user.
     * @param request request object.
     * @return result of the operation.
     */
    SpeechletResponse executeOperation(Intent intent, Request request) {
        return intent.getOperation().apply(request, api);
    }

    @Getter
    enum Intent {

        /**
         * Retrieves the list of containers on given environment.
         */
        LIST("ListIntent", of(TARGET_ENV), (request, api) -> {
            val response = api.getAllContainers(request);
            val envName = request.getTargetEnvironment().getName();
            val message = response.getMessage();
            final String output;
            if (!response.isSuccess()) { //error
                output = message;
            } else if (message.isEmpty()) { //no containers found
                output = NO_CONTAINERS_FOUND_PATTERN.format(envName);
            } else {
                output = CONTAINERS_LIST_PATTERN.format(envName, message);
            }
            return RESPONSE_FACTORY.newAskResponse(output, GET_ALL_CONTAINERS);
        }),

        /**
         * Retrieves the list of containers currently running on given environment.
         */
        LIST_RUNNING("ListRunningIntent", of(TARGET_ENV), (request, api) -> {
            val response = api.getRunningContainers(request);
            val envName = request.getTargetEnvironment().getName();
            val message = response.getMessage();
            final String output;
            if (!response.isSuccess()) { //error
                output = message;
            } else if (message.isEmpty()) { //no containers found
                output = NO_RUNNING_CONTAINERS_FOUND_PATTERN.format(envName);
            } else {
                output = RUNNING_CONTAINERS_LIST_PATTERN.format(envName, message);
            }
            return RESPONSE_FACTORY.newAskResponse(output, GET_RUNNING_CONTAINERS);
        }),

        /**
         * Creates container with given name on given environment.
         */
        CREATE("CreateIntent", of(TARGET_ENV, CONTAINER_NAME), (request, api) -> {
            val response = api.createContainer(request);
            final String output;
            if (!response.isSuccess()) { //error
                output = response.getMessage();
            } else {
                output = CREATE_SUCCESS_PATTERN
                        .format(request.getContainer(),
                                request.getTargetEnvironment().getName());
            }
            return RESPONSE_FACTORY.newAskResponse(output, ASKCard.CREATE);
        }),

        /**
         * Deploys given container from given source environment to given target environment.
         */
        DEPLOY("DeployIntent", of(TARGET_ENV, SOURCE_ENV, CONTAINER_NAME), (request, api) -> {
            val response = api.deployContainer(request);
            final String output;
            if (!response.isSuccess()) { //error
                output = response.getMessage();
            } else {
                output = DEPLOY_SUCCESS_PATTERN
                        .format(request.getContainer(),
                                request.getSourceEnvironment().getName(),
                                request.getTargetEnvironment().getName());
            }
            return RESPONSE_FACTORY.newAskResponse(output, ASKCard.DEPLOY);
        }),

        /**
         * Launches the given container on given environment.
         */
        RUN("RunIntent", of(TARGET_ENV, CONTAINER_NAME), (request, api) -> {
            val response = api.runContainer(request);
            final String output;
            if (!response.isSuccess()) { //error
                output = response.getMessage();
            } else {
                output = RUN_SUCCESS_PATTERN
                        .format(request.getContainer(),
                                request.getTargetEnvironment().getName());
            }
            return RESPONSE_FACTORY.newAskResponse(output, ASKCard.RUN);
        }),


        /**
         * Shuts down the given container on given environment.
         */
        SHUTDOWN("ShutdownIntent", of(TARGET_ENV, CONTAINER_NAME), (request, api) -> {
            val response = api.shutDownContainer(request);
            final String output;
            if (!response.isSuccess()) { //error
                output = response.getMessage();
            } else {
                output = SHUTDOWN_SUCCESS_PATTERN
                        .format(request.getContainer(),
                                request.getTargetEnvironment().getName());
            }
            return RESPONSE_FACTORY.newAskResponse(output, ASKCard.SHUTDOWN);
        }),


        /**
         * CheckerIntent returns state of checker
         */

        CHECKER("CheckerIntent", RESPONSE_FACTORY::getChecker),

        /**
         * HelpIntent returns help message.
         */
        AMAZON_HELP("AMAZON.HelpIntent", RESPONSE_FACTORY::getHelp),

        /**
         * StopIntent exits the conversation
         */
        AMAZON_STOP("AMAZON.StopIntent", RESPONSE_FACTORY::getGoodbye),

        /**
         * CancelIntent exits the conversation
         */
        AMAZON_CANCEL("AMAZON.CancelIntent", AMAZON_STOP.operation);

        /**
         * Name of the intent.
         */
        private final String intentName;

        /**
         * Function which is used by intent.
         */
        private final BiFunction<Request, DockerConnection, SpeechletResponse> operation;

        /**
         * Slots required to by this intent.
         */
        private final EnumSet<ASKArguments> slots;

        /**
         * Constructor for intents which don't require operations with docker containers.
         *
         * @param intentName {@link #intentName}
         * @param supplier   {@link #operation} result supplier.
         */
        Intent(String intentName,
               Supplier<SpeechletResponse> supplier) {

            this.intentName = intentName;
            this.operation = (request, dockerApi) -> supplier.get();
            this.slots = EnumSet.noneOf(ASKArguments.class);
        }

        Intent(String intentName,
               BiFunction<Request, DockerConnection, SpeechletResponse> operation) {

            this.intentName = intentName;
            this.operation = operation;
            this.slots = EnumSet.noneOf(ASKArguments.class);
        }

        Intent(String intentName,
               EnumSet<ASKArguments> slots,
               BiFunction<Request, DockerConnection, SpeechletResponse> operation) {

            this.intentName = intentName;
            this.slots = slots;
            this.operation = (request, dockerApi) ->
                    getInvalidResponse(request, this.slots)
                            .orElseGet(() -> operation.apply(request, dockerApi));
        }

        private static Optional<SpeechletResponse>
        getInvalidResponse(final Request request,
                           EnumSet<ASKArguments> slots) {

            final SpeechletResponse response;
            if (slots.containsAll(of(SOURCE_ENV, TARGET_ENV))
                    && request.getTargetEnvironment() == null) {
                response = RESPONSE_FACTORY
                        .newAskResponse(TARGET_ENV_NOT_SET, INCOMPLETE_REQUEST);
            } else if (slots.contains(ASKArguments.SOURCE_ENV)
                    && request.getSourceEnvironment() == null) {
                response = RESPONSE_FACTORY
                        .newAskResponse(SOURCE_ENV_NOT_SET, INCOMPLETE_REQUEST);
            } else if (slots.contains(TARGET_ENV)
                    && request.getTargetEnvironment() == null) {
                response = RESPONSE_FACTORY
                        .newAskResponse(ENV_NOT_SET, INCOMPLETE_REQUEST);
            } else if (slots.contains(CONTAINER_NAME)
                    && request.getContainer() == null) {
                response = RESPONSE_FACTORY
                        .newAskResponse(CONTAINER_NOT_SET, INCOMPLETE_REQUEST);
            } else {
                response = null;
            }
            return Optional.ofNullable(response);
        }
    }



}
