package com.ll.goohaeyou.domain.calculate.itemProcessor;

import com.ll.goohaeyou.domain.application.entity.Application;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import static com.ll.goohaeyou.domain.application.entity.type.WageStatus.*;

@Component
public class Exceeded3DaysProcessor implements ItemProcessor<Application, Application> {
    @Override
    public Application process(@NonNull Application application) {
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