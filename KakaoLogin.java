package com.chulchul.user.oauth;

import java.net.URI;
import java.util.Map;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class KakaoLogin extends OauthLogin{

    private final String client_id = "client_id";

    private final String redirect_url = "http://localhost:8080/oauth2/kakao/token";
    private final String authorization_url = "https://kauth.kakao.com/oauth/authorize?client_id="+client_id+"&redirect_uri="+redirect_url+"&response_type=code";
    private final String access_token_url = "https://kauth.kakao.com/oauth/token";
    private final String user_profile_url = "https://kapi.kakao.com/v2/user/me";

    @Override
    public String getAuthorizationUrl() {
        return this.authorization_url;
    }

    @Override
    public Map<String, Object> getAccessToken(Map<String, Object> authorize) {
        String code = authorize.get("code").toString();
        RestTemplate restTemplate = new RestTemplate();
        URI uri = URI.create(this.access_token_url);

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
        parameters.set("grant_type", "authorization_code");
        parameters.set("client_id", this.client_id);
        parameters.set("redirect_uri", this.redirect_url);
        parameters.set("code", code);

        HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(parameters);
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(uri, restRequest, String.class);
        JsonParser jp = new JacksonJsonParser();
        Map<String, Object> attribute = jp.parseMap(apiResponse.getBody());
        return attribute;
    }

    @Override
    public Map<String, Object> getUserProfile(Map<String, Object> attribute) {
        String accessToken = attribute.get("access_token").toString();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ accessToken);

        URI uri = URI.create(this.user_profile_url);
        
        HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(headers);
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(uri, restRequest, String.class);
        JsonParser jsonParser = new JacksonJsonParser();
        Map<String, Object> userProfile = jsonParser.parseMap(apiResponse.getBody());
        setProperty(userProfile);
        
        return userProfile;
    }

    @Override
    public void setProperty(Map<String, Object> userProfile) {
        Map<String, Object> property = (Map<String, Object>) userProfile.get("properties");
        this.providerId = userProfile.get("id").toString();
        this.nickname = property.get("nickname").toString();
    }
    
}
