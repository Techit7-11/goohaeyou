package com.ll.gooHaeYu.domain.calculate.calculate.itemProcessor;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import static com.ll.gooHaeYu.domain.application.application.entity.type.WageStatus.PAYMENT_COMPLETED;
import static com.ll.gooHaeYu.domain.application.application.entity.type.WageStatus.SETTLEMENT_REQUESTED;

@Component
public class Processor1 implements ItemProcessor<Application, Application> {

    @Override
    public Application process(Application application) throws Exception {

        if (application.getWageStatus() == PAYMENT_COMPLETED) {
            application.changeToCompleted();
            application.updateWageStatus(SETTLEMENT_REQUESTED);
        }

        return application;
    }
}
