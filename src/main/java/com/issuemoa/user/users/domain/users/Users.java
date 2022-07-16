package com.issuemoa.user.users.domain.users;

import com.issuemoa.user.users.common.DateUtil;
import com.issuemoa.user.users.domain.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity(name = "users")
public class Users extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String email;
    private String password;
    private int loginFailCnt;
    private String addr;
    private String addrPostNo;
    private String tempYn;
    private String dropYn;
    private LocalDateTime lastLoginTime;

    @Builder
    public Users(Long id, String email, String password, int loginFailCnt, String addr, String addrPostNo, String tempYn, String dropYn, LocalDateTime lastLoginTime) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.loginFailCnt = loginFailCnt;
        this.addr = addr;
        this.addrPostNo = addrPostNo;
        this.tempYn = tempYn;
        this.dropYn = dropYn;
        this.lastLoginTime = lastLoginTime;
    }

    @Getter
    public static class Response {
        private Long id;
        private String email;
        private String addr;
        private String addrPostNo;
        private String tempYn;
        private String dropYn;
        private String registerTime;
        private String modifyTime;
        private String lastLoginTime;

        public Response(Object o) {
            Users users = (Users) o;
            this.id = users.id;
            this.email = users.email;
            this.addr = users.addr;
            this.addrPostNo = users.addrPostNo;
            this.tempYn = users.tempYn;
            this.dropYn = users.dropYn;
            this.registerTime = DateUtil.toStringDateTime(users.getRegisterTime());
            this.modifyTime = DateUtil.toStringDateTime(users.getModifyTime());
            this.lastLoginTime = DateUtil.toStringDateTime(users.lastLoginTime);
        }
    }
}
