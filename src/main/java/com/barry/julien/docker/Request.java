package com.barry.julien.docker;

import lombok.Builder;
import lombok.Value;

/**
 * Created by gaoqingyang on 2/24/17.
 */
@Value
@Builder
public final class Request
{

    Environment sourceEnvironment;

    Environment targetEnvironment;

    String container;

}
