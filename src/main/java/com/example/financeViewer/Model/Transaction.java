package com.example.financeViewer.Model;

import com.example.financeViewer.Repository.MonthReport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@NamedNativeQuery(name = "Transaction.getMonthReports",
        query = "SELECT sum(amount) as total, date_part('month', date) as month, date_part('year', date) as year FROM transaction GROUP BY date_part('month', date), date_part('year', date) ORDER BY date_part('year', date), date_part('month', date)",
        resultSetMapping = "Mapping.MonthReport")
@SqlResultSetMapping(name = "Mapping.MonthReport",
        classes = @ConstructorResult(targetClass = MonthReport.class,
                columns = {@ColumnResult(name = "total"),
                        @ColumnResult(name = "month"),
                        @ColumnResult(name = "year")
                }))
@Entity
@Getter
@Setter
public class Transaction {

    private @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TransactionGenerator")
    @SequenceGenerator(name="TransactionGenerator", allocationSize=1)
    Long id;
    private Float amount;
    private String description;
    private LocalDate date;
    private String origin;
    private String purpose;

    public Transaction(LocalDate date, Float amount, String description, String origin, String purpose) {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.origin = origin;
        this.purpose = purpose;
    }

    public Transaction() {
    }


    @Override
    public String toString() {

        return "Transaction [id=" + id
                + ", amount=" + amount
                + ", description=" + description
                + ", date=" + date
                + ", origin=" + origin
                + ", purpose=" + purpose
                + "]";
    }

    public Boolean equalsTransaction(Transaction otherTransaction) {
        return this.compare(otherTransaction) && id.equals(otherTransaction.getId());
    }

    public Boolean compare(Transaction otherTransaction) {
        return otherTransaction.getAmount().compareTo(amount) == 0
                && otherTransaction.getDescription().equals(description)
                && otherTransaction.getOrigin().equals(origin)
                && otherTransaction.getPurpose().equals(purpose)
                && otherTransaction.getDate().equals(date);
    }
}
