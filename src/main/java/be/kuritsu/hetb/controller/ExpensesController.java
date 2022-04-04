package be.kuritsu.hetb.controller;

import static be.kuritsu.hetb.config.SecurityConfig.ROLE_EXPENSE_TRACKER_USER;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import be.kuritsu.het.api.ExpenseApi;
import be.kuritsu.het.api.ExpensesApi;
import be.kuritsu.het.model.ExpenseListResponse;
import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.hetb.exception.TechnicalException;
import be.kuritsu.hetb.service.ExpenseExportService;
import be.kuritsu.hetb.service.ExpenseImportService;
import be.kuritsu.hetb.service.ExpenseService;

@RestController
public class ExpensesController implements ExpensesApi, ExpenseApi {

    private final ExpenseService expenseService;
    private final ExpenseImportService expenseImportService;
    private final ExpenseExportService expenseExportService;

    @Autowired
    public ExpensesController(ExpenseService expenseService, ExpenseImportService expenseImportService, ExpenseExportService expenseExportService) {
        this.expenseService = expenseService;
        this.expenseImportService = expenseImportService;
        this.expenseExportService = expenseExportService;
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<ExpenseResponse> registerExpense(ExpenseRequest expenseRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseService.registerExpense(expenseRequest));
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<ExpenseResponse> getExpense(String id) {
        return ResponseEntity.ok(expenseService.getExpense(UUID.fromString(id)));
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<ExpenseResponse> updateExpense(String id, ExpenseRequest expenseRequest) {
        return ResponseEntity.ok(expenseService.updateExpense(UUID.fromString(id), expenseRequest));
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<Void> deleteExpense(String id) {
        expenseService.deleteExpense(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<Resource> exportExpenses() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
            expenseExportService.exportExpenses(printStream);

            ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

            String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                    .replaceAll("[:.]", "_");
            String proposedFilename = String.format("expense_tracker_export_%s.csv", formattedDate);
            String contentDispositionHeaderValue = String.format("attachment; filename=\"%s\"", proposedFilename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionHeaderValue)
                    .body(resource);
        } catch (IOException e) {
            throw new TechnicalException("An error happened during export of expenses", e);
        }
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<ExpenseListResponse> getExpenses(Integer pageSize,
            Integer pageNumber,
            String sortDirection,
            String sortBy,
            List<String> tagFilters,
            String descriptionFilter,
            Boolean paidWithCreditCardFilter,
            Boolean creditCardStatementIssuedFilter) {
        ExpenseService.ExpenseListRequest expenseListRequest = new ExpenseService.ExpenseListRequest(pageSize,
                pageNumber,
                ExpenseService.SortDirection.valueOf(sortDirection),
                ExpenseService.SortBy.valueOf(sortBy))
                .tagFilters(tagFilters)
                .descriptionFilter(descriptionFilter)
                .paidWithCreditCardFilter(paidWithCreditCardFilter)
                .creditCardStatementIssuedFilter(creditCardStatementIssuedFilter);

        return ResponseEntity.ok(expenseService.getExpenses(expenseListRequest));
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<Void> importExpenses(MultipartFile file) {
        try {
            expenseImportService.importExpenses(file.getInputStream());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
