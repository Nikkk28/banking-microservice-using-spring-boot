package com.project.banking.repository;


import org.hibernate.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT t FROM Transaction t JOIN FETCH t.sender JOIN FETCH t.recipient ORDER BY t.timestamp DESC")
    List<Transaction> findAllWithUsers();
    
    @Query("SELECT t FROM Transaction t WHERE t.sender.id = :userId OR t.recipient.id = :userId ORDER BY t.timestamp DESC")
    List<Transaction> findByUserId(@Param("userId") String userId);
}
