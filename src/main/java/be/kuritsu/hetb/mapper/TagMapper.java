package be.kuritsu.hetb.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import be.kuritsu.hetb.domain.Tag;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TagMapper {

    Tag expenseRequestToRequest(be.kuritsu.het.model.Tag tag);
}
