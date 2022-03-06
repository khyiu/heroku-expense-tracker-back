package be.kuritsu.hetb.service;

import java.util.List;

import org.springframework.lang.Nullable;

import be.kuritsu.hetb.domain.Tag;

public interface TagService {

    List<Tag> findTags(@Nullable String query);
}
