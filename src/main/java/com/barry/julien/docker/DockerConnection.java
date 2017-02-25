package com.barry.julien.docker;

/**
 * Created by gaoqingyang on 2/24/17.
 */
public interface DockerConnection
{
    Response getAllContainers(Request request);

}
