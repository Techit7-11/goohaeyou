package com.ll.gooHaeYu.domain.calculate.calculate.itemProcessor;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class Processor1 implements ItemProcessor<Application, Application> {

    @Override
    public Application process(Application application) throws Exception {

        System.out.println("------------------");
        return application;
    }
}
