package com.example.financeViewer.Controller;

import com.example.financeViewer.Model.Transaction;
import com.example.financeViewer.Services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(CollectionModel.of(transactionService.getAllTransactions(), linkTo(methodOn(TransactionController.class).all()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Long id) {
        Optional<EntityModel<Transaction>> transaction = transactionService.getTransactionById(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/byPurpose/{purpose}")
    public ResponseEntity<?> purpose(@PathVariable String purpose) {
        return ResponseEntity.ok(CollectionModel.of(transactionService.getTransactionsByPurpose(purpose), linkTo(methodOn(TransactionController.class).purpose(purpose)).withSelfRel()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (transactionService.deleteTransactionById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Transaction newTransaction) {
        EntityModel<Transaction> savedTransaction = transactionService.createTransaction(newTransaction);
        return ResponseEntity.created(savedTransaction.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(savedTransaction);
    }

    @PostMapping("/legacy")
    public ResponseEntity<?> legacy(@RequestParam("file") MultipartFile file) {
        if (transactionService.importLegacyCSVtoTransactions(file)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/comdirect")
    public ResponseEntity<?> comdirect(@RequestParam("file") MultipartFile file) {
        if (transactionService.importComdirectCSVtoTransactions(file)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/monthreports")
    public ResponseEntity<?> getMonthReports() {
        return ResponseEntity.ok(transactionService.getMonthReports());
    }

}
