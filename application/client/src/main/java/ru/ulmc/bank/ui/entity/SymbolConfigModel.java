package ru.ulmc.bank.ui.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;
import ru.ulmc.bank.entities.configuration.SymbolConfig;

import java.util.Date;
import java.util.UUID;

import static ru.ulmc.bank.ui.entity.RowStatus.NOT_CHANGED;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "inGridId", callSuper = false)
public class SymbolConfigModel {
    private final String inGridId;
    @Delegate
    private SymbolConfig symbolEntity;

    private RowStatus rowStatus = NOT_CHANGED;
    private boolean valid = false;

    public SymbolConfigModel(String symbol) {
        symbolEntity = new SymbolConfig(symbol, 0d, 0d);
        inGridId = UUID.randomUUID().toString(); //todo: refactor with simple increment
    }

    public SymbolConfigModel(SymbolConfig symbolEntity) {
        inGridId = UUID.randomUUID().toString();
        this.symbolEntity = symbolEntity;
    }

}
