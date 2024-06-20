package com.traveldiary.be.controller;

import com.traveldiary.be.dto.LocationDTO;
import com.traveldiary.be.entity.Locations;
import com.traveldiary.be.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "http://localhost:63342")  // 프론트엔드 서버 주소를 여기에 지정
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Locations> saveLocation(@RequestBody LocationDTO locationDTO) {
        logger.info("Received LocationDTO: {}", locationDTO);
        Locations location = locationService.saveLocation(locationDTO);
        return ResponseEntity.ok(location);
    }


    @DeleteMapping("/{locationId}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long locationId) {
        logger.info("Received request to delete location with id: {}", locationId);
        try {
            locationService.deleteLocation(locationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete location with id {}: {}", locationId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error while deleting location with id {}: {}", locationId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/date")
    public ResponseEntity<List<LocationDTO>> getLocationsByDate(@RequestParam("date") LocalDate date) {
        logger.info("Fetching locations for date: {}", date);
        List<LocationDTO> locations = locationService.getLocationsByDate(date);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/share/{userId}/{date}")
    public List<LocationDTO> getSharedLocations(@PathVariable Long userId, @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return locationService.getLocationsByUserAndDate(userId, localDate);
    }
}
