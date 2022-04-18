package be.kuritsu.hetb.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "het", name = "expense")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = { "id" })
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

    @OrderBy("value ASC")
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(schema = "het", name = "expense_tag",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new TreeSet<>();

    @Column(name = "checked")
    private Boolean checked;

    @Column(name = "\"order\"")
    private Integer order;

    public void setTags(@NonNull Set<Tag> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public void addTag(@NonNull Tag tag) {
        tags.add(tag);
    }

    public void removeTag(@NonNull Tag tag) {
        tags.remove(tag);
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", version=" + version +
                ", date=" + date +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", paidWithCreditCard=" + paidWithCreditCard +
                ", creditCardStatementIssued=" + creditCardStatementIssued +
                ", tags=" + tags +
                ", order=" + order +
                ", checked=" + checked +
                '}';
    }
}
