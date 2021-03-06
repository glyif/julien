package com.barry.julien.dialog;

import lombok.RequiredArgsConstructor;

/**
 * Created by gaoqingyang on 2/25/17.
 */
@RequiredArgsConstructor
public enum Replies
{
    LIST_INTENT_EXAMPLE("list all containers on production environment. "),

    REPROMPT("What would you like for me to do?"),

    WELCOME_PROMPT("For instructions on what you can say, please say help me."),

    WELCOME_MESSAGE("Hello, I'm Julien, your personal docker assistant. You can ask me questions like: "
            + LIST_INTENT_EXAMPLE.get()
            + REPROMPT.get()),

    CONTAINERS_LIST_PATTERN("Here's a full list of your containers on %s environment: %s."),

    SHUTDOWN_SUCCESS_PATTERN("Ok, shutting down container %s on %s environment."),

    RUN_SUCCESS_PATTERN("Ok, launching container %s on %s environment."),

    CREATE_SUCCESS_PATTERN("Ok, creating container %s on %s environment."),

    DEPLOY_SUCCESS_PATTERN("Ok, deploying container %s from %s to %s."),

    RUNNING_CONTAINERS_LIST_PATTERN("Here's a full list of running containers on %s environment: %s."),

    INCOMPLETE_COMMAND_PATTERN("Please, repeat your command. I didn't hear the name of the %s."),

    TARGET_ENV_NOT_SET(INCOMPLETE_COMMAND_PATTERN.format("target environment")),

    SOURCE_ENV_NOT_SET(INCOMPLETE_COMMAND_PATTERN.format("source environment")),

    ENV_NOT_SET(INCOMPLETE_COMMAND_PATTERN.format("environment")),

    CONTAINER_NOT_SET(INCOMPLETE_COMMAND_PATTERN.format("container")),

    CHECKER("I'm sorry, but the checker is broken"),

    NO_CONTAINERS_FOUND_PATTERN("There are currently no containers found on the %s environment."),

    NO_RUNNING_CONTAINERS_FOUND_PATTERN("There are currently no containers running on the %s environment."),

    HELP_MESSAGE("You can execute voice commands, such as, " + LIST_INTENT_EXAMPLE.get() + REPROMPT.get()),

    HELP_REPROMPT("You can say things like, " + LIST_INTENT_EXAMPLE.get() + REPROMPT.get()),

    GOODBYE("Live long and prosper...");


    /**
     * Text of the message
     */
    private final String text;

    /**
     * @return {@link #text}
     */
    public String get()
    {

        return (text);

    }

    /**
     * Format the string pattern.
     *
     * @param args the args passed to {@link String#format(String, Object...)}
     * @return formatted string.
     */
    public String format(Object... args)
    {

        return (String.format(text, args));

    }

}
