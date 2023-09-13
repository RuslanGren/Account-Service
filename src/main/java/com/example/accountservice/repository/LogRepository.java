package com.example.accountservice.repository;


import com.example.accountservice.models.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByOrderByIdAsc();
}
