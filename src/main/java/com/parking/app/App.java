package com.parking.app;

import java.util.Collections;
import java.util.List;

import com.parking.model.BikeParkingLocation;
import com.parking.model.Coordinates;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import spark.Request;
import spark.Response;
import spark.Spark;

import com.parking.services.BikeParkingLocationService;
import com.parking.view.JsonTransformerRoute;
import spark.servlet.SparkApplication;

/**
 * Main MVC App Class that controls the navigation.     localhost/closestBikeLocations?lat=37.7833&long=-122.4167
 *
 */
public class App implements SparkApplication {
       private static final Logger logger = Logger.getLogger(App.class);

    public void init() {

        final ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml");
        /**
         * Dispatch Method for fetching the Closest Bike Parking Locations given the coordinates in the request.
         * In Success flow, This method either a JSON format location list to the clients
         * In Error flow, Logs the error and returns an empty List as JSON.
         */
        Spark.get(new JsonTransformerRoute("/closestBikeLocations") {
            @Override
            public Object handle(Request request, Response response) {

                Coordinates coordinates = extractCoordinatesFromRequest(request);

                if(coordinates == null) {
                    logger.warn("Request Rejected: Invalid Request received. Could not determine the starting location. Request Url:"+ request.url());
                    halt(400, "Invalid Request. Please submit coordinates in the correct format");
                }
                logger.info("Request received for Coordinates:" + coordinates);
                String locationCount = request.queryParams("limit");
                int noOfLocationsToReturn = 10;
                if(StringUtils.isNotEmpty(locationCount)) {
                    noOfLocationsToReturn = Integer.parseInt(locationCount);
                   if(noOfLocationsToReturn > 50) {
                       logger.warn("Request Rejected: More than allowed number of locations requested. Allowed:"+50+" Requested:"+ noOfLocationsToReturn);
                       halt(400, "Cannot give more than 50 Locations");
                   }
                }

                BikeParkingLocationService bikeParkingLocationService = ctx.getBean(BikeParkingLocationService.class);
                try {
                    List<BikeParkingLocation> closestParkingLocationList = bikeParkingLocationService.fetchClosestBikeParkingLocations(coordinates, noOfLocationsToReturn);

                    return closestParkingLocationList;
                }catch (Exception e) {
                    logger.error("Request Rejected: Exception Occurred:"+e.getMessage(), e);
                    return Collections.emptyList();
                }

            }

            /**
             * Extracts the latitude and longitude from the request and creates the Coordinates domain object.
             * @param request - Input Client Request.
             * @return Coordinates Object representing the input starting location.
             */
            private Coordinates extractCoordinatesFromRequest(Request request) {
                String latitude = request.queryParams("lat");
                String longitude = request.queryParams("long");
                if(StringUtils.isNotEmpty(latitude) && StringUtils.isNotEmpty(longitude)) {
                    try {
                        double latDouble = Double.parseDouble(latitude);
                        double longDouble = Double.parseDouble(longitude);
                        return new Coordinates(latDouble, longDouble);
                    }catch (NumberFormatException e) {
                        logger.error("Exception while extracting Coordinates "+ e.getMessage(), e);
                    }
                }
                return null;
            }
        });
    }
}
