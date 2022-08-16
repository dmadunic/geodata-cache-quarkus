package com.ag04.geodata.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.ag04.geodata.domain.Currency} entity.
 */
@RegisterForReflection
public class CurrencyDTO implements Serializable {

    public Long id;

    @NotNull
    public String name;

    @NotNull
    @Size(max = 20)
    public String code;

    @NotNull
    @Size(max = 3)
    public String numCode;

    @NotNull
    public Boolean preferred;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CurrencyDTO)) {
            return false;
        }

        return id != null && id.equals(((CurrencyDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "CurrencyDTO{" +
            "id=" +
            id +
            ", name='" +
            name +
            "'" +
            ", code='" +
            code +
            "'" +
            ", numCode='" +
            numCode +
            "'" +
            ", preferred='" +
            preferred +
            "'" +
            "}"
        );
    }
}
