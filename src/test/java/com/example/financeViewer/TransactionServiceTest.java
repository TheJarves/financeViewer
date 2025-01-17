package com.example.financeViewer;

import com.example.financeViewer.Model.Transaction;
import com.example.financeViewer.Services.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FinanceViewerApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    private Transaction getTestTransaction() {
        return new Transaction(
                LocalDate.now(),
                200.0F,
                "Testing transactionService",
                "Unit test",
                "Tests"
        );
    }

    @Test
    public void testCreateTransaction() {
        assertTrue(
                Objects.requireNonNull(transactionService
                        .createTransaction(getTestTransaction())
                        .getContent()
                ).compare(getTestTransaction())
        );
    }

    @Test
    public void testGetAllTransactions() {
        EntityModel<Transaction> transactionEntityModel = transactionService.createTransaction(getTestTransaction());
        assertTrue(
                Objects.requireNonNull(transactionService.getAllTransactions().getLast().getContent())
                        .equalsTransaction(transactionEntityModel.getContent())
        );
    }

    @Test
    public void testGetTransactionById() {
        EntityModel<Transaction> transactionEntityModel = transactionService.createTransaction(getTestTransaction());
        assertTrue(
                Objects.requireNonNull(transactionService
                        .getTransactionById(Objects.requireNonNull(transactionEntityModel.getContent()).getId())
                        .orElseGet(Assertions::fail)
                        .getContent()
                ).equalsTransaction(transactionEntityModel.getContent())
        );
    }

    @Test
    public void testGetTransactionsByPurpose() {
        EntityModel<Transaction> transactionEntityModel = transactionService.createTransaction(getTestTransaction());
        assertTrue(
                Objects.requireNonNull(transactionService
                        .getTransactionsByPurpose(Objects.requireNonNull(transactionEntityModel.getContent()).getPurpose())
                        .getFirst()
                        .getContent()
                ).equalsTransaction(transactionEntityModel.getContent())
        );
    }

    @Test
    public void testDeleteTransaction() {
        transactionService.createTransaction(getTestTransaction());
        assertTrue(transactionService.deleteTransactionById(1L));
    }

    @Test
    public void testImportLegacyCSVtoTransactions() {
        MultipartFile multipartFileSuccess = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "amount,description,date,origin,purpose\n200,Service Test,20.11.2023,Testcase,Unittest".getBytes());

        MultipartFile multipartFileFailure = new MockMultipartFile(
                "file",
                "test.tsv",
                "text/tsv",
                "amount,description,date,origin,purpose\n200,Service Test,20.11.2023,Testcase,Unittest".getBytes());

        assertTrue(transactionService.importLegacyCSVtoTransactions(multipartFileSuccess));
        assertFalse(transactionService.importLegacyCSVtoTransactions(multipartFileFailure));
    }

    @Test
    public void testImportComdirectCSVtoTransactions() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "comdirect.csv",
                "text/csv",
                "Buchungstag;Wertstellung (Valuta);Vorgang;Buchungstext;Umsatz in EUR\n09.12.2024;09.12.2024;Lastschrift / Belastung;Auftraggeber: GREEN THAI GMBH Buchungstext: Green Thai GmbH//Darmstadt/DE 2024-12-06T13:17:52 KFN 0 VJ 2412 Ref. 7Q2C1U7T32F631GD/3378;-18,80;".getBytes());

        assertTrue(transactionService.importComdirectCSVtoTransactions(file));
    }
//
//    @Test
//    public void testGetMonthReports() {
//        transactionService.createTransaction(testTransaction);
//        MonthReport expectedMonthReport = new MonthReport(
//                200.0F,
//                (double) LocalDate.now().getMonthValue(),
//                (double) LocalDate.now().getYear());
//        assertSame(transactionService.getMonthReports().getFirst(), expectedMonthReport);
//    }
}
