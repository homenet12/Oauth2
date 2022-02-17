package com.chulchul.user.oauth;

import java.util.Map;

public abstract class OauthLogin {
    
    protected abstract String getAuthorizationUrl();
    protected abstract Map<String, Object> getAccessToken(Map<String, Object> authorization);
    protected abstract Map<String, Object> getUserProfile(Map<String, Object> attribute);
    
    protected String providerId;
    protected String nickname;

    protected abstract void setProperty(Map<String, Object> userProfile);

    public static OauthLogin oauthFactory(String provider){
        if(provider.equals("kakao")) return new KakaoLogin();
        else if (provider.equals("naver")) return new NaverLogin();
        else {
            throw new IllegalArgumentException("이외에 플랫폼 로그인은 지원하지 않습니다.");
        }
    }

    public String getProviderId() {
        return this.providerId;
    }

    public String getNickname() {
        return this.nickname;
    }
}
