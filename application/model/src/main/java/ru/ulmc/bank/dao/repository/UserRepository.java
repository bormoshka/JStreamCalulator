package ru.ulmc.bank.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.ulmc.bank.entities.persistent.system.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByLogin(String login);

    User findByLoginAndPassword(String login, String password);

    User findByFullNameAndLogin(String fullname, String login);

    Integer countByLogin(String login);
}
