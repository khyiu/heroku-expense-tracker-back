package be.kuritsu.hetb.service;

import java.io.InputStream;

public interface ExpenseImportService {

    void importExpenses(InputStream importFileContent);
}
