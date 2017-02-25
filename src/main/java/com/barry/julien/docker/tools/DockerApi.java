package com.barry.julien.docker.tools;

import com.barry.julien.docker.DockerConnection;
import com.barry.julien.docker.Environment;
import com.barry.julien.docker.Request;
import com.barry.julien.docker.tools.exception.JulienException;
import com.barry.julien.docker.tools.response.CommandResponse;
import com.barry.julien.docker.tools.response.Container;
import com.barry.julien.docker.Response;
import com.barry.julien.docker.tools.utils.SshConnection;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by gaoqingyang on 2/24/17.
 */
@Slf4j
public class DockerApi implements DockerConnection
{
    private static final String GET_ALL_CONTAINERS_COMMAND = "docker ps -a --format \"{{.Names}}\" | tr '_' ' ' ";
    private static final String GET_RUNNING_CONTAINERS_COMMAND = "docker ps --format \"{{.Names}} {{.Image}}\" | tr '_' ' ' ";

    private Container parseContainer(String containerInfo)
    {

        String[] parameters = containerInfo.split(" ");

        return (Container.builder()
                .name(parameters[0])
                .imageName(parameters[1])
                .build());

    }

    private List<Container> parseContainers(String message)
    {

        List<Container> containers = new ArrayList<>();

        if (!message.isEmpty()) {
            String[] containersInfo = message.split("\\n");

            for (String containerInfo : containersInfo)
            {

                containers.add(parseContainer(containerInfo));

            }
        }

        return containers;

    }

    private String getContainersName(List<Container> containers)
    {

        return (containers.stream().map(Container::getName).collect(Collectors.joining(", ")).replace("_", " "));

    }


    private Response getContainers(Environment environment, String command)
    {

        try
        {

            Session session = SshConnection.getSession(environment);
            CommandResponse commandResponse = SshConnection.executeCommand(session, command);

            String errorMessage = commandResponse.getErrMessage();

            SshConnection.closeSession(session);

            if (errorMessage.isEmpty())
            {
                List<Container> containers = parseContainers(commandResponse.getMessage());

                return new Response(getContainersName(containers), true);
            }
            else
            {
                return new Response(errorMessage, false);
            }

        }
        catch (JulienException e)
        {
            return new Response(e.getMessage(), false);
        }

    }

    public Response getAllContainers(Request request)
    {

        return getContainers(request.getTargetEnvironment(), GET_ALL_CONTAINERS_COMMAND);

    }

    public Response getRunningContainers(Request request)
    {

        return getContainers(request.getTargetEnvironment(), GET_RUNNING_CONTAINERS_COMMAND);

    }



}
