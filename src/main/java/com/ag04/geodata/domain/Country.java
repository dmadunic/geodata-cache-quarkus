package com.ag04.geodata.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Country.
 */
@Entity
@Table(name = "country")
@Cacheable
@RegisterForReflection
public class Country extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    public String name;

    @NotNull
    @Size(max = 20)
    @Column(name = "code", length = 20, nullable = false)
    public String code;

    @NotNull
    @Size(max = 2)
    @Column(name = "code_a_2", length = 2, nullable = false)
    public String codeA2;

    @NotNull
    @Size(max = 3)
    @Column(name = "code_a_3", length = 3, nullable = false)
    public String codeA3;

    @Size(max = 255)
    @Column(name = "flag", length = 255)
    public String flag;

    @NotNull
    @Column(name = "active", nullable = false)
    public Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        return id != null && id.equals(((Country) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "Country{" +
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

    public Country update() {
        return update(this);
    }

    public Country persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Country update(Country country) {
        if (country == null) {
            throw new IllegalArgumentException("country can't be null");
        }
        var entity = Country.<Country>findById(country.id);
        if (entity != null) {
            entity.name = country.name;
            entity.code = country.code;
            entity.codeA2 = country.codeA2;
            entity.codeA3 = country.codeA3;
            entity.flag = country.flag;
            entity.active = country.active;
        }
        return entity;
    }

    public static Country persistOrUpdate(Country country) {
        if (country == null) {
            throw new IllegalArgumentException("country can't be null");
        }
        if (country.id == null) {
            persist(country);
            return country;
        } else {
            return update(country);
        }
    }
}
