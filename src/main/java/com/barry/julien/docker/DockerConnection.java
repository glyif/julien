package com.barry.julien.docker;

/**
 * Created by gaoqingyang on 2/24/17.
 */
public interface DockerConnection
{

    Response getAllContainers(Request request);

    Response getRunningContainers(Request request);

    Response shutDownContainer(Request request);

    Response createContainer(Request request);

    Response deployContainer(Request request);

    Response runContainer(Request request);


}
