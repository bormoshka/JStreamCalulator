package ru.ulmc.bank.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.ulmc.bank.entities.persistent.system.Permission;
@Repository
public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long> {
    Permission findByName(String code);
}
