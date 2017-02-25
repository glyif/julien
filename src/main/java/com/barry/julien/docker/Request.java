package com.barry.julien.docker;

import lombok.Getter;
import lombok.Value;

/**
 * Created by gaoqingyang on 2/24/17.
 */
@Value
@Getter
public final class Request
{

    Environment sourceEnvironment;

    Environment targetEnvironment;

    String container;

}
