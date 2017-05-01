package com.noqapp.client.utils;

/**
 * Created by chandra on 5/1/17.
 */

public class Formatter {

    public static String getFormattedAddress(String address) {
        if (address.contains(",")) {
            String[] arr = address.split(",");
            if (arr.length > 2) {

                int secondIndex = address.indexOf(',', address.indexOf(',') + 1);
                return address.substring(0, secondIndex) + "\n" + address.substring(secondIndex, address.length() - 1);
            } else
                return address;
        } else {
            return address;
        }

    }
}
