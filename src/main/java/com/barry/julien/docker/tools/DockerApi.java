package com.barry.julien.docker.tools;

import com.barry.julien.docker.tools.response.Container;

/**
 * Created by gaoqingyang on 2/24/17.
 */
public class DockerApi
{

    private Container parseContainer(String containerInfo)
    {

        String[] parameters = containerInfo.split(" ");

        return (Container.builder().name(parameters[0]).imageName(parameters[1]).build());

    }


}
