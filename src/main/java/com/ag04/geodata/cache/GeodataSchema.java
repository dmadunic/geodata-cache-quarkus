package com.ag04.geodata.cache;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.types.java.CommonContainerTypes;
import org.infinispan.protostream.types.java.CommonTypes;
import com.ag04.geodata.cache.adapter.CountryDTOAdapter;

@AutoProtoSchemaBuilder(
    dependsOn = {
        CommonTypes.class,
        CommonContainerTypes.class
    },
    includeClasses = {
        CountryDTOAdapter.class
    },
    schemaPackageName = "geodata_list")
public interface GeodataSchema extends GeneratedSchema { 
}