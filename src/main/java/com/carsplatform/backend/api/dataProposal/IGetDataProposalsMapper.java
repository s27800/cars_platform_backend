package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IGetDataProposalsMapper {
    IGetDataProposalsMapper INSTANCE = Mappers.getMapper(IGetDataProposalsMapper.class);

    @Mapping(target = "carName", source = "car.name")
    GetDataProposalsResponse toDto(DataProposal proposal);
}
