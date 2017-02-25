package com.barry.julien.docker.tools.utils;


import com.barry.julien.docker.Environment;
import com.barry.julien.docker.tools.exception.JulienException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

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

    public static void closeSession(Session session) {
        if (session != null) {
            session.disconnect();
        }
    }






}
