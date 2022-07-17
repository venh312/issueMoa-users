package com.issuemoa.user.users.service.users;

import com.issuemoa.user.users.domain.users.QUsers;
import com.issuemoa.user.users.domain.users.Users;
import com.issuemoa.user.users.domain.users.UsersRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private QUsers users = QUsers.users;

    public Long save(Users.Request request) {
        request.setPasssword(bCryptPasswordEncoder.encode(request.getPasssword()));
        return usersRepository.save(request.toEntity()).getId();
    }

    public HashMap<String, Object> findAll(Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<Users.Response> list = jpaQueryFactory.from(users)
                .offset(page)
                .limit(pageSize)
                .orderBy(users.registerTime.desc())
                .fetch()
                .stream()
                .map(Users.Response::new)
                .collect(Collectors.toList());

        Long totalCnt = (long) jpaQueryFactory.select(users.count()).from(users).fetchOne();

        int totalPage = (int) Math.ceil((float) totalCnt / pageSize);
        totalPage = totalPage == 0 ? 1 : totalPage;

        resultMap.put("list", list);
        resultMap.put("page", page);
        resultMap.put("pageSize", pageSize);
        resultMap.put("totalCnt", totalCnt);
        resultMap.put("totalPage", totalPage);

        return resultMap;
    }

    public Users.Response findById(Long id) {
        return new Users.Response(usersRepository.findById(id).get());
    }

    public long updateUsersPassword(Users.Request request) {
        return jpaQueryFactory.update(users)
                .set(users.password, bCryptPasswordEncoder.encode(request.getPasssword()))
                .set(users.modifyTime, LocalDateTime.now())
                .where(users.id.eq(request.getId()))
                .execute();
    }

    public long updateUsersInfo(Users.Request request) {
        return jpaQueryFactory.update(users)
                .set(users.addr, request.getAddr())
                .set(users.addr, request.getAddrPostNo())
                .set(users.modifyTime, LocalDateTime.now())
                .where(users.id.eq(request.getId()))
                .execute();
    }

    public long updateLastLoginTime(String email) {
        return jpaQueryFactory.update(users)
                .set(users.lastLoginTime, LocalDateTime.now())
                .where(users.email.eq(email))
                .execute();
    }

    public long updateTempYn(Users.Request request) {
        return jpaQueryFactory.update(users)
                .set(users.dropYn, request.getTempYn())
                .set(users.modifyTime, LocalDateTime.now())
                .where(users.id.eq(request.getId()))
                .execute();
    }

    public long updateDropYn(Users.Request request) {
        return jpaQueryFactory.update(users)
                .set(users.dropYn, request.getDropYn())
                .set(users.modifyTime, LocalDateTime.now())
                .where(users.id.eq(request.getId()))
                .execute();
    }

    public int countByEmail(String email) {
        return usersRepository.countByEmail(email);
    }

    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email).get();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("#UsernameNotFoundException"));
    }
}
