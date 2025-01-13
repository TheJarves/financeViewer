package com.example.financeViewer.Repository;

import com.example.financeViewer.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public List<Transaction> findByPurpose(String purpose);

    @Query(nativeQuery = true)
    public List<MonthReport> getMonthReports();

}

