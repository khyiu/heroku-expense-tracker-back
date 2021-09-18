package be.kuritsu.testutil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;

import be.kuritsu.het.model.ExpenseRequest;

public class ExpenseRequestFactory {
    private static final LocalDate MIN_DATE = LocalDate.of(2000, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(2099, 12, 31);
    private static final double MIN_AMOUNT = -200.000;
    private static final double MAX_AMOUNT = 200.000;
    private static final int DESCRIPTION_MIN_LENGTH = 1;
    private static final int DESCRIPTION_MAX_LENGTH = 1024;

    private ExpenseRequestFactory() {
    }

    public static ExpenseRequest getRandomValidExpenseRequest() {
        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setDate(getRandomDateBetween(MIN_DATE, MAX_DATE));
        expenseRequest.setAmount(getRandomAmount(MIN_AMOUNT, MAX_AMOUNT));
        expenseRequest.setDescription(getRandomString(DESCRIPTION_MIN_LENGTH, DESCRIPTION_MAX_LENGTH));

        boolean paidWithCreditCard = ThreadLocalRandom.current().nextBoolean();
        expenseRequest.setPaidWithCreditCard(paidWithCreditCard);

        if (paidWithCreditCard) {
            expenseRequest.setCreditCardStatementIssued(ThreadLocalRandom.current().nextBoolean());
        }

        expenseRequest.setTags(Collections.singletonList(getRandomString(1, 50)));
        return expenseRequest;
    }

    public static ExpenseRequest getExpenseRequest(LocalDate date, BigDecimal amount, List<String> tags, int lengthOfRandomDescription) {
        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setDate(date);
        expenseRequest.setAmount(amount);
        expenseRequest.setTags(tags);
        expenseRequest.setDescription(lengthOfRandomDescription == 0 ?
                null :
                RandomStringUtils.random(lengthOfRandomDescription));

        boolean paidWithCreditCard = ThreadLocalRandom.current().nextBoolean();
        expenseRequest.setPaidWithCreditCard(paidWithCreditCard);

        if (paidWithCreditCard) {
            expenseRequest.setCreditCardStatementIssued(ThreadLocalRandom.current().nextBoolean());
        }

        return expenseRequest;
    }

    public static ExpenseRequest getExpenseRequest(LocalDate date,
            BigDecimal amount,
            List<String> tags,
            String description,
            boolean paidWithCreditCard,
            boolean creditCardStatementIssued) {
        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setDate(date);
        expenseRequest.setAmount(amount);
        expenseRequest.setTags(tags);
        expenseRequest.setDescription(description);
        expenseRequest.setPaidWithCreditCard(paidWithCreditCard);
        expenseRequest.setCreditCardStatementIssued(creditCardStatementIssued);

        return expenseRequest;
    }

    private static LocalDate getRandomDateBetween(LocalDate startInclusive, LocalDate endExclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);

        return LocalDate.ofEpochDay(randomDay);
    }

    private static BigDecimal getRandomAmount(double lowerBound, double upperBound) {
        double amount = ThreadLocalRandom.current()
                .nextDouble(lowerBound, upperBound);
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_EVEN);
    }

    private static String getRandomString(int minLength, int maxLength) {
        int randomStringLength = ThreadLocalRandom.current()
                .nextInt(minLength, maxLength);
        return RandomStringUtils.random(randomStringLength, true, true);
    }
}
