package com.parking.services;

import java.util.List;

import com.parking.model.BikeParkingLocation;
import com.parking.model.Coordinates;


/**
 * Service Interface for requesting the Bike Locations.
 */
public interface BikeParkingLocationService {

    /**
     * Fetches the Closest Bike Parking Locations with the input Coordinates and limiting to the input count of Locations required.
     *
     * @param coordinates   - Starting point represented as Coordinates object.
     * @param noOfLocationsToReturn  - Count of locations required.
     * @return List of BikeParkingLocation objects.
     * @throws Exception - If there is any error calling the webservices to fetch the parking locations.
     */
	public List<BikeParkingLocation> fetchClosestBikeParkingLocations(Coordinates coordinates, int noOfLocationsToReturn) throws Exception;

}
