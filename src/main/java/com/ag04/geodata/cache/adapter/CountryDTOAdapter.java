package com.ag04.geodata.cache.adapter;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import com.ag04.geodata.service.dto.CountryDTO;

@ProtoAdapter(CountryDTO.class)
public class CountryDTOAdapter {

    @ProtoFactory
    CountryDTO create(Long id, String name, String code, String codeA2, String codeA3, String flag, Boolean active) {
        return new CountryDTO(id, name, code, codeA2, codeA3, flag, active);
    }

    @ProtoField(1)
    public Long getId(CountryDTO c) {
        return c.id;
        //return String.valueOf(c.id);
    }

    @ProtoField(2)
    String getName(CountryDTO c) {
        return c.name;
    }

    @ProtoField(3)
    String getCode(CountryDTO c) {
        return c.code;
    }

    @ProtoField(4)
    String getCodeA2(CountryDTO c) {
        return c.codeA2;
    }

    @ProtoField(5)
    String getCodeA3(CountryDTO c) {
        return c.codeA3;
    }

    @ProtoField(6)
    String getFlag(CountryDTO c) {
        return c.flag;
    }

    @ProtoField(7)
    Boolean getActive(CountryDTO c) {
        return c.active;
    }

}