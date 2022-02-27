package com.x10.viriyah.services.impl;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface ExternalService {

    ResponseEntity<ExternalResponse> postRequest(String url, ExternalRequest request);

    <T> ResponseEntity<T> postRequest(String url, ExternalRequest request, Class<T> responseType);

    ResponseEntity<ExternalResponse> exchange(String url, HttpMethod method, HttpEntity<Object> entity);

}
