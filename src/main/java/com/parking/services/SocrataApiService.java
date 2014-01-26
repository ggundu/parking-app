package com.parking.services;

import com.parking.model.BikeParkingLocation;
import com.parking.model.Coordinates;

import java.util.List;

/**
 * Service Interface for Socrata API calls.
 * User: gautam
 * Date: 1/22/14
 * Time: 11:50 PM
 *
 */
public interface SocrataApiService {
    /**
     * Service to fetch the Bike Parking Locations within the given Coordinates.
     *
     * @param minCoordinates - Max Lat/Lng Boundary.
     * @param maxCoordinates - Min Lat/Lng Boundary.
     * @return List of BikeParkingLocations within the boundaries.
     */
    List<BikeParkingLocation> fetchBikeParkingLocationsWithinCoordinates(Coordinates minCoordinates, Coordinates maxCoordinates) throws Exception;
}
