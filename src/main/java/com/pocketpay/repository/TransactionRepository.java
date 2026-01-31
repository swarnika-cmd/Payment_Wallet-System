package com.pocketpay.repository;

import com.pocketpay.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<Transaction> {
    // Find all transactions where mobile is SENDER OR RECEIVER with Pagination
    Page<Transaction> findBySenderMobileOrReceiverMobile(String senderMobile, String receiverMobile, Pageable pageable);

    // Analytics: Total Amount for a given day
    @org.springframework.data.jpa.repository.Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.timestamp BETWEEN :start AND :end AND t.status = 'SUCCESS'")
    java.math.BigDecimal getTotalTransactionVolume(java.time.LocalDateTime start, java.time.LocalDateTime end);

    // Analytics: Count by Type
    @org.springframework.data.jpa.repository.Query("SELECT t.type, COUNT(t) FROM Transaction t GROUP BY t.type")
    java.util.List<Object[]> getTransactionTypeDistribution();
}
