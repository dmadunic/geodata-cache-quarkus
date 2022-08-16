package com.ag04.geodata.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ag04.geodata.TestUtil;
import org.junit.jupiter.api.Test;

class CurrencyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CurrencyDTO.class);
        CurrencyDTO currencyDTO1 = new CurrencyDTO();
        currencyDTO1.id = 1L;
        CurrencyDTO currencyDTO2 = new CurrencyDTO();
        assertThat(currencyDTO1).isNotEqualTo(currencyDTO2);
        currencyDTO2.id = currencyDTO1.id;
        assertThat(currencyDTO1).isEqualTo(currencyDTO2);
        currencyDTO2.id = 2L;
        assertThat(currencyDTO1).isNotEqualTo(currencyDTO2);
        currencyDTO1.id = null;
        assertThat(currencyDTO1).isNotEqualTo(currencyDTO2);
    }
}
