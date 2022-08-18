package com.ag04.geodata.service.impl;

import com.ag04.geodata.cache.annotations.Cached;
import com.ag04.geodata.domain.Country;
import com.ag04.geodata.service.CountryService;
import com.ag04.geodata.service.Paged;
import com.ag04.geodata.service.dto.CountryDTO;
import com.ag04.geodata.service.mapper.CountryMapper;
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
public class CountryServiceImpl implements CountryService {

    private final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);

    @Inject
    CountryMapper countryMapper;

    @Override
    @Transactional
    public CountryDTO persistOrUpdate(CountryDTO countryDTO) {
        log.debug("Request to save Country : {}", countryDTO);
        var country = countryMapper.toEntity(countryDTO);
        country = Country.persistOrUpdate(country);
        return countryMapper.toDto(country);
    }

    /**
     * Delete the Country by id.
     *
     * @param id the id of the entity.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Country : {}", id);
        Country
            .findByIdOptional(id)
            .ifPresent(country -> {
                country.delete();
            });
    }

    /**
     * Get one country by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Cached(cacheName = "com.ag04.geodata.service.dto.CountryDTO")
    public Optional<CountryDTO> findOne(Long id) {
        log.debug("Request to get Country : {}", id);
        return Country.findByIdOptional(id).map(country -> countryMapper.toDto((Country) country));
    }

    /**
     * Get all the countries.
     * @param page the pagination information.
     * @return the list of entities.
     */
    @Override
    public Paged<CountryDTO> findAll(Page page, Sort sort) {
        log.debug("Request to get all Countries");
        return new Paged<>(Country.findAll(sort).page(page)).map(country -> countryMapper.toDto((Country) country));
    }
}
