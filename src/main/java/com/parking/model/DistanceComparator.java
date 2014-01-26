package com.parking.model;

import java.util.Comparator;

/**
 * Comparator used for Sorting the locations by the order of the Distance from the starting point to respective location.
 * User: gautam
 * Date: 1/23/14
 * Time: 2:36 AM
 *
 */
public class DistanceComparator implements Comparator<BikeParkingLocation> {

    @Override
    public int compare(BikeParkingLocation o1, BikeParkingLocation o2) {
        double distanceDiff = o1.getDistanceFromOrigin()-o2.getDistanceFromOrigin();
        if(distanceDiff > 0) {
            return 1;
        } else if(distanceDiff < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
