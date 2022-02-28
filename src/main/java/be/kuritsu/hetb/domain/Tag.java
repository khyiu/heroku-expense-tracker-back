package be.kuritsu.hetb.domain;

import java.util.ArrayList;
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
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "het", name = "tag")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(value, tag.value) && Objects.equals(owner, tag.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, owner);
    }

    @Override
    public int compareTo(Tag otherTag) {
        return value.compareTo(otherTag.getValue());
    }
}
