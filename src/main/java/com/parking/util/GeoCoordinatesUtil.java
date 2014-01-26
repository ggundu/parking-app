package com.parking.util;

import com.parking.model.Coordinates;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for Geo Coordanates and Lat/Lng operations.
 *
 * User: gautam
 * Date: 1/23/14
 * Time: 12:52 AM
 *
 */
public class GeoCoordinatesUtil {

    private static final int EARTH_RADIUS_MILES = 3963;

    private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
    private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
    private static final double MIN_LON = Math.toRadians(-180d); // -PI
    private static final double MAX_LON = Math.toRadians(180d);  //  PI

    /**
     * Gives the boundary coordinates for Input coordinates and radius (in miles)
     * @param coordinates - Center point Lat/Lng.
     * @param radiusInMiles - Radius to consider for boundaries.
     * @return Map containing the Min and Max coordinates with keys 'minCoordinates' and 'maxCoordinates' respectively
     */
    public static Map<String, Coordinates> getBoundaryCoordinates(Coordinates coordinates, double radiusInMiles) {
        Map<String, Coordinates> resultMap = new HashMap<>();
        double radLat = Math.toRadians(coordinates.getLatitude());
        double radLon = Math.toRadians(coordinates.getLongitude());

        // angular distance in radians on a great circle
        double radDist = radiusInMiles / EARTH_RADIUS_MILES;

        double minLat = radLat - radDist;
        double maxLat = radLat + radDist;

        double minLon, maxLon;
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            double deltaLon = Math.asin(Math.sin(radDist) /
                    Math.cos(radLat));
            minLon = radLon - deltaLon;
            if (minLon < MIN_LON) minLon += 2d * Math.PI;
            maxLon = radLon + deltaLon;
            if (maxLon > MAX_LON) maxLon -= 2d * Math.PI;
        } else {
            // a pole is within the distance
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            minLon = MIN_LON;
            maxLon = MAX_LON;
        }
        Coordinates minCoordinates = new Coordinates(Math.toDegrees(minLat), Math.toDegrees(minLon));
        resultMap.put("minCoordinates", minCoordinates);
        Coordinates maxCoordinates = new Coordinates(Math.toDegrees(maxLat), Math.toDegrees(maxLon));
        resultMap.put("maxCoordinates", maxCoordinates);

        return resultMap;
    }

    /**
     * Gives the Distance between two Coordinates objects.
     *
     * @param fromCoordinates - From Coordinates .
     * @param toCoordinates  - To Coordinates.
     * @return  Distance between the two points.
     */
    public static double getDistanceBetweenTwoPoints(Coordinates fromCoordinates, Coordinates toCoordinates) {
        double fromLat = fromCoordinates.getLatitude();
        double fromLong = fromCoordinates.getLongitude();
        double toLat = toCoordinates.getLatitude();
        double toLong = toCoordinates.getLongitude();
        //convert degrees to radians
        fromLat = fromLat * Math.PI / 180;
        fromLong = fromLong * Math.PI / 180;
        toLat = toLat * Math.PI / 180;
        toLong = toLong * Math.PI / 180;

        double dist = 0;

        if(fromLat != toLat || fromLong != toLong) {
            //the two points are not the same
            dist = Math.sin(fromLat) * Math.sin(toLat) + Math.cos(fromLat) * Math.cos(toLat) * Math.cos(toLong - fromLong);

            dist = EARTH_RADIUS_MILES * (-1 * Math.atan(dist / Math.sqrt(1 - dist * dist)) + Math.PI / 2);
        }
        return dist;
    }

}
