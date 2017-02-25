package com.barry.julien.docker;

import lombok.Getter;`
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by gaoqingyang on 2/24/17.
 */

@Getter
@RequiredArgsConstructor
public enum Environment
{

    DEVELOPMENT("45.55.21.198", "development"),
    STAGING("45.55.21.208", "staging"),
    PRODUCTION("104.236.179.196", "production");

    private final String ip;

    private final String name;

    private final String user = "root";

    private final String password = "Alexa123&";

    public static Optional<Environment> getByName(String name)
    {
        return Arrays.stream(Environment.values())
                .filter(environment -> environment.name.equalsIgnoreCase(name))
                .findAny();
    }


}
