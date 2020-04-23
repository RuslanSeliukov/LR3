package hneu.lab3.chart.websokets;

import hneu.lab3.chart.websokets.models.BasicMassage;
import hneu.lab3.chart.websokets.models.MessageWithCounter;
import hneu.lab3.chart.websokets.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import static hneu.lab3.chart.websokets.util.Constants.*;

@ServerEndpoint("/chatEndpoint")
public class MainWebSocket {

    Logger LOG = LoggerFactory.getLogger(MainWebSocket.class);
    Parser<BasicMassage> parser;
    ValidationUtils validationUtils;
    private static int messageCounter = 0;

    @Autowired
    GsonParser<BasicMassage> gParser;
    @Autowired
    SimpleJsonParser<BasicMassage> simpleJsonParser;

    public MainWebSocket() {
        validationUtils = new ValidationUtils();
        switch (PARSER_TYPE) {
            case (GSON):
                parser = new GsonParser<>();
                break;
            case (SIMPLE_JSON):
                parser = new SimpleJsonParser<>();
                break;
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        SessionManager.getInstance().addSession(session);
        LOG.info("Connection success: " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message ) {

        //increment message counter
        incrementCount();

        //parse message
        BasicMassage basicMassage = parser.parseToObject(message, BasicMassage.class);

        MessageWithCounter messageWrapper = prepareMessageWithCounter(basicMassage);

        if (basicMassage.getMessage().contains(USER_COUNT_RESTRICTION))
            SessionManager.getInstance().writeMessageToClientsWithRestrictions(messageWrapper);
        else
            SessionManager.getInstance().writeMessageToClients(messageWrapper);

        LOG.info("Client: " + basicMassage.getUser() + " with session id: " + session.getId() + " send message " + message);

        //validate json by schema
        if (validationUtils.validateJsonBySchema(parser.parseToJson(basicMassage, BasicMassage.class)))
            LOG.info("Json validation success");
        else
            LOG.info("Json validation failure");

        //parse json file to object
        if (parser.parseJsonFileToObject(BasicMassage.class) != null)
            LOG.info("Parsed json file to object successfully");
         else
            LOG.warn("Can not parse json file to object");
    }

    @OnClose
    public void onClose(Session session) {
        SessionManager.getInstance().removeSession(session);
        LOG.info("Session " + session.getId() + " closed");
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        LOG.info("An exception occurred for: " + session.getId());
        SessionManager.getInstance().removeSession(session);
        LOG.info("Connection closed for session:" + session.getId());
    }

    public static synchronized void incrementCount() {
        messageCounter++;
    }

    private MessageWithCounter prepareMessageWithCounter(BasicMassage basicMassage) {
        MessageWithCounter messageWithCounter = new MessageWithCounter();
        messageWithCounter.setUser(basicMassage.getUser());
        messageWithCounter.setMessage(basicMassage.getMessage());
        messageWithCounter.setCounter(messageCounter);
        return messageWithCounter;
    }
}