package com.chulchul.user.oauth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.chulchul.user.User;
import com.chulchul.user.UserService;
import com.chulchul.user.dto.UserDto;
import com.chulchul.user.oauthRefac.OAuthProvider;
import com.chulchul.user.oauthRefac.OAuthProviderFactory;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final UserService userService;
    private final OAuthProviderFactory oauthFactory;

    @GetMapping("/{provider}")
    public String oauth2Login(@PathVariable String provider, HttpServletRequest request){
        
        OauthLogin oauthLogin = OauthLogin.oauthFactory(provider);
        return "redirect:" + oauthLogin.getAuthorizationUrl();
        
    }

    @GetMapping("/{provider}/token")
    public String redirectCode( @PathVariable String provider, 
                                @RequestParam Map<String, Object> authorization, 
                                HttpServletRequest request) throws JsonProcessingException, ParseException{ 
        oauthFactory.getOAuthProvider(provider).requestSocialLoginUser(authorization.get("code").toString());
        OauthLogin oauthLogin = OauthLogin.oauthFactory(provider);
        oauthLogin.getUserProfile(oauthLogin.getAccessToken(authorization));
        User user = userService.findByOauth(oauthLogin.providerId, provider);
        if(user != null){
            request.getSession().setAttribute("user", new UserDto(user));
            return "redirect:/";
        }

        request.getSession().setAttribute("user", userService.saveUser(oauthLogin.nickname, provider, oauthLogin.providerId));
        return "redirect:/";
    }
}
