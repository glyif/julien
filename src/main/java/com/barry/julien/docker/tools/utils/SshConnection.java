package com.barry.julien.docker.tools.utils;


import com.barry.julien.docker.Environment;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Created by gaoqingyang on 2/24/17.
 */
public class SshConnection
{

    private static final int PORT = 22;

    public static Session getSession(Environment environment)
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
            String.format("Missing connection to %s environment", environment);

        }
    }


}
