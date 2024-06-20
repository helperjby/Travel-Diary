package com.traveldiary.be.service;

import com.traveldiary.be.dto.LocationDTO;
import com.traveldiary.be.entity.Locations;
import com.traveldiary.be.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Locations saveLocation(LocationDTO locationDTO) {
        logger.info("Saving location: {}", locationDTO);

        Locations location = new Locations();
        location.setUserId(locationDTO.getUserId());
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        location.setAddress(locationDTO.getAddress());
        location.setRecordTime(locationDTO.getRecordTime());

        Locations savedLocation = locationRepository.save(location);
        logger.info("Location saved: {}", savedLocation);
        return savedLocation;
    }

    public List<LocationDTO> getLocationsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        logger.info("Fetching locations between {} and {}", startOfDay, endOfDay);

        List<Locations> locations = locationRepository.findAllByRecordTimeBetween(startOfDay, endOfDay);
        logger.info("Found locations: {}", locations);

        List<LocationDTO> locationDTOs = locations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Converted to DTOs: {}", locationDTOs);
        return locationDTOs;
    }

    public void deleteLocation(Long locationId) {
        logger.info("Attempting to delete location with id: {}", locationId);
        Optional<Locations> locationOptional = locationRepository.findById(locationId);
        if (locationOptional.isPresent()) {
            locationRepository.delete(locationOptional.get());
            logger.info("Deleted location with id: {}", locationId);
        } else {
            logger.error("Location not found for id: {}", locationId);
            throw new IllegalArgumentException("삭제할 위치를 찾을 수 없습니다. locationId=" + locationId);
        }
    }

    public List<LocationDTO> getLocationsByUserAndDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Locations> locations = locationRepository.findAllByUserIdAndRecordTimeBetween(userId, startOfDay, endOfDay);
        return locations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private LocationDTO convertToDto(Locations location) {
        LocationDTO dto = new LocationDTO();
        dto.setLocationId(location.getId());  // locationId 설정
        dto.setUserId(location.getUserId());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setAddress(location.getAddress());
        dto.setRecordTime(location.getRecordTime());
        return dto;
    }

}
