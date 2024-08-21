package com.demo.repositories;

import com.demo.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("select ac from Account ac where ac.username = :username and ac.password = :password")
            Account getAccount(String username, String password);
}
