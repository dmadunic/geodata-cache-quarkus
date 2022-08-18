package com.ag04.geodata.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.undertow.server.handlers.SetAttributeHandler.ClearBuilder;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.ag04.geodata.domain.Country} entity.
 */
@RegisterForReflection
public class CountryDTO implements Serializable {

    public Long id;

    @NotNull
    public String name;

    @NotNull
    @Size(max = 20)
    public String code;

    @NotNull
    @Size(max = 2)
    public String codeA2;

    @NotNull
    @Size(max = 3)
    public String codeA3;

    @Size(max = 255)
    public String flag;

    @NotNull
    public Boolean active;

    public CountryDTO() {
        // default constructor
    }

    public CountryDTO(Long id, @NotNull String name, @NotNull @Size(max = 20) String code,
            @NotNull @Size(max = 2) String codeA2, @NotNull @Size(max = 3) String codeA3, @Size(max = 255) String flag,
            @NotNull Boolean active) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.codeA2 = codeA2;
        this.codeA3 = codeA3;
        this.flag = flag;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CountryDTO)) {
            return false;
        }

        return id != null && id.equals(((CountryDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "CountryDTO{" +
            "id=" +
            id +
            ", name='" +
            name +
            "'" +
            ", code='" +
            code +
            "'" +
            ", codeA2='" +
            codeA2 +
            "'" +
            ", codeA3='" +
            codeA3 +
            "'" +
            ", flag='" +
            flag +
            "'" +
            ", active='" +
            active +
            "'" +
            "}"
        );
    }
}
