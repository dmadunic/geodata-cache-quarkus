package com.ag04.geodata.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ag04.geodata.TestUtil;
import org.junit.jupiter.api.Test;

class CountryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CountryDTO.class);
        CountryDTO countryDTO1 = new CountryDTO();
        countryDTO1.id = 1L;
        CountryDTO countryDTO2 = new CountryDTO();
        assertThat(countryDTO1).isNotEqualTo(countryDTO2);
        countryDTO2.id = countryDTO1.id;
        assertThat(countryDTO1).isEqualTo(countryDTO2);
        countryDTO2.id = 2L;
        assertThat(countryDTO1).isNotEqualTo(countryDTO2);
        countryDTO1.id = null;
        assertThat(countryDTO1).isNotEqualTo(countryDTO2);
    }
}
