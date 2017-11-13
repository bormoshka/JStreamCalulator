package ru.ulmc.bank.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.ulmc.bank.entities.persistent.system.UserRole;

public interface UserRoleRepository extends PagingAndSortingRepository<UserRole, Long> {
    UserRole findByName(String name);
}
