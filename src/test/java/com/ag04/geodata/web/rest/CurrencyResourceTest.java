package com.ag04.geodata.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ag04.geodata.TestUtil;
import com.ag04.geodata.service.dto.CurrencyDTO;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import javax.inject.Inject;
import liquibase.Liquibase;
import org.junit.jupiter.api.*;

@QuarkusTest
public class CurrencyResourceTest {

    private static final TypeRef<CurrencyDTO> ENTITY_TYPE = new TypeRef<>() {};

    private static final TypeRef<List<CurrencyDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {};

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_CODE = "AAA";
    private static final String UPDATED_NUM_CODE = "BBB";

    private static final Boolean DEFAULT_PREFERRED = false;
    private static final Boolean UPDATED_PREFERRED = true;

    String adminToken;

    CurrencyDTO currencyDTO;

    @Inject
    LiquibaseFactory liquibaseFactory;

    @BeforeAll
    static void jsonMapper() {
        RestAssured.config =
            RestAssured.config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(TestUtil.jsonbObjectMapper()));
    }

    @BeforeEach
    public void authenticateAdmin() {
        this.adminToken = TestUtil.getAdminToken();
    }

    @BeforeEach
    public void databaseFixture() {
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            liquibase.dropAll();
            liquibase.validate();
            liquibase.update(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CurrencyDTO createEntity() {
        var currencyDTO = new CurrencyDTO();
        currencyDTO.name = DEFAULT_NAME;
        currencyDTO.code = DEFAULT_CODE;
        currencyDTO.numCode = DEFAULT_NUM_CODE;
        currencyDTO.preferred = DEFAULT_PREFERRED;
        return currencyDTO;
    }

    @BeforeEach
    public void initTest() {
        currencyDTO = createEntity();
    }

    @Test
    public void createCurrency() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Currency
        currencyDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(currencyDTO)
                .when()
                .post("/api/currencies")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .as(ENTITY_TYPE);

        // Validate the Currency in the database
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testCurrencyDTO = currencyDTOList.stream().filter(it -> currencyDTO.id.equals(it.id)).findFirst().get();
        assertThat(testCurrencyDTO.name).isEqualTo(DEFAULT_NAME);
        assertThat(testCurrencyDTO.code).isEqualTo(DEFAULT_CODE);
        assertThat(testCurrencyDTO.numCode).isEqualTo(DEFAULT_NUM_CODE);
        assertThat(testCurrencyDTO.preferred).isEqualTo(DEFAULT_PREFERRED);
    }

    @Test
    public void createCurrencyWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Currency with an existing ID
        currencyDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(currencyDTO)
            .when()
            .post("/api/currencies")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Currency in the database
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        currencyDTO.name = null;

        // Create the Currency, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(currencyDTO)
            .when()
            .post("/api/currencies")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Currency in the database
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkCodeIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        currencyDTO.code = null;

        // Create the Currency, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(currencyDTO)
            .when()
            .post("/api/currencies")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Currency in the database
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkNumCodeIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        currencyDTO.numCode = null;

        // Create the Currency, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(currencyDTO)
            .when()
            .post("/api/currencies")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Currency in the database
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkPreferredIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        currencyDTO.preferred = null;

        // Create the Currency, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(currencyDTO)
            .when()
            .post("/api/currencies")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Currency in the database
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateCurrency() {
        // Initialize the database
        currencyDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(currencyDTO)
                .when()
                .post("/api/currencies")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the currency
        var updatedCurrencyDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies/{id}", currencyDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(ENTITY_TYPE);

        // Update the currency
        updatedCurrencyDTO.name = UPDATED_NAME;
        updatedCurrencyDTO.code = UPDATED_CODE;
        updatedCurrencyDTO.numCode = UPDATED_NUM_CODE;
        updatedCurrencyDTO.preferred = UPDATED_PREFERRED;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedCurrencyDTO)
            .when()
            .put("/api/currencies/" + currencyDTO.id)
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Currency in the database
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeUpdate);
        var testCurrencyDTO = currencyDTOList.stream().filter(it -> updatedCurrencyDTO.id.equals(it.id)).findFirst().get();
        assertThat(testCurrencyDTO.name).isEqualTo(UPDATED_NAME);
        assertThat(testCurrencyDTO.code).isEqualTo(UPDATED_CODE);
        assertThat(testCurrencyDTO.numCode).isEqualTo(UPDATED_NUM_CODE);
        assertThat(testCurrencyDTO.preferred).isEqualTo(UPDATED_PREFERRED);
    }

    @Test
    public void updateNonExistingCurrency() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(currencyDTO)
            .when()
            .put("/api/currencies/" + Long.MAX_VALUE)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Currency in the database
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCurrency() {
        // Initialize the database
        currencyDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(currencyDTO)
                .when()
                .post("/api/currencies")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the currency
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/currencies/{id}", currencyDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var currencyDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(currencyDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllCurrencies() {
        // Initialize the database
        currencyDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(currencyDTO)
                .when()
                .post("/api/currencies")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .as(ENTITY_TYPE);

        // Get all the currencyList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(currencyDTO.id.intValue()))
            .body("name", hasItem(DEFAULT_NAME))
            .body("code", hasItem(DEFAULT_CODE))
            .body("numCode", hasItem(DEFAULT_NUM_CODE))
            .body("preferred", hasItem(DEFAULT_PREFERRED.booleanValue()));
    }

    @Test
    public void getCurrency() {
        // Initialize the database
        currencyDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(currencyDTO)
                .when()
                .post("/api/currencies")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .as(ENTITY_TYPE);

        var response = given() // Get the currency
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies/{id}", currencyDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(ENTITY_TYPE);

        // Get the currency
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies/{id}", currencyDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(currencyDTO.id.intValue()))
            .body("name", is(DEFAULT_NAME))
            .body("code", is(DEFAULT_CODE))
            .body("numCode", is(DEFAULT_NUM_CODE))
            .body("preferred", is(DEFAULT_PREFERRED.booleanValue()));
    }

    @Test
    public void getNonExistingCurrency() {
        // Get the currency
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/currencies/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
