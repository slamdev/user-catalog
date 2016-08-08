package com.github.slamdev.ripe.business.isp.control;

import com.github.slamdev.ripe.business.isp.entity.InternetServiceProvider;
import com.github.slamdev.ripe.business.isp.entity.InternetServiceProviderCreationEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static javax.mail.Message.RecipientType.TO;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CongratulationEmailSenderTest {

    @InjectMocks
    @Spy
    private CongratulationEmailSender sender;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage message;

    @Before
    public void setUp() {
        // Spying is used since we cannot mock final methods of SpringTemplateEngine
        doReturn("").when(sender).buildHtmlContent(anyObject());
    }

    @Test
    public void should_send_email() throws MessagingException {
        InternetServiceProvider isp = InternetServiceProvider.builder()
                .companyName("Company").email("some@email.com").build();
        sender.subject = "some subject";
        InternetServiceProviderCreationEvent event = new InternetServiceProviderCreationEvent(isp);
        when(mailSender.createMimeMessage()).thenReturn(message);
        sender.sendCongratulationEmail(event);
        verify(message, times(1)).setSubject("some subject", "UTF-8");
        verify(message, times(1)).setRecipient(TO, new InternetAddress("some@email.com"));
    }
}
