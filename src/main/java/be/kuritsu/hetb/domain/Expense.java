package be.kuritsu.hetb.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "het", name = "expense")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Expense {

    @GeneratedValue
    @Id
    private UUID id;

    @Column(name = "owner")
    private String owner;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "paidWithCreditCard")
    private Boolean paidWithCreditCard;

    @Column(name = "creditCardStatementIssued")
    private Boolean creditCardStatementIssued;

    @ElementCollection
    @CollectionTable(schema = "het", name = "expense_tag", joinColumns = @JoinColumn(name = "expense_id", referencedColumnName = "id"))
    @Column(name = "value")
    private Set<String> tags = new TreeSet<>();

    @Column(name = "\"order\"")
    private Integer order;

    public void setTags(@NonNull Set<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(this.tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Expense expense = (Expense) o;
        return owner.equals(expense.owner)
                && date.equals(expense.date)
                && amount.equals(expense.amount)
                && Objects.equals(description, expense.description)
                && Objects.equals(paidWithCreditCard, expense.paidWithCreditCard)
                && Objects.equals(creditCardStatementIssued, expense.creditCardStatementIssued)
                && tags.equals(expense.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, date, amount, description, paidWithCreditCard, creditCardStatementIssued, tags);
    }
}
