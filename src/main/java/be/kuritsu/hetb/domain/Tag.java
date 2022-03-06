package be.kuritsu.hetb.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(schema = "het", name = "tag")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(of = {"value", "owner"})
public class Tag implements Comparable<Tag> {

    @GeneratedValue
    @Id
    private UUID id;

    @Column(name = "value")
    private String value;

    @Column(name = "owner")
    private String owner;

    @ManyToMany(mappedBy = "tags")
    private List<Expense> expenses = new ArrayList<>();

    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) && value.equals(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }

    @Override
    public int compareTo(Tag otherTag) {
        return value.compareTo(otherTag.getValue());
    }
}
