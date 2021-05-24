package com.company;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;


public class RouteBerekening {
    private List<routeOrders> locaties;

    public RouteBerekening(List<routeOrders> locaties) {
        this.locaties = locaties;
    }

    public void GetLatLons() throws IOException, InterruptedException {
        for (routeOrders order : locaties) {
            StringBuilder inline = null;
            String urlBuild = buildUrl(order.straat);
            URL url = new URL(urlBuild);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsCode = conn.getResponseCode();
            System.out.println(responsCode);
            Scanner sc = new Scanner(url.openStream());
            while (sc.hasNext()) {
                String response = sc.nextLine();
                int latPos = response.indexOf("lat");
                int lonPos = response.indexOf("lon");
                String lat = response.substring(latPos + 6, latPos + 14);
                String lon = response.substring(lonPos + 6, lonPos + 13);
                System.out.println(lat);
                System.out.println(lon);
                order.setLat(lat);
                order.setLon(lon);
            }
            sc.close();
            // even wachten zodat de api ons niet blokeerd :(
            Thread.sleep(1000);
        }
    }

    public void findNeighrest(List<routeOrders> orders){
        for (int i = 0; i < orders.size() - 1; i++){
            //todo neighrest vinden op basis van de route api
            routeOrders deze = orders.get(i);
            routeOrders volgende = orders.get(i + 1);
            System.out.println(deze.straat + " zoekt " + volgende.straat);
            String x = buildRouteUrl(deze.getLat(), deze.getLon(), volgende.getLat(), volgende.getLon());
            System.out.println(x);
        }
    }

    private String buildRouteUrl(String lat1, String lon1, String lat2, String lon2){
        String baseUrl = "https://us1.locationiq.com/v1/directions/driving/";
        String apiKey = "pk.578fbdd9e700eed091ccb46bbaac6cb9";
        String coords = lon1 + "," + lat1 + ";" + lon2 + "," + lat2;
        String url = baseUrl + coords + "?key=" + apiKey + "&steps=false&alternatives=false";
        return url;
    }

    private String buildUrl(String adress) {
        String baseUrl = "https://us1.locationiq.com/v1/search.php?key=pk.578fbdd9e700eed091ccb46bbaac6cb9&q=";
        String encodedQuery = encodeValue(adress);
        System.out.println(baseUrl + encodedQuery + "&format=json");
        return baseUrl + encodedQuery + "&format=json";
    }

    // Method to encode a string value using `UTF-8` encoding scheme
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}
