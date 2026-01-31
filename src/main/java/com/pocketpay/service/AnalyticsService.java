package com.pocketpay.service;

import com.pocketpay.enums.TransactionType;
import com.pocketpay.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;

    public AnalyticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public BigDecimal getDailyTransactionVolume(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        BigDecimal total = transactionRepository.getTotalTransactionVolume(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Map<String, Long> getTransactionTypeDistribution() {
        List<Object[]> results = transactionRepository.getTransactionTypeDistribution();
        Map<String, Long> distribution = new HashMap<>();

        for (Object[] row : results) {
            TransactionType type = (TransactionType) row[0];
            Long count = (Long) row[1];
            distribution.put(type.name(), count);
        }
        return distribution;
    }
}
