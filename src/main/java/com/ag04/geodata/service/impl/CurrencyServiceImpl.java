package com.ag04.geodata.service.impl;

import com.ag04.geodata.domain.Currency;
import com.ag04.geodata.service.CurrencyService;
import com.ag04.geodata.service.Paged;
import com.ag04.geodata.service.dto.CurrencyDTO;
import com.ag04.geodata.service.mapper.CurrencyMapper;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private final Logger log = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    @Inject
    CurrencyMapper currencyMapper;

    @Override
    @Transactional
    public CurrencyDTO persistOrUpdate(CurrencyDTO currencyDTO) {
        log.debug("Request to save Currency : {}", currencyDTO);
        var currency = currencyMapper.toEntity(currencyDTO);
        currency = Currency.persistOrUpdate(currency);
        return currencyMapper.toDto(currency);
    }

    /**
     * Delete the Currency by id.
     *
     * @param id the id of the entity.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Currency : {}", id);
        Currency
            .findByIdOptional(id)
            .ifPresent(currency -> {
                currency.delete();
            });
    }

    /**
     * Get one currency by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    public Optional<CurrencyDTO> findOne(Long id) {
        log.debug("Request to get Currency : {}", id);
        return Currency.findByIdOptional(id).map(currency -> currencyMapper.toDto((Currency) currency));
    }

    /**
     * Get all the currencies.
     * @param page the pagination information.
     * @return the list of entities.
     */
    @Override
    public Paged<CurrencyDTO> findAll(Page page, Sort sort) {
        log.debug("Request to get all Currencies");
        return new Paged<>(Currency.findAll(sort).page(page)).map(currency -> currencyMapper.toDto((Currency) currency));
    }
}
