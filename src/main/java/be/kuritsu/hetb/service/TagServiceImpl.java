package be.kuritsu.hetb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import be.kuritsu.hetb.domain.Tag;
import be.kuritsu.hetb.repository.TagRepository;
import be.kuritsu.hetb.repository.TagSpecification;
import be.kuritsu.hetb.security.SecurityContextService;

@Service
public class TagServiceImpl implements TagService {

    private final SecurityContextService securityContextService;
    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(SecurityContextService securityContextService, TagRepository tagRepository) {
        this.securityContextService = securityContextService;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> findTags(@Nullable String query) {
        String ownerName = securityContextService.getAuthenticatedUserName();
        TagSpecification tagSpecification = new TagSpecification(ownerName, query);
        return tagRepository.findAll(tagSpecification, Sort.by(Sort.Direction.ASC, "value"));
    }
}
