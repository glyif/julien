package com.barry.julien.docker;

import lombok.Getter;
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

    DEVELOPMENT("IP ADDRESS HERE", "development"),
    STAGING("IP ADDRESS HERE", "staging"),
    PRODUCTION("IPADDRESS HERE", "production");

    private final String ip;

    private final String name;

    private final String user = "";

    private final String password = "";

    public static Optional<Environment> getByName(String name)
    {
        return Arrays.stream(Environment.values())
                .filter(environment -> environment.name.equalsIgnoreCase(name))
                .findAny();
    }


}
