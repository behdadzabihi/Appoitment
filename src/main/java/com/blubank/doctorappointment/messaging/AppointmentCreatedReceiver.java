package com.blubank.doctorappointment.messaging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;


@Component
public class AppointmentCreatedReceiver implements MessageListener {

    private static final Logger log = LogManager.getLogger(AppointmentCreatedReceiver.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String appointmentMessage = new String(message.getBody());
        log.info("Received: " + appointmentMessage);
    }
}

