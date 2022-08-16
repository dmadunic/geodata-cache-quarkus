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
 * A Currency.
 */
@Entity
@Table(name = "currency")
@Cacheable
@RegisterForReflection
public class Currency extends PanacheEntityBase implements Serializable {

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
    @Size(max = 3)
    @Column(name = "num_code", length = 3, nullable = false)
    public String numCode;

    @NotNull
    @Column(name = "preferred", nullable = false)
    public Boolean preferred;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Currency)) {
            return false;
        }
        return id != null && id.equals(((Currency) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "Currency{" +
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

    public Currency update() {
        return update(this);
    }

    public Currency persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Currency update(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("currency can't be null");
        }
        var entity = Currency.<Currency>findById(currency.id);
        if (entity != null) {
            entity.name = currency.name;
            entity.code = currency.code;
            entity.numCode = currency.numCode;
            entity.preferred = currency.preferred;
        }
        return entity;
    }

    public static Currency persistOrUpdate(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("currency can't be null");
        }
        if (currency.id == null) {
            persist(currency);
            return currency;
        } else {
            return update(currency);
        }
    }
}
