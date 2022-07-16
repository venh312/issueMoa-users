package com.issuemoa.user.users.service.users;

import com.issuemoa.user.users.domain.users.QUsers;
import com.issuemoa.user.users.domain.users.Users;
import com.issuemoa.user.users.domain.users.UsersRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private QUsers users = QUsers.users;

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
}
