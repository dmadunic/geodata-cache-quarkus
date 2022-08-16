package com.ag04.geodata.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ag04.geodata.TestUtil;
import com.ag04.geodata.service.dto.CountryDTO;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import javax.inject.Inject;
import liquibase.Liquibase;
import org.junit.jupiter.api.*;

@QuarkusTest
public class CountryResourceTest {

    private static final TypeRef<CountryDTO> ENTITY_TYPE = new TypeRef<>() {};

    private static final TypeRef<List<CountryDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {};

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CODE_A_2 = "AA";
    private static final String UPDATED_CODE_A_2 = "BB";

    private static final String DEFAULT_CODE_A_3 = "AAA";
    private static final String UPDATED_CODE_A_3 = "BBB";

    private static final String DEFAULT_FLAG = "AAAAAAAAAA";
    private static final String UPDATED_FLAG = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    String adminToken;

    CountryDTO countryDTO;

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
    public static CountryDTO createEntity() {
        var countryDTO = new CountryDTO();
        countryDTO.name = DEFAULT_NAME;
        countryDTO.code = DEFAULT_CODE;
        countryDTO.codeA2 = DEFAULT_CODE_A_2;
        countryDTO.codeA3 = DEFAULT_CODE_A_3;
        countryDTO.flag = DEFAULT_FLAG;
        countryDTO.active = DEFAULT_ACTIVE;
        return countryDTO;
    }

    @BeforeEach
    public void initTest() {
        countryDTO = createEntity();
    }

    @Test
    public void createCountry() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Country
        countryDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(countryDTO)
                .when()
                .post("/api/countries")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .as(ENTITY_TYPE);

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testCountryDTO = countryDTOList.stream().filter(it -> countryDTO.id.equals(it.id)).findFirst().get();
        assertThat(testCountryDTO.name).isEqualTo(DEFAULT_NAME);
        assertThat(testCountryDTO.code).isEqualTo(DEFAULT_CODE);
        assertThat(testCountryDTO.codeA2).isEqualTo(DEFAULT_CODE_A_2);
        assertThat(testCountryDTO.codeA3).isEqualTo(DEFAULT_CODE_A_3);
        assertThat(testCountryDTO.flag).isEqualTo(DEFAULT_FLAG);
        assertThat(testCountryDTO.active).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    public void createCountryWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Country with an existing ID
        countryDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(countryDTO)
            .when()
            .post("/api/countries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        countryDTO.name = null;

        // Create the Country, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(countryDTO)
            .when()
            .post("/api/countries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkCodeIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        countryDTO.code = null;

        // Create the Country, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(countryDTO)
            .when()
            .post("/api/countries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkCodeA2IsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        countryDTO.codeA2 = null;

        // Create the Country, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(countryDTO)
            .when()
            .post("/api/countries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkCodeA3IsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        countryDTO.codeA3 = null;

        // Create the Country, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(countryDTO)
            .when()
            .post("/api/countries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkActiveIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        countryDTO.active = null;

        // Create the Country, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(countryDTO)
            .when()
            .post("/api/countries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateCountry() {
        // Initialize the database
        countryDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(countryDTO)
                .when()
                .post("/api/countries")
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
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the country
        var updatedCountryDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries/{id}", countryDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(ENTITY_TYPE);

        // Update the country
        updatedCountryDTO.name = UPDATED_NAME;
        updatedCountryDTO.code = UPDATED_CODE;
        updatedCountryDTO.codeA2 = UPDATED_CODE_A_2;
        updatedCountryDTO.codeA3 = UPDATED_CODE_A_3;
        updatedCountryDTO.flag = UPDATED_FLAG;
        updatedCountryDTO.active = UPDATED_ACTIVE;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedCountryDTO)
            .when()
            .put("/api/countries/" + countryDTO.id)
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeUpdate);
        var testCountryDTO = countryDTOList.stream().filter(it -> updatedCountryDTO.id.equals(it.id)).findFirst().get();
        assertThat(testCountryDTO.name).isEqualTo(UPDATED_NAME);
        assertThat(testCountryDTO.code).isEqualTo(UPDATED_CODE);
        assertThat(testCountryDTO.codeA2).isEqualTo(UPDATED_CODE_A_2);
        assertThat(testCountryDTO.codeA3).isEqualTo(UPDATED_CODE_A_3);
        assertThat(testCountryDTO.flag).isEqualTo(UPDATED_FLAG);
        assertThat(testCountryDTO.active).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    public void updateNonExistingCountry() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
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
            .body(countryDTO)
            .when()
            .put("/api/countries/" + Long.MAX_VALUE)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Country in the database
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCountry() {
        // Initialize the database
        countryDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(countryDTO)
                .when()
                .post("/api/countries")
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
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the country
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/countries/{id}", countryDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var countryDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(countryDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllCountries() {
        // Initialize the database
        countryDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(countryDTO)
                .when()
                .post("/api/countries")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .as(ENTITY_TYPE);

        // Get all the countryList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(countryDTO.id.intValue()))
            .body("name", hasItem(DEFAULT_NAME))
            .body("code", hasItem(DEFAULT_CODE))
            .body("codeA2", hasItem(DEFAULT_CODE_A_2))
            .body("codeA3", hasItem(DEFAULT_CODE_A_3))
            .body("flag", hasItem(DEFAULT_FLAG))
            .body("active", hasItem(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    public void getCountry() {
        // Initialize the database
        countryDTO =
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(countryDTO)
                .when()
                .post("/api/countries")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .as(ENTITY_TYPE);

        var response = given() // Get the country
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries/{id}", countryDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(ENTITY_TYPE);

        // Get the country
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries/{id}", countryDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(countryDTO.id.intValue()))
            .body("name", is(DEFAULT_NAME))
            .body("code", is(DEFAULT_CODE))
            .body("codeA2", is(DEFAULT_CODE_A_2))
            .body("codeA3", is(DEFAULT_CODE_A_3))
            .body("flag", is(DEFAULT_FLAG))
            .body("active", is(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    public void getNonExistingCountry() {
        // Get the country
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/countries/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
