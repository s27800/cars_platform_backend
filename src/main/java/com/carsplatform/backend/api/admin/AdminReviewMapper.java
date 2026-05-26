package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminCarInfoResponse;
import com.carsplatform.backend.api.admin.dtos.AdminReviewResponse;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.UsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {UsernameMapper.class})
public interface AdminReviewMapper {
    AdminReviewMapper INSTANCE = Mappers.getMapper(AdminReviewMapper.class);

    @Mapping(target = "usernameResponse", source = "user")
    @Mapping(target = "likesCount", source = "likesCount")
    @Mapping(target = "carInfo", source = "car", qualifiedByName = "toCarInfo")
    AdminReviewResponse toDto(Review review);

    @Named("toCarInfo")
    default AdminCarInfoResponse toCarInfo(Car car) {
        if (car == null) {
            return null;
        }

        return AdminCarInfoResponse.builder()
                .carId(car.getId())
                .carName(car.getName())
                .brandName(car.getGeneration().getModel().getBrand().getName())
                .modelName(car.getGeneration().getModel().getName())
                .generationName(car.getGeneration().getName())
                .build();
    }

    @Named("toDtoList")
    default Page<AdminReviewResponse> toDtoList(Page<Review> reviews) {
        if (reviews == null)
            return null;

        return reviews.map(this::toDto);
    }
}
