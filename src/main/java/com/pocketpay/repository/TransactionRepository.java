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
}
