package hneu.lab3.chart.websokets.util;

import hneu.lab3.chart.websokets.models.MessageWithCounter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static hneu.lab3.chart.websokets.util.Constants.USER_COUNT_RESTRICTION;

public class SessionManager {

    Logger LOG = LoggerFactory.getLogger(SessionManager.class);

    private final List<Session> sessions = Collections.synchronizedList(new ArrayList<>());
    private static SessionManager instance;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public void writeMessageToClients(MessageWithCounter messageWrapper) {

        String message = prepareMessage(messageWrapper);

        for (Session clientSession : sessions) {
            try {
                clientSession.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error("An exception occurred while sending message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void writeMessageToClientsWithRestrictions(MessageWithCounter messageWrapper) {

        String message = prepareMessage(messageWrapper);

        int restriction = getRestriction(message);

        synchronized (sessions) {
            try {
                for (int i = 0; i < restriction; i++) {
                    if (i < sessions.size()) {
                        Session clientSession = sessions.get(i);
                        clientSession.getBasicRemote().sendText(message);
                    } else
                        break;
                }
            } catch (IOException e) {
                LOG.error("An exception occurred while sending message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String prepareMessage(MessageWithCounter messageWrapper) {
        Parser<MessageWithCounter> parser = new GsonParser<>();
        return parser.parseToJson(messageWrapper, MessageWithCounter.class);
    }

    private int getRestriction(String message) {
        String[] arr = message.split(USER_COUNT_RESTRICTION);
        String restriction = StringUtils.EMPTY;
        for (Character character : arr[1].toCharArray()) {
            if (StringUtils.isNumeric(String.valueOf(character)))
                restriction += String.valueOf(character);
            else
                break;
        }
        return Integer.parseInt(restriction);
    }
}
