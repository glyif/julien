package com.barry.julien.docker.tools.utils;


import com.barry.julien.docker.Environment;
import com.barry.julien.docker.tools.exception.JulienException;
import com.barry.julien.docker.tools.response.CommandResponse;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Created by gaoqingyang on 2/24/17.
 */
public class SshConnection
{

    private static final int PORT = 22;

    /**
     * getSession - starts new jsch session to connect to droplet
     * @param environment
     * @return
     * @throws JulienException
     */

    public static Session getSession(Environment environment) throws JulienException
    {
        String host = environment.getIp();
        String userName = environment.getUser();
        String password = environment.getPassword();

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(userName, host, PORT);
            session.setPassword(password);
            session.connect();

            return (session);
        } catch (JSchException e)
        {
            throw new JulienException(String.format("Missing connection to %s environment", environment));

        }
    }

    /**
     * closeSession - closes the jsch session
     * @param session
     */

    public static void close_session(Session session) {
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * getString - returns the string
     * @param is
     * @return
     * @throws JulienException
     */

    private static String getString(InputStream is) throws JulienException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is)))
        {
            return (br.lines().collect(Collectors.joining("\n")));
        }
        catch (IOException e)
        {
            throw new JulienException(e.getMessage());
        }
    }

    /**
     * executeCommand - takes a string and runs the command on a session
     * @param session
     * @param command
     * @return
     * @throws JulienException
     */

    public static CommandResponse executeCommand(Session session, String command) throws JulienException
    {
        try
        {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");

            channel.setCommand(command);

            InputStream out;
            InputStream error;

            try {
                out = channel.getInputStream();
                error = channel.getErrStream();
            }
            catch (IOException e)
            {
                throw new JulienException(e.getMessage());
            }

            try {
                out = channel.getInputStream();
                error = channel.getErrStream();
            } catch (IOException e) {
                throw new JulienException(e.getMessage());
            }

            channel.connect();

            CommandResponse response = CommandResponse.builder()
                    .message(getString(out))
                    .errMessage(getString(error)).build();

            channel.disconnect();
            return (response);
        }
        catch (JSchException e)
        {
            throw new JulienException(e.getMessage());

        }
    }

    /**
     * executeVoidCommand - gets error message of bad command
     * @param session
     * @param command
     * @return
     * @throws JulienException
     */
    public static String executeVoidCommand(Session session, String command) throws JulienException
    {
        return (executeCommand(session, command).getErrMessage());
    }




}
