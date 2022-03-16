package be.kuritsu.hetb.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import be.kuritsu.hetb.domain.Tag;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TagMapper {

    default Tag tagToTagEntity(be.kuritsu.het.model.Tag tag) {
        return Tag.builder()
                .value(StringUtils.stripAccents(tag.getValue()))
                .build();
    }

    be.kuritsu.het.model.Tag tagEntityToTag(Tag tag);
}
