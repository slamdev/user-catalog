package com.github.slamdev.ripe.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static java.time.Instant.now;
import static java.util.Arrays.stream;

@Component
@Profile("!standalone")
public class StubMailSender extends JavaMailSenderImpl {

    private static final String HEADER_MESSAGE_ID = "Message-ID";

    @Value("${mail.stubs.dir}")
    private String mailStubsDir;

    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object... originalMessages) {
        stream(mimeMessages).map(this::prepareMessage).forEach(this::sendToFile);
    }

    private MimeMessage prepareMessage(MimeMessage message) {
        try {
            if (message.getSentDate() == null) {
                message.setSentDate(new Date());
            }
            String messageId = message.getMessageID();
            message.saveChanges();
            if (messageId != null) {
                message.setHeader(HEADER_MESSAGE_ID, messageId);
            }
            return message;
        } catch (MessagingException e) {
            throw new IllegalStateException(e);
        }
    }

    private void sendToFile(MimeMessage message) {
        String fileName = now().getNano() + ".eml";
        Path file = Paths.get(mailStubsDir, fileName);
        try (OutputStream stream = Files.newOutputStream(file)) {
            message.writeTo(stream);
        } catch (IOException | MessagingException e) {
            throw new IllegalStateException(e);
        }
    }
}
