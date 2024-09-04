package com.ll.goohaeyou.calculate.itemProcessor;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import static com.ll.goohaeyou.jobApplication.domain.type.WageStatus.*;

@Component
public class Exceeded3DaysProcessor implements ItemProcessor<JobApplication, JobApplication> {
    @Override
    public JobApplication process(@NonNull JobApplication jobApplication) {
        if (jobApplication.getWageStatus() == PAYMENT_COMPLETED) {
            jobApplication.changeToCompleted();
            jobApplication.updateWageStatus(SETTLEMENT_REQUESTED);
        } else if (jobApplication.getWageStatus() ==  APPLICATION_APPROVED ){
            jobApplication.changeToCompleted();
            jobApplication.markAsReceived(true);
            jobApplication.updateWageStatus(WAGE_PAID);
        }

        return jobApplication;
    }
}
