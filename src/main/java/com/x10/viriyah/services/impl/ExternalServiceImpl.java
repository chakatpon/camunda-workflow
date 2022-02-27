package com.x10.viriyah.services.impl;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalServiceImpl implements ExternalService {

    @Autowired
    private RestTemplate rest;

    @Override
    public ResponseEntity<ExternalResponse> postRequest(String url, ExternalRequest request) {
        return rest.postForEntity(url, request, ExternalResponse.class);
    }

    @Override
    public <T> ResponseEntity<T> postRequest(String url, ExternalRequest request, Class<T> responseType) {
        return rest.postForEntity(url, request, responseType);
    }

    public ResponseEntity<ExternalResponse> exchange(String url, HttpMethod method, HttpEntity<Object> entity) {
        return rest.exchange(url, method, entity, ExternalResponse.class);
    }

}
