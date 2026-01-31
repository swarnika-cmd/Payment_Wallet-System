package com.pocketpay.specification;

import com.pocketpay.dto.TransactionSearchCriteria;
import com.pocketpay.entity.Transaction;
import com.pocketpay.entity.Wallet;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecifications {

    public static Specification<Transaction> getTransactionsByCriteria(Wallet userWallet,
            TransactionSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Base Logic: User must be SENDER or RECEIVER
            // (fromWallet == userWallet OR toWallet == userWallet)
            Predicate isSender = criteriaBuilder.equal(root.get("fromWallet"), userWallet);
            Predicate isReceiver = criteriaBuilder.equal(root.get("toWallet"), userWallet);
            predicates.add(criteriaBuilder.or(isSender, isReceiver));

            // 2. Date Range
            if (criteria.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), criteria.getEndDate()));
            }

            // 3. Amount Range
            if (criteria.getMinAmount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), criteria.getMinAmount()));
            }
            if (criteria.getMaxAmount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), criteria.getMaxAmount()));
            }

            // 4. Type
            if (criteria.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), criteria.getType()));
            }

            // 5. Status
            if (criteria.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // Combine all with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
