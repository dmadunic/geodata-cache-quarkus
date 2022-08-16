package com.ag04.geodata.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ag04.geodata.TestUtil;
import org.junit.jupiter.api.Test;

public class CurrencyTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Currency.class);
        Currency currency1 = new Currency();
        currency1.id = 1L;
        Currency currency2 = new Currency();
        currency2.id = currency1.id;
        assertThat(currency1).isEqualTo(currency2);
        currency2.id = 2L;
        assertThat(currency1).isNotEqualTo(currency2);
        currency1.id = null;
        assertThat(currency1).isNotEqualTo(currency2);
    }
}
