package com.barry.julien.docker.tools.response;

import lombok.Builder;
import lombok.Value;

/**
 * Created by gaoqingyang on 2/24/17.
 */
@Builder
@Value
public final class CommandResponse
{
    String message;
    String errMessage;

}
