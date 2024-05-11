package com.mrsisa.pharmacy.tasks;

import com.mrsisa.pharmacy.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableAsync
@Component
public class EndPromotions {
    private final IPromotionService promotionService;

    @Autowired
    public EndPromotions(IPromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void endPromotionAndResetPrices() {
        promotionService.endExpiredPromotions();
    }
}
