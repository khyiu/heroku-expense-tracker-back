package be.kuritsu.hetb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.kuritsu.hetb.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
}
