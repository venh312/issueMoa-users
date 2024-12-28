package com.issuemoa.users.domain.settings;

import com.issuemoa.users.domain.BaseTime;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity(name = "user_settings")
public class Settings extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long userId;
    private String theme;
    private String voice;
    private String speed;
    private String language;

    public void updateTheme(String theme) {
        this.theme = theme;
    }
    public void updateVoice(String voice) {
        this.voice = voice;
    }
    public void updateSpeed(String speed) {
        this.speed = speed;
    }
    public void updateLanguage(String language) {
        this.language = language;
    }
}
