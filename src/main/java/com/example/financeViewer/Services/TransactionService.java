package com.example.financeViewer.Services;

import com.example.financeViewer.Assembler.TransactionModelAssembler;
import com.example.financeViewer.Helper.CSVHelper;
import com.example.financeViewer.Model.Transaction;
import com.example.financeViewer.Repository.MonthReport;
import com.example.financeViewer.Repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionModelAssembler transactionModelAssembler;
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository transactionRepository, TransactionModelAssembler transactionModelAssembler) {
        this.transactionRepository = transactionRepository;
        this.transactionModelAssembler = transactionModelAssembler;
    }

    public List<EntityModel<Transaction>> getAllTransactions() {
        return transactionRepository.findAll().stream().map(transactionModelAssembler::toModel).collect(Collectors.toList());
    }

    public Optional<EntityModel<Transaction>> getTransactionById(Long id) {
        return transactionRepository.findById(id).map(transactionModelAssembler::toModel);
    }

    public List<EntityModel<Transaction>> getTransactionsByPurpose(String purpose) {
        return transactionRepository.findByPurpose(purpose).stream().map(transactionModelAssembler::toModel).collect(Collectors.toList());
    }

    public boolean deleteTransactionById(Long id) {
        transactionRepository.deleteById(id);
        return transactionRepository.findById(id).isEmpty();
    }

    public EntityModel<Transaction> createTransaction(Transaction transaction) {
        return transactionModelAssembler.toModel(transactionRepository.save(transaction));
    }

    public boolean importLegacyCSVtoTransactions(MultipartFile file) {
        try {
            List<Transaction> importList = CSVHelper.legacyCSVtoTransactionList(file);
            transactionRepository.saveAll(importList);
            return true;
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean importComdirectCSVtoTransactions(MultipartFile file) {
        try {
            List<Transaction> importList = CSVHelper.comdirectCSVtoTransactionList(file);
            transactionRepository.saveAll(importList);
            return true;
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage() + "\n");
            return false;
        }
    }

    public List<MonthReport> getMonthReports() {
        return transactionRepository.getMonthReports();
    }
}
