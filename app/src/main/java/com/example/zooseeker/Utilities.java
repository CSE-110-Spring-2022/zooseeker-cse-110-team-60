package com.example.zooseeker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utilities class. Contains static methods to display alerts and calculates
 * distance between two latitudes and longitudes using inverse Vincenty formula
 * and Haversine formula.
 */
public class Utilities {

    /**
     * Displays alert on activity passed in with chosen message and buttons.
     *
     * @param activity          Activity to display alert on.
     * @param message           Message to be displayed.
     * @param positiveButtonMsg Message to be set for positive button.
     * @param negativeButtonMsg Message to be set for negative button.
     */
    public static void showAlert(Activity activity, String message,
                                    String positiveButtonMsg, String negativeButtonMsg) {
        DialogInterface.OnClickListener dialog = (dialogInterface, i) -> {
            switch (i) {
                // "Yes" or "Ok" button clicked
                case DialogInterface.BUTTON_POSITIVE:
                    break;

                // "No" or "Cancel" button clicked
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder.setMessage(message).setPositiveButton(positiveButtonMsg, dialog)
                    .setNegativeButton(negativeButtonMsg, dialog).show();
    }

    /**
     * Calculates geodetic distance between two points specified by
     * latitude/longitude using Haversine inverse formula for perfect squares.
     *
     * Source: https://stackoverflow
     * .com/questions/120283/how-can-i-measure-distance-and-create-a-bounding
     * -box-based-on-two-latitudelongi
     *
     * @param lat1,lng1 first point in decimal degrees.
     * @param lat2,lng2 second point in decimal degrees.
     *
     * @return Distance between two points in kilometers.
     */
    public static double getHaversineDistance(double lat1, double lng1, double lat2,
                                              double lng2) {
        double earthRadius = 6371.0; // 3958.75 miles or 6371.0 kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a =
                Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    /**
     * Calculates geodetic distance between two points specified by
     * latitude/longitude using Vincenty inverse formula for ellipsoids.
     *
     * Source: http://www.java2s.com/example/java-utility-method/distance
     * -calculate/distancevincenty-final-double-lat1-final-double-lon1-final
     * -double-lat2-final-double-lon2-d1be5.html
     *
     * @param lat1,lng1 First point in decimal degrees.
     * @param lat2,lng2 Second point in decimal degrees.
     *
     * @return Distance between two points in meters.
     */
    public static double getVincentyDistance(double lat1, double lng1, double lat2,
                                             double lng2) {
        final double a = 6378137.0;
        final double b = 6356752.314245;
        final double f = 1 / 298.257223563;

        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));

        double L = Math.toRadians((lng2 - lng1));

        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double lambda = L, lambdaP = 2 * Math.PI;
        double cosSqAlpha = 0, sinSigma = 0, cos2SigmaM = 0, cosSigma = 0, sigma = 0;

        int iterLimit = 100;

        while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0) {

            double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);

            sinSigma =
                    Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));

            // co-incident points
            if (sinSigma == 0) {
                return 0;
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;

            // two points on equator
            if (cosSqAlpha == 0) {
                return Math.abs(a * L);
            }

            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));

            lambdaP = lambda;
            lambda =
                    L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        }

        // formula failed to converge
        if (iterLimit == 0) {
            return 0;
        }

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);

        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

        double deltaSigma =
                B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        double s = b * A * (sigma - deltaSigma);

        return s;
    }
}
