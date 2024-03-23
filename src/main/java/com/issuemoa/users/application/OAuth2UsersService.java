package com.issuemoa.users.application;

import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2UsersService extends DefaultOAuth2UserService {
    private final UsersRepository usersRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 현재 로그인 진행 중인 서비스를 구분하는 코드
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("==> [Oauth2UsersService] loadUser >> registrationId :: {}, getAttributes :: {}", registrationId, oAuth2User.getAttributes());

        if ("google".equals(registrationId)) {
            saveOrUpdateGoogle(oAuth2User);
        } else if ("kakao".equals(registrationId)) {
            saveOrUpdateKaKao(oAuth2User);
        }

        return oAuth2User;
    }

    // 등록된 사용자가 있으면 업데이트, 없으면 유저 생성
    private void saveOrUpdateGoogle(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String sub = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        Users users = usersRepository.findByEmail(email)
                                .map(entity -> entity.updateName(name))
                                .orElse(Users.builder()
                                    .uid(sub)
                                    .email(email)
                                    .name(name)
                                    .gradeCode("I")
                                    .snsType("google")
                                    .build());
        usersRepository.save(users);
    }

    private void saveOrUpdateKaKao(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String id = String.valueOf(attributes.get("id"));
        HashMap<String, Object> kakao_account = (HashMap<String, Object>) attributes.get("kakao_account");
        HashMap<String, String> profile = (HashMap<String, String>) kakao_account.get("profile");
        String name = profile.get("nickname");
        String email = (String) kakao_account.get("email");
        Users users = usersRepository.findByEmail(email)
                                .map(entity -> entity.updateName(name))
                                .orElse(Users.builder()
                                    .uid(id)
                                    .email(email)
                                    .name(name)
                                    .gradeCode("I")
                                    .snsType("kakao")
                                    .build());
        usersRepository.save(users);
    }
}
