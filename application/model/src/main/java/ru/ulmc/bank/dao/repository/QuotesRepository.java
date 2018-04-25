package ru.ulmc.bank.dao.repository;

import org.springframework.stereotype.Repository;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

@Repository
public interface QuotesRepository extends org.springframework.data.repository.Repository<BaseQuote, String> {
    void save(BaseQuote quote);
}
