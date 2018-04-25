package ru.ulmc.bank.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.ulmc.bank.entities.persistent.system.UserRole;
@Repository
public interface UserRoleRepository extends PagingAndSortingRepository<UserRole, Long> {
    UserRole findByName(String name);
}
