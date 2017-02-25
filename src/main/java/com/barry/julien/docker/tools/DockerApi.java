package com.barry.julien.docker.tools;

import com.barry.julien.docker.DockerConnection;
import com.barry.julien.docker.Environment;
import com.barry.julien.docker.Request;
import com.barry.julien.docker.tools.exception.JulienException;
import com.barry.julien.docker.tools.response.CommandResponse;
import com.barry.julien.docker.tools.response.Container;
import com.barry.julien.docker.Response;
import com.barry.julien.docker.tools.utils.SshConnection;
import com.barry.julien.handler.ask.JulienSpeechlet;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.barry.julien.docker.tools.utils.SshConnection.executeCommand;
import static com.barry.julien.docker.tools.utils.SshConnection.executeVoidCommand;
import static com.barry.julien.docker.tools.utils.SshConnection.getSession;
/**
 * Created by gaoqingyang on 2/24/17.
 */
@Slf4j
public class DockerApi implements DockerConnection
{
    private static final String GET_ALL_CONTAINERS_COMMAND = "docker ps -a --format \"{{.Names}}\" | tr '_' ' ' ";
    private static final String GET_RUNNING_CONTAINERS_COMMAND = "docker ps --format \"{{.Names}} {{.Image}}\"";
    private static final String SHUTDOWN_COMMAND = "docker kill %s";
    private static final String START_COMMAND = "docker start %s";
    private static final String CREATE_CONTAINER_COMMAND = "docker create --name %s %s";
    private static final String DEFAULT_IMAGE = "alpine";
    private static final String DEFAULT_SCRIPT = " sh -c 'while sleep 5m; do :; done'";
    private static final String SAVE_IMAGE_COMMAND = "docker save %s > %s.tar";
    private static final String INSTALL_SSHPASS_COMMAND = "apt install sshpass";
    private static final String COPY_COMMAND = "sshpass -p \"%s\" scp -r %s@%s:/%s/%s.tar /%s/%s.tar";
    private static final String LOAD_IMAGE = "docker load -i %s.tar";

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

    public Response shutDownContainer(Request request) {
        String containerName = request.getContainer();
        Environment environment = request.getTargetEnvironment();
        try {
            Session session = getSession(environment);

            String errMessage;

            CommandResponse getAllContainersResponse = executeCommand(session, GET_ALL_CONTAINERS_COMMAND);

            List<Container> allContainers = parseContainers(getAllContainersResponse.getMessage());

            if (isContainerExist(allContainers, containerName)) {
                CommandResponse getRunningContainersResponse = executeCommand(session, GET_RUNNING_CONTAINERS_COMMAND);
                List<Container> runningContainers = parseContainers(getRunningContainersResponse.getMessage());
                if (isContainerExist(runningContainers, containerName)) {
                    errMessage = stopContainer(session, containerName);
                } else {
                    errMessage = String.format("Container %s is not running", containerName);
                }
            } else {
                errMessage = String.format("Container %s doesn't exist", containerName);
            }
            SshConnection.closeSession(session);
            if (errMessage.isEmpty()) {
                return new Response(null, true);
            } else {
                return new Response(errMessage, false);
            }
        } catch (JulienException e) {
            return new Response(e.getMessage(), false);
        }
    }

    private String stopContainer(Session session, String containerName) throws JulienException {
        CommandResponse commandResponse = executeCommand(session,
                String.format(SHUTDOWN_COMMAND, getFormattedContainerName(containerName)));

        return commandResponse.getErrMessage();
    }

    private boolean isContainerExist(List<Container> containers, String containerName) {
        Set<String> containerNames = containers.stream()
                .map(Container::getName)
                .collect(Collectors.toSet());
        return containerNames.contains(getFormattedContainerName(containerName));
    }

    public Response createContainer(Request request) {
        String containerName = request.getContainer();
        Environment environment = request.getTargetEnvironment();
        try {
            Session session = getSession(environment);

            String errMessage;

            CommandResponse getAllContainersResponse = executeCommand(session, GET_ALL_CONTAINERS_COMMAND);

            List<Container> allContainers = parseContainers(getAllContainersResponse.getMessage());

            if (!isContainerExist(allContainers, containerName)) {
                errMessage = createContainer(session, containerName);
            } else {
                errMessage = String.format("Container %s is not running.", containerName);
            }

            SshConnection.closeSession(session);
            if (errMessage.isEmpty()) {
                return new Response(null, true);
            } else {
                return new Response(errMessage, false);
            }
        } catch (JulienException e) {
            return new Response(e.getMessage(), false);
        }
    }

    private String createContainer(Session session, String containerName) throws JulienException {
        return createContainer(session, containerName, DEFAULT_IMAGE);
    }

    private String createContainer(Session session, String containerName, String imageName) throws JulienException {
        CommandResponse commandResponse = executeCommand(session,
                String.format(CREATE_CONTAINER_COMMAND, getFormattedContainerName(containerName), imageName + DEFAULT_SCRIPT));

        return commandResponse.getErrMessage();
    }

