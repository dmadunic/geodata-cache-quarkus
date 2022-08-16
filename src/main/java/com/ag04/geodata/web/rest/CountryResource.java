package com.ag04.geodata.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.ag04.geodata.service.CountryService;
import com.ag04.geodata.service.Paged;
import com.ag04.geodata.service.dto.CountryDTO;
import com.ag04.geodata.web.rest.errors.BadRequestAlertException;
import com.ag04.geodata.web.rest.vm.PageRequestVM;
import com.ag04.geodata.web.rest.vm.SortRequestVM;
import com.ag04.geodata.web.util.HeaderUtil;
import com.ag04.geodata.web.util.PaginationUtil;
import com.ag04.geodata.web.util.ResponseUtil;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing {@link com.ag04.geodata.domain.Country}.
 */
@Path("/api/countries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CountryResource {

    private final Logger log = LoggerFactory.getLogger(CountryResource.class);

    private static final String ENTITY_NAME = "country";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    CountryService countryService;

    /**
     * {@code POST  /countries} : Create a new country.
     *
     * @param countryDTO the countryDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new countryDTO, or with status {@code 400 (Bad Request)} if the country has already an ID.
     */
    @POST
    public Response createCountry(@Valid CountryDTO countryDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save Country : {}", countryDTO);
        if (countryDTO.id != null) {
            throw new BadRequestAlertException("A new country cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = countryService.persistOrUpdate(countryDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /countries} : Updates an existing country.
     *
     * @param countryDTO the countryDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated countryDTO,
     * or with status {@code 400 (Bad Request)} if the countryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the countryDTO couldn't be updated.
     */
    @PUT
    @Path("/{id}")
    public Response updateCountry(@Valid CountryDTO countryDTO, @PathParam("id") Long id) {
        log.debug("REST request to update Country : {}", countryDTO);
        if (countryDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = countryService.persistOrUpdate(countryDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, countryDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /countries/:id} : delete the "id" country.
     *
     * @param id the id of the countryDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCountry(@PathParam("id") Long id) {
        log.debug("REST request to delete Country : {}", id);
        countryService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /countries} : get all the countries.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of countries in body.
     */
    @GET
    public Response getAllCountries(@BeanParam PageRequestVM pageRequest, @BeanParam SortRequestVM sortRequest, @Context UriInfo uriInfo) {
        log.debug("REST request to get a page of Countries");
        var page = pageRequest.toPage();
        var sort = sortRequest.toSort();
        Paged<CountryDTO> result = countryService.findAll(page, sort);
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }

    /**
     * {@code GET  /countries/:id} : get the "id" country.
     *
     * @param id the id of the countryDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the countryDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    public Response getCountry(@PathParam("id") Long id) {
        log.debug("REST request to get Country : {}", id);
        Optional<CountryDTO> countryDTO = countryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(countryDTO);
    }
}
