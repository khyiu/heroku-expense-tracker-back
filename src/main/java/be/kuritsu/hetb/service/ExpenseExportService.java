package be.kuritsu.hetb.service;

import java.io.PrintStream;

public interface ExpenseExportService {

    void exportExpenses(PrintStream printStream);
}