    public Response deployContainer(Request request) {
        String containerName = request.getContainer();
        Environment targetEnvironment = request.getTargetEnvironment();
        Environment sourceEnvironment = request.getSourceEnvironment();
        try {
            Session sourceSession = getSession(sourceEnvironment);
            Session targetSession = getSession(targetEnvironment);

            String errMessage;

            CommandResponse getAllSourceContainersResponse = executeCommand(sourceSession, GET_ALL_CONTAINERS_COMMAND);
            List<Container> allSourceContainers = parseContainers(getAllSourceContainersResponse.getMessage());

            if (isContainerExist(allSourceContainers, containerName)) {
                CommandResponse getAllTargetContainersResponse = executeCommand(targetSession, GET_ALL_CONTAINERS_COMMAND);
                List<Container> allTargetContainers = parseContainers(getAllTargetContainersResponse.getMessage());

                if (!isContainerExist(allTargetContainers, containerName)) {
                    errMessage = deployContainer(sourceSession, sourceEnvironment,
                            getContainer(allSourceContainers, containerName),
                            targetSession, targetEnvironment);
                } else {
                    errMessage = String.format("Container %s already exists on %s environment.",
                            containerName, targetEnvironment.getName());
                }
            } else {
                errMessage = String.format("Container %s doesn't exist on %s environment.",
                        containerName, sourceEnvironment.getName());
            }

            SshConnection.closeSession(sourceSession);
            SshConnection.closeSession(targetSession);

            if (errMessage.isEmpty()) {
                return new Response(null, true);
            } else {
                return new Response(errMessage, false);
            }
        } catch (JulienException e) {
            return new Response(e.getMessage(), false);
        }
    }

    private Container getContainer(List<Container> containers, String containerName) {
        String formattedContainerName = getFormattedContainerName(containerName);
        for (Container container : containers) {
            if (container.getName().equals(formattedContainerName)) {
                return container;
            }
        }

        throw new RuntimeException(String.format("Container %s not found.", containerName));
    }

    private String deployContainer(Session sourceSession, Environment sourceEnvironment,
                                   Container container,
                                   Session targetSession, Environment targetEnvironment) throws JulienException {
        String errMessage;

        // package image to file
        errMessage = saveImage(sourceSession, container.getImageName());
        if (!errMessage.isEmpty()){
            return errMessage;
        }

        // install sshpass
        executeVoidCommand(targetSession, INSTALL_SSHPASS_COMMAND);

        // copy image
        errMessage = copyTarImage(targetSession, sourceEnvironment, targetEnvironment,
                getFormattedImageName(container.getImageName()));
        if (!errMessage.isEmpty()){
            return errMessage;
        }

        // load image
        errMessage = executeVoidCommand(targetSession,
                String.format(LOAD_IMAGE, getFormattedImageName(container.getImageName())));
        if (!errMessage.isEmpty()){
            return errMessage;
        }

        // create container
        errMessage = createContainer(targetSession, container.getName(), container.getImageName());
        if (!errMessage.isEmpty()){
            return errMessage;
        }

        // run container
        errMessage = runContainer(targetSession, container.getName());

        return errMessage;
    }

    private String copyTarImage(Session targetSession,
                                Environment sourceEnvironment, Environment targetEnvironment,
                                String imageName) throws JulienException {
        CommandResponse commandResponse = executeCommand(targetSession,
                String.format(COPY_COMMAND, sourceEnvironment.getPassword(),
                        sourceEnvironment.getUser(), sourceEnvironment.getIp(),
                        sourceEnvironment.getUser(), imageName,
                        targetEnvironment.getUser(), imageName));

        return commandResponse.getErrMessage();
    }

    private String saveImage(Session session, String imageName) throws JulienException {
        CommandResponse commandResponse = executeCommand(session,
                String.format(SAVE_IMAGE_COMMAND, imageName, getFormattedImageName(imageName)));

        return commandResponse.getErrMessage();
    }

    public Response runContainer(Request request) {
        String containerName = request.getContainer();
        Environment environment = request.getTargetEnvironment();
        try {
            Session session = getSession(environment);

            String errMessage;

            CommandResponse getAllContainersResponse = executeCommand(session, GET_ALL_CONTAINERS_COMMAND);

            List<Container> allContainers = parseContainers(getAllContainersResponse.getMessage());

            if (isContainerExist(allContainers, containerName)) {
                CommandResponse getRunningContainersResponse = executeCommand(session, GET_RUNNING_CONTAINERS_COMMAND);
                List<Container> runningContainers = parseContainers(getRunningContainersResponse.getMessage());
                if (!isContainerExist(runningContainers, containerName)) {
                    errMessage = runContainer(session, containerName);
                } else {
                    errMessage = String.format("Container %s is already running.", containerName);
                }
            } else {
                errMessage = String.format("Container %s doesn't exist", containerName);
            }
            SshConnection.closeSession(session);
            if (errMessage.isEmpty()) {
                return new Response(null, true);
            } else {
                return new Response(errMessage, false);
            }
        } catch (JulienException e) {
            return new Response(e.getMessage(), false);
        }
    }

    private String runContainer(Session session, String containerName) throws JulienException {
        CommandResponse commandResponse = executeCommand(session,
                String.format(START_COMMAND, getFormattedContainerName(containerName)));

        return commandResponse.getErrMessage();
    }

    private String getFormattedContainerName(String name) {
        return name.replace(" ", "_").toLowerCase();
    }

    private String getFormattedImageName(String imageName){
        return imageName.replace("/", "_");
    }

}