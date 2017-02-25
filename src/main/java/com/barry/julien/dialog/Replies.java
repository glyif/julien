package com.barry.julien.dialog;

import lombok.RequiredArgsConstructor;

/**
 * Created by gaoqingyang on 2/25/17.
 */
@RequiredArgsConstructor
public enum Replies
{

    WELCOME_PROMPT("For instructions on what you can say, please say help me."),

    CONTAINERS_LIST_PATTERN("Here's full list of your containers on %s environment: %s."),

    RUNNING_CONTAINERS_LIST_PATTERN("Here's full list of your containers currently running on %s environment: %s.");


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
