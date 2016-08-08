package com.github.slamdev.ripe.business.isp.control;

import com.github.slamdev.ripe.business.isp.entity.InternetServiceProvider;
import com.github.slamdev.ripe.business.isp.entity.InternetServiceProviderCreationEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InternetServiceProviderRepositoryAspectTest {

    @InjectMocks
    private InternetServiceProviderRepositoryAspect aspect;

    @Mock
    private ApplicationEventPublisher publisher;

    @Test
    public void should_send_event_to_publisher() {
        InternetServiceProvider isp = InternetServiceProvider.builder().build();
        ArgumentCaptor<InternetServiceProviderCreationEvent> captor = ArgumentCaptor.forClass(InternetServiceProviderCreationEvent.class);
        aspect.publishEvent(isp);
        verify(publisher, times(1)).publishEvent(captor.capture());
        assertThat(captor.getValue().getInternetServiceProvider(), is(isp));
    }
}
