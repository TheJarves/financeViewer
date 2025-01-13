package com.example.financeViewer;

import com.example.financeViewer.Repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FinanceViewerApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class FinanceViewerApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TransactionRepository repository;

    private static final Logger logger = Logger.getLogger(FinanceViewerApplicationTests.class.getName());

    @Test
    public void createTransaction() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\": \"2024-09-12\", \"amount\": \"100\", \"description\": \"Some other test\", \"origin\": \"Test\", \"purpose\": \"anderer testzwecke\"}"))
                .andExpect(status().isCreated())
                .andDo(handler -> {
                    String[] url = Objects.requireNonNull(handler.getResponse().getHeader("Location")).split("/");
                    repository.deleteById(Long.parseLong(url[url.length - 1]));
                });
    }

    @Test
    public void viewTransaction() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/transactions")).andExpect(status().isOk());
    }

    @Test
    public void getTransaction() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\": \"2024-09-12\", \"amount\": \"100\", \"description\": \"Some other test\", \"origin\": \"Test\", \"purpose\": \"anderer testzwecke\"}"))
                .andExpect(status().isCreated())
                .andDo(handler -> {
                    String[] url = Objects.requireNonNull(handler.getResponse().getHeader("Location")).split("/");
                    mvc.perform(MockMvcRequestBuilders.get("/transactions/" + url[url.length - 1])).andExpect(status().isOk());
                    repository.deleteById(Long.parseLong(url[url.length - 1]));
                });
        repository.deleteAll();
    }

    @Test
    public void importLegacy() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "legacy.csv",
                "text/csv",
                "amount,description,date,origin,purpose\n200,legacy test,01.03.2025,Testcase,Legacyimport".getBytes());

        mvc.perform(MockMvcRequestBuilders.multipart("/transactions/legacy")
                .file(file))
                .andExpect(status().isOk());
    }

    @Test
    public void importComdirect() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "comdirect.csv",
                "text/csv",
                "Buchungstag;Wertstellung (Valuta);Vorgang;Buchungstext;Umsatz in EUR\n09.12.2024;09.12.2024;Lastschrift / Belastung;Auftraggeber: GREEN THAI GMBH Buchungstext: Green Thai GmbH//Darmstadt/DE 2024-12-06T13:17:52 KFN 0 VJ 2412 Ref. 7Q2C1U7T32F631GD/3378;-18,80;".getBytes());

        mvc.perform(MockMvcRequestBuilders.multipart("/transactions/comdirect")
                .file(file))
                .andExpect(status().isOk());
    }

	@Test
	public void getByPurpose() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/transactions")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"date\": \"2024-09-12\", \"amount\": \"100\", \"description\": \"Some test\", \"origin\": \"Test\", \"purpose\": \"testzwecke\"}"))
				.andExpect(status().isCreated());
		mvc.perform(MockMvcRequestBuilders.post("/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"date\": \"2024-09-12\", \"amount\": \"-100\", \"description\": \"Some other test\", \"origin\": \"Test\", \"purpose\": \"anderer testzwecke\"}"))
				.andExpect(status().isCreated());

		mvc.perform(MockMvcRequestBuilders.get("/transactions/byPurpose/testzwecke"))
				.andExpect(status().isOk())
				.andDo(handler -> {
                    assertEquals(2, StringUtils.countOccurrencesOf(handler.getResponse().getContentAsString(), "testzwecke"));
				});
	}

}
