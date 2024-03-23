package com.issuemoa.users.infrastructure.config;

import com.issuemoa.users.infrastructure.common.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class OAuth2AuthorizationRequestCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        log.info("==> [OAuth2AuthorizationRequestCookieRepository] loadAuthorizationRequest code :: {}", request.getParameter("code"));
        return CookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                    .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
                    .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("==> [OAuth2AuthorizationRequestCookieRepository] saveAuthorizationRequest authorizationRequest :: {}", authorizationRequest);
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }
        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS, false);
    }

    private void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        log.info("==> [OAuth2AuthorizationRequestCookieRepository] removeAuthorizationRequestCookies");
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        log.info("==> [OAuth2AuthorizationRequestCookieRepository] removeAuthorizationRequest");
        return this.loadAuthorizationRequest(request);
    }
}
