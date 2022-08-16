package com.ag04.geodata.service.mapper;

import com.ag04.geodata.domain.*;
import com.ag04.geodata.service.dto.CountryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Country} and its DTO {@link CountryDTO}.
 */
@Mapper(componentModel = "cdi", uses = {})
public interface CountryMapper extends EntityMapper<CountryDTO, Country> {
    default Country fromId(Long id) {
        if (id == null) {
            return null;
        }
        Country country = new Country();
        country.id = id;
        return country;
    }
}
