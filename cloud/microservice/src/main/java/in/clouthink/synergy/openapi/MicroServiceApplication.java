package in.clouthink.synergy.openapi;

import in.clouthink.daas.audit.annotation.EnableAudit;
import in.clouthink.daas.audit.configure.AuditConfigurer;
import in.clouthink.daas.audit.spi.AuditEventPersister;
import in.clouthink.synergy.security.impl.audit.AuditEventPersisterImpl;
import in.clouthink.synergy.security.impl.audit.SecurityContextAuditImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.context.WebApplicationContext;

@SpringBootApplication
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@Import({MicroServiceWebMvcConfigurer.class})
@EnableEurekaClient
@EnableAsync
@EnableAudit
public class MicroServiceApplication extends SpringBootServletInitializer {

    @Bean
    public AuditEventPersister auditEventPersisterImpl() {
        return new AuditEventPersisterImpl();
    }

    @Bean
    public AuditConfigurer auditConfigurer() {
        return result -> {
            result.setSecurityContext(new SecurityContextAuditImpl());
            result.setAuditEventPersister(auditEventPersisterImpl());
            result.setErrorDetailRequired(true);
        };
    }

    @Override
    protected WebApplicationContext run(SpringApplication application) {
        application.getSources().remove(ErrorPageFilter.class);
        return super.run(application);
    }

    public static void main(String[] args) {
        SpringApplication.run(new Object[]{MicroServiceApplication.class}, args);
    }

}
