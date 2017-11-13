package ru.ulmc.bank.ui.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;
import ru.ulmc.bank.entities.configuration.SymbolConfig;

import java.util.Date;

import static ru.ulmc.bank.ui.entity.RowStatus.NOT_CHANGED;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "inGridId", callSuper = false)
public class SymbolConfigModel {
    private final long inGridId;
    @Delegate
    private SymbolConfig symbolEntity;

    private RowStatus rowStatus = NOT_CHANGED;
    private boolean valid = false;

    public SymbolConfigModel() {
        inGridId = new Date().getTime();
    }

    public SymbolConfigModel(SymbolConfig symbolEntity) {
        inGridId = new Date().getTime();
        this.symbolEntity = symbolEntity;
    }

}
