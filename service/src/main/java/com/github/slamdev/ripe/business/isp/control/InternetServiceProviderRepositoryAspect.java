package com.github.slamdev.ripe.business.isp.control;

import com.github.slamdev.ripe.business.isp.entity.InternetServiceProvider;
import com.github.slamdev.ripe.business.isp.entity.InternetServiceProviderCreationEvent;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InternetServiceProviderRepositoryAspect {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @AfterReturning(pointcut = "execution(* com.github.slamdev.ripe.business.isp.boundary.InternetServiceProviderRepository.save(..))", returning = "isp")
    public void publishEvent(InternetServiceProvider isp) {
        eventPublisher.publishEvent(new InternetServiceProviderCreationEvent(isp));
    }
}
