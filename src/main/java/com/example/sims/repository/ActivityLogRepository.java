package com.example.sims.repository;


import com.example.sims.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop20ByOrderByCreatedAtDesc();

    @Query("SELECT al FROM ActivityLog al JOIN FETCH al.user ORDER BY al.createdAt DESC")
    List<ActivityLog> findAllWithUser();
}