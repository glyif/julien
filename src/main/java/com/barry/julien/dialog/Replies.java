package com.barry.julien.dialog;

import lombok.RequiredArgsConstructor;

/**
 * Created by gaoqingyang on 2/25/17.
 */
@RequiredArgsConstructor
public enum Replies
{
    LIST_INTENT_EXAMPLE("list all containers on production environment. "),

    REPROMPT("Now, what can I help you with?"),

    WELCOME_PROMPT("For instructions on what you can say, please say help me."),

    WELCOME_MESSAGE("Welcome to Dockee. You can ask a question like: "
            + LIST_INTENT_EXAMPLE.get()
            + REPROMPT.get()),

    CONTAINERS_LIST_PATTERN("Here's full list of your containers on %s environment: %s."),

    RUNNING_CONTAINERS_LIST_PATTERN("Here's full list of your containers currently running on %s environment: %s."),

    INCOMPLETE_COMMAND_PATTERN("Please, repeat your command. I didn't hear the name of the %s."),

    TARGET_ENV_NOT_SET(INCOMPLETE_COMMAND_PATTERN.format("target environment")),

    SOURCE_ENV_NOT_SET(INCOMPLETE_COMMAND_PATTERN.format("source environment")),

    ENV_NOT_SET(INCOMPLETE_COMMAND_PATTERN.format("environment")),

    CONTAINER_NOT_SET(INCOMPLETE_COMMAND_PATTERN.format("container")),


    NO_CONTAINERS_FOUND_PATTERN("No containers found on %s environment."),

    NO_RUNNING_CONTAINERS_FOUND_PATTERN("No running containers found on %s environment."),

    HELP_MESSAGE("You can execute voice commands, such as, " + LIST_INTENT_EXAMPLE.get() + REPROMPT.get()),

    HELP_REPROMPT("You can say things like, " + LIST_INTENT_EXAMPLE.get() + REPROMPT.get()),

    GOODBYE("Goodbye...");


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
