package dev.vality.wb.list.manager;

import dev.vality.wb.list.manager.listener.StartupListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.PreDestroy;

@ServletComponentScan
@SpringBootApplication
public class WbListManagerApplication extends SpringApplication {

    @Autowired
    private StartupListener startupListener;

    public static void main(String[] args) {
        SpringApplication.run(WbListManagerApplication.class, args);
    }

    @PreDestroy
    public void preDestroy() {
        startupListener.stop();
    }
}
