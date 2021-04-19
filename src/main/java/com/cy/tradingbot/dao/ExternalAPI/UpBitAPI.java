package com.cy.tradingbot.dao.ExternalAPI;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cy.tradingbot.domain.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Repository
public class UpBitAPI {
    private final RestTemplate restTemplate;
    private String secretKey;
    private String accessKey;

    public UpBitAPI() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        restTemplate = new RestTemplate(factory);
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String mapToQueryString(MultiValueMap<String, String> map) {
        if (map == null || map.isEmpty()) return null;

        StringBuilder stringBuilder = new StringBuilder();

        map.forEach((k, v) -> v.forEach(s -> {
                    stringBuilder.append(k);
                    if (v.size() > 1)
                        stringBuilder.append("[]");
                    stringBuilder.append("=");
                    stringBuilder.append(s);
                    stringBuilder.append("&");
                })
        );

        if (!map.isEmpty())
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    private String getSecretKey(MultiValueMap<String, String> parameters) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String queryHash = "";
        String query = mapToQueryString(parameters);

        if (query != null && !query.isEmpty()) {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(query.getBytes("utf8"));

            queryHash = String.format("%0128x", new BigInteger(1, md.digest()));
        }

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        return "Bearer " + jwtToken;
    }

    private final String uri = "https://api.upbit.com/v1";

    public Optional<List<Candle>> getDayCandle(String coinName, int count) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/candles/days");
        uriComponentsBuilder.queryParam("market", coinName);
        uriComponentsBuilder.queryParam("count", count);

        ResponseEntity<List<Candle>> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Candle>>() {
        });

        if (responseEntity.getStatusCode() != HttpStatus.OK)
            return Optional.empty();

        return Optional.ofNullable(responseEntity.getBody());
    }

    public Optional<List<Candle>> getTicker(String coinName) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/ticker");
        uriComponentsBuilder.queryParam("markets", coinName);

        ResponseEntity<List<Candle>> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Candle>>() {
        });

        if (responseEntity.getStatusCode() != HttpStatus.OK)
            return Optional.empty();

        return Optional.ofNullable(responseEntity.getBody());
    }

    public Optional<List<Wallet>> getAccounts() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.set("Authorization", getSecretKey(null));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/accounts");

        ResponseEntity<List<Wallet>> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Wallet>>() {
        });

        if (responseEntity.getStatusCode() != HttpStatus.OK)
            return Optional.empty();

        return Optional.ofNullable(responseEntity.getBody());
    }

    public Optional<Order> order(String coinName, String side, Double volume, Double price, String orderType) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("market", coinName);
        parameters.add("side", side);
        if (volume != null)
            parameters.add("volume", String.valueOf(volume));
        if (price != null)
            parameters.add("price", String.valueOf(price));
        parameters.add("ord_type", orderType);

        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.set("Authorization", getSecretKey(parameters));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpEntity<?> entity = new HttpEntity<>(parameters, httpHeaders);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/orders");

        ResponseEntity<Order> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.POST, entity, Order.class);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (responseEntity.getStatusCode() != HttpStatus.CREATED)
            return Optional.empty();

        return Optional.ofNullable(responseEntity.getBody());
    }

    public Optional<Order> getOrder(String uuid) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("uuid", uuid);

        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.set("Authorization", getSecretKey(parameters));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/order");
        uriComponentsBuilder.queryParams(parameters);

        ResponseEntity<Order> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, Order.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK)
            return Optional.empty();

        return Optional.ofNullable(responseEntity.getBody());
    }

    public void deleteOrder(String uuid) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("uuid", uuid);

        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.set("Authorization", getSecretKey(parameters));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/order");
        uriComponentsBuilder.queryParams(parameters);

        ResponseEntity<Order> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.DELETE, entity, Order.class);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public Optional<List<OrderBook>> getOrderBook(List<String> coinNameList) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
//
//        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/orderbook");
//
//
//        uriComponentsBuilder.queryParam("markets", coinNameList);
//
//        ResponseEntity<List<OrderBook>> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<OrderBook>>() {
//        });
//
//        if (responseEntity.getStatusCode() != HttpStatus.OK)
//            return Optional.empty();
//
//        return Optional.ofNullable(responseEntity.getBody());
//    }

    public Optional<List<Candle>> getMinuteCandle(String coinName, int minute, int count) {
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/candles/minutes/" + minute);
        uriComponentsBuilder.queryParam("market", coinName);
        uriComponentsBuilder.queryParam("count", count);

        ResponseEntity<List<Candle>> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Candle>>() {
        });

        if (responseEntity.getStatusCode() != HttpStatus.OK)
            return Optional.empty();

        return Optional.ofNullable(responseEntity.getBody());
    }

    public Optional<List<Coin>> getAllCoin() {
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri + "/market/all");

        ResponseEntity<List<Coin>> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Coin>>() {
        });

        if (responseEntity.getStatusCode() != HttpStatus.OK)
            return Optional.empty();

        return Optional.ofNullable(responseEntity.getBody());
    }
}
