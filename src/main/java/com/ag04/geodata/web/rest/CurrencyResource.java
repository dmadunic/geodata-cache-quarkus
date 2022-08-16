package com.ag04.geodata.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.ag04.geodata.service.CurrencyService;
import com.ag04.geodata.service.Paged;
import com.ag04.geodata.service.dto.CurrencyDTO;
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
 * REST controller for managing {@link com.ag04.geodata.domain.Currency}.
 */
@Path("/api/currencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CurrencyResource {

    private final Logger log = LoggerFactory.getLogger(CurrencyResource.class);

    private static final String ENTITY_NAME = "currency";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    CurrencyService currencyService;

    /**
     * {@code POST  /currencies} : Create a new currency.
     *
     * @param currencyDTO the currencyDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new currencyDTO, or with status {@code 400 (Bad Request)} if the currency has already an ID.
     */
    @POST
    public Response createCurrency(@Valid CurrencyDTO currencyDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save Currency : {}", currencyDTO);
        if (currencyDTO.id != null) {
            throw new BadRequestAlertException("A new currency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = currencyService.persistOrUpdate(currencyDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /currencies} : Updates an existing currency.
     *
     * @param currencyDTO the currencyDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated currencyDTO,
     * or with status {@code 400 (Bad Request)} if the currencyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the currencyDTO couldn't be updated.
     */
    @PUT
    @Path("/{id}")
    public Response updateCurrency(@Valid CurrencyDTO currencyDTO, @PathParam("id") Long id) {
        log.debug("REST request to update Currency : {}", currencyDTO);
        if (currencyDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = currencyService.persistOrUpdate(currencyDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, currencyDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /currencies/:id} : delete the "id" currency.
     *
     * @param id the id of the currencyDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCurrency(@PathParam("id") Long id) {
        log.debug("REST request to delete Currency : {}", id);
        currencyService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /currencies} : get all the currencies.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of currencies in body.
     */
    @GET
    public Response getAllCurrencies(@BeanParam PageRequestVM pageRequest, @BeanParam SortRequestVM sortRequest, @Context UriInfo uriInfo) {
        log.debug("REST request to get a page of Currencies");
        var page = pageRequest.toPage();
        var sort = sortRequest.toSort();
        Paged<CurrencyDTO> result = currencyService.findAll(page, sort);
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }

    /**
     * {@code GET  /currencies/:id} : get the "id" currency.
     *
     * @param id the id of the currencyDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the currencyDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    public Response getCurrency(@PathParam("id") Long id) {
        log.debug("REST request to get Currency : {}", id);
        Optional<CurrencyDTO> currencyDTO = currencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(currencyDTO);
    }
}
