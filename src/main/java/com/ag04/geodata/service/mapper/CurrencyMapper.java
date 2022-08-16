package com.ag04.geodata.service.mapper;

import com.ag04.geodata.domain.*;
import com.ag04.geodata.service.dto.CurrencyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Currency} and its DTO {@link CurrencyDTO}.
 */
@Mapper(componentModel = "cdi", uses = {})
public interface CurrencyMapper extends EntityMapper<CurrencyDTO, Currency> {
    default Currency fromId(Long id) {
        if (id == null) {
            return null;
        }
        Currency currency = new Currency();
        currency.id = id;
        return currency;
    }
}
