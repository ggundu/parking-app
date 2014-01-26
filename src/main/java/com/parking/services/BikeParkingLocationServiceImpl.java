/**
 * 
 */
package com.parking.services;

import com.parking.model.BikeParkingLocation;
import com.parking.model.Coordinates;
import com.parking.model.DistanceComparator;
import com.parking.util.GeoCoordinatesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation for BikeParkingLocationService.
 * 
 * @author gautam
 * 
 */
@Service
public class BikeParkingLocationServiceImpl implements
		BikeParkingLocationService {

    private static final Logger logger = Logger.getLogger(BikeParkingLocationServiceImpl.class);

    @Autowired
    private SocrataApiService socrataApiService;



    /**
     * @see com.parking.services.BikeParkingLocationService#fetchClosestBikeParkingLocations
     * (com.parking.model.Coordinates, int)
     */
	public List<BikeParkingLocation> fetchClosestBikeParkingLocations(
            Coordinates coordinates, int noOfLocationsToReturn) throws Exception {

        List<BikeParkingLocation> locationList = getBikeParkingLocationsWithinRadius(coordinates, 1);
        if(locationList == null || locationList.isEmpty()) {
            locationList = getBikeParkingLocationsWithinRadius(coordinates, 2);
        }
        if(locationList != null && !locationList.isEmpty()) {
            locationList = processDistanceFromOrigin(coordinates, locationList, noOfLocationsToReturn);
        }

        return locationList;

	}

    /**
     * Retrieves the Bike Parking Locations given the starting coordinates and the radius in Miles.
     *
     * @param coordinates - Starting Coordinates.
     * @param radiusInMiles - required radius to search for.
     * @return  List of BikeParkingLocation's
     * @throws Exception - Thrown for any issue invoking Socrata API calls.
     */
    private List<BikeParkingLocation> getBikeParkingLocationsWithinRadius(Coordinates coordinates, double radiusInMiles) throws Exception {
        Map<String, Coordinates> boundaryCoordinates = GeoCoordinatesUtil.getBoundaryCoordinates(coordinates, radiusInMiles);


        Coordinates minBoundaryCoordinates = boundaryCoordinates.get("minCoordinates");
        Coordinates maxBoundaryCoordinates = boundaryCoordinates.get("maxCoordinates");
        List<BikeParkingLocation> locationList = null;

        try {
            locationList =  socrataApiService.fetchBikeParkingLocationsWithinCoordinates(minBoundaryCoordinates, maxBoundaryCoordinates);
        }catch (Exception e) {
            logger.error("Socrata Exception: Exception occurred while making call to Socrata. Exception:"+e.getMessage(), e);
            throw e;
        }
        return locationList;
    }

    /**
     * Determines the locations to be eligible to return by figuring out the distance between the starting point and respective locations and
     * sorting them by the distance and removes duplicate locations.
     *
     * @param origin - Starting coordinates.
     * @param locationList  - List of BikeParkingLocations retrieved from API.
     * @param noOfLocationsToReturn - Count of number of locations to return.
     * @return List of final BikeParkingLocations to be returned to client;
     */
    private List<BikeParkingLocation> processDistanceFromOrigin(Coordinates origin, List<BikeParkingLocation> locationList, int noOfLocationsToReturn) {
        List<BikeParkingLocation> postProcessList = new ArrayList<>(locationList.size());
        Map<String, String> mapForUniqueLocs = new HashMap<>();

        for(BikeParkingLocation eachLocation : locationList) {
            boolean addLocation = false;
            String currentLocSortKey = eachLocation.getNoOfRacksInstalled()+""+eachLocation.getNoOfParkingSpaces();
           if(mapForUniqueLocs.get(eachLocation.getName()) == null) {
              addLocation = true;
              mapForUniqueLocs.put(eachLocation.getName(), currentLocSortKey);
           }else {
              String existingSortingKey = mapForUniqueLocs.get(eachLocation.getName());
              if(currentLocSortKey.compareTo(existingSortingKey)>0) {
                  mapForUniqueLocs.put(eachLocation.getName(), existingSortingKey);
                  addLocation = true;
              }
           }
            if(addLocation) {
                eachLocation.setDistanceFromOrigin(GeoCoordinatesUtil.getDistanceBetweenTwoPoints(origin, eachLocation.getCoordinates()));
                postProcessList.add(eachLocation);
            }

        }
        Collections.sort(postProcessList, new DistanceComparator());
        if(noOfLocationsToReturn < postProcessList.size()) {
            postProcessList = postProcessList.subList(0, noOfLocationsToReturn);
        }

        return postProcessList;
    }

}
