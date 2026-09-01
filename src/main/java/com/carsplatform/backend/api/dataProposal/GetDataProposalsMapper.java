package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.cars.CarInfoMapper;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {CarInfoMapper.class})
public interface GetDataProposalsMapper {

    @Mapping(target = "carInfo", source = "car", qualifiedByName = "toCarInfo")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    GetDataProposalsResponse toDto(DataProposal proposal);
}
