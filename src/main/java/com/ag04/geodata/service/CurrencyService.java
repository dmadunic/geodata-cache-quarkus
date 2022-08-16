package com.ag04.geodata.service;

import com.ag04.geodata.service.dto.CurrencyDTO;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ag04.geodata.domain.Currency}.
 */
public interface CurrencyService {
    /**
     * Save a currency.
     *
     * @param currencyDTO the entity to save.
     * @return the persisted entity.
     */
    CurrencyDTO persistOrUpdate(CurrencyDTO currencyDTO);

    /**
     * Delete the "id" currencyDTO.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all the currencies.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<CurrencyDTO> findAll(Page page, Sort sort);

    /**
     * Get the "id" currencyDTO.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CurrencyDTO> findOne(Long id);
}
