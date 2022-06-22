package com.sena.repository;

import com.sena.repository.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionRespository extends JpaRepository<Transaction, Long> {
}
