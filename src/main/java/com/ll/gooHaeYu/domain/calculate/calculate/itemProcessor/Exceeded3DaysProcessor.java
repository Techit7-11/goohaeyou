package com.ll.gooHaeYu.domain.calculate.calculate.itemProcessor;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import static com.ll.gooHaeYu.domain.application.application.entity.type.WageStatus.*;

@Component
public class Exceeded3DaysProcessor implements ItemProcessor<Application, Application> {

    @Override
    public Application process(Application application) throws Exception {
        System.out.println(application.getWageStatus());
        if (application.getWageStatus() == PAYMENT_COMPLETED) {
            application.changeToCompleted();
            application.updateWageStatus(SETTLEMENT_REQUESTED);
        } else if (application.getWageStatus() ==  APPLICATION_APPROVED ){
            application.changeToCompleted();
            application.setReceive(true);
            application.updateWageStatus(WAGE_PAID);
        }

        return application;
    }
}
