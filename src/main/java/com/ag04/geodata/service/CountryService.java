package com.ag04.geodata.service;

import com.ag04.geodata.service.dto.CountryDTO;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ag04.geodata.domain.Country}.
 */
public interface CountryService {
    /**
     * Save a country.
     *
     * @param countryDTO the entity to save.
     * @return the persisted entity.
     */
    CountryDTO persistOrUpdate(CountryDTO countryDTO);

    /**
     * Delete the "id" countryDTO.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all the countries.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<CountryDTO> findAll(Page page, Sort sort);

    /**
     * Get the "id" countryDTO.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CountryDTO> findOne(Long id);
}
