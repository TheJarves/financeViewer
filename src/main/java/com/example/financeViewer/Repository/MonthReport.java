package com.example.financeViewer.Repository;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthReport {
    int month;
    int year;
    float total;

    public MonthReport(Float total, Double month, Double year) {
        this.month = month.intValue();
        this.total = total;
        this.year = year.intValue();
    }

    public void setMonth(Double month) {
        this.month = month.intValue();
    }

    public void setYear(Double year) {
        this.year = year.intValue();
    }
}
