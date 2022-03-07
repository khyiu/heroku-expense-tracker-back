package be.kuritsu.hetb.controller;

import static be.kuritsu.hetb.config.SecurityConfig.ROLE_EXPENSE_TRACKER_USER;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import be.kuritsu.het.api.TagsApi;
import be.kuritsu.het.model.Tag;
import be.kuritsu.hetb.mapper.TagMapper;
import be.kuritsu.hetb.service.TagService;

@RestController
public class TagController implements TagsApi {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @Autowired
    public TagController(TagService tagService, TagMapper tagMapper) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<List<Tag>> getTags(String query) {
        List<be.kuritsu.hetb.domain.Tag> tags = tagService.findTags(query);
        List<Tag> queriedTags = tags.stream()
                .map(tagMapper::tagEntityToTag)
                .collect(Collectors.toList());
        return ResponseEntity.ok(queriedTags);
    }
}
