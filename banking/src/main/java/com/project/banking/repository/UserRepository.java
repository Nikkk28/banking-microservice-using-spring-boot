package com.project.banking.repository;


import com.project.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    @Modifying
    @Query("UPDATE User u SET u.balance = :balance WHERE u.id = :userId")
    int updateUserBalance(@Param("userId") String userId, @Param("balance") double balance);
    
    boolean existsByEmail(String email);
}
