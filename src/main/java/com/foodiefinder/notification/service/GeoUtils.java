package com.foodiefinder.notification.service;

public class GeoUtils {

    final static double EARTH_RADIUS_KM = 6371; // 지구 반경 (km)

    /**
     * lat,lon을 중심으로하는 한 변이 distance*2 길이인 정사각형의 왼쪽아래 꼭짓점
     */
    public static Coordinates calculateBottomLeftCoordinate(double lat, double lon, double distance) {
        final double metersPerDegree = (2 * Math.PI * EARTH_RADIUS_KM * 1000) / 360; // 1도당 미터 수

        double latChange = (distance * 2) / metersPerDegree;
        double lonChange = (distance * 2) / (metersPerDegree * Math.cos(Math.toRadians(lat)));

        return new Coordinates(lat - latChange, lon - lonChange);
    }

    /**
     * lat,lon을 중심으로하는 한 변이 distance*2 길이인 정사각형의 오른쪽 위 꼭짓점
     */
    public static Coordinates calculateTopRightCoordinate(double lat, double lon, double sideLengthInMeters) {
        final double metersPerDegree = (2 * Math.PI * EARTH_RADIUS_KM * 1000) / 360; // 1도당 미터 수

        double latChange = sideLengthInMeters / metersPerDegree;
        double lonChange = sideLengthInMeters / (metersPerDegree * Math.cos(Math.toRadians(lat)));

        return new Coordinates(lat + latChange, lon + lonChange);
    }

    /**
     * 두 점 사이의 거리 (meter) <br>
     * FIXME: 중복코드, calculateDistance의 경우 맛집 API 쪽에서도 동일한 기능을 개발하셨지만 아직 해당 기능이 develop에 포함되어있지 않아서 임시로 구현했습니다.
     */
    public static int calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (EARTH_RADIUS_KM * c * 1000); //meter
    }


    public static class Coordinates {
        public double latitude;
        public double longitude;

        public Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
