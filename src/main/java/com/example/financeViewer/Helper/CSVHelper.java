package com.example.financeViewer.Helper;

import com.example.financeViewer.Model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.lang.Float.parseFloat;


public class CSVHelper {
    public static String TYPE = "text/csv";

    public static boolean isCSV(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Transaction> legacyCSVtoTransactionList(MultipartFile file) throws IOException {
        if (isCSV(file)) {
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.Builder.create().setHeader().build());
                return csvParser.stream()
                        .map(record -> {
                            List<Integer> date = Arrays.stream(record.get("date").split("\\.")).map(Integer::parseInt).toList();
                                    return new Transaction(
                                            LocalDate.of(date.get(2), date.get(1), date.get(0)),
                                            parseFloat(record.get("amount")),
                                            record.get("description"),
                                            record.get("origin"),
                                            record.get("purpose"));
                                }
                        ).toList();
            } catch (IOException e) {
                throw new RuntimeException("Error while reading CSV file", e);
            }
        } else {
            throw new RuntimeException("CSV file is not a CSV file");
        }
    }

    public static List<Transaction> comdirectCSVtoTransactionList(MultipartFile file) throws IOException {
        if (isCSV(file)) {
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.Builder.create().setHeader().setDelimiter(';').build());
                return csvParser.stream()
                        .map(record -> {
                            List<Integer> date = Arrays.stream(record.get("Buchungstag").split("\\.")).map(Integer::parseInt).toList();
                            String[] info = record.get("Buchungstext").split(" Buchungstext: ");
                            return new Transaction(
                                    LocalDate.of(date.get(2), date.get(1), date.get(0)),
                                    parseFloat(record.get("Umsatz in EUR").replaceAll("\\.", "").replace(",", ".")),
                                    info[0].replace("Auftraggeber: ", "").replace("Empf�nger: ", ""),
                                    "comdirect",
                                    info[1].split(" Ref\\. ")[0]
                            );
                        }).toList();
            } catch (IOException e) {
                throw new RuntimeException("Error while reading CSV file", e);
            }
        } else {
            throw new RuntimeException("CSV file is not a CSV file");
        }
    }
}
