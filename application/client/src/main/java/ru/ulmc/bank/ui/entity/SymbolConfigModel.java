package ru.ulmc.bank.ui.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;
import ru.ulmc.bank.core.common.exception.UserInputException;
import ru.ulmc.bank.entities.configuration.SymbolConfig;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import static ru.ulmc.bank.ui.entity.RowStatus.NOT_CHANGED;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "inGridId", callSuper = false)
public class SymbolConfigModel {
    private final ThreadLocal<DecimalFormat> formatter = ThreadLocal.withInitial(() -> {
        DecimalFormat df = new DecimalFormat("##.##");
        return df;
    });
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

    public String getBidModifier() {
        return formatter.get().format(symbolEntity.getBidBaseModifier());
    }

    public void setBidModifier(String bidModifier) {
        try {
            symbolEntity.setBidBaseModifier(formatter.get().parse(bidModifier).doubleValue());
        } catch (ParseException e) {
            throw new UserInputException("Wrong format of bidModifier", e);
        }
    }

    public String getOfferModifier() {
        return formatter.get().format(symbolEntity.getOfferBaseModifier());
    }

    public void setOfferModifier(String offerModifier) {
        try {
            symbolEntity.setOfferBaseModifier(formatter.get().parse(offerModifier).doubleValue());
        } catch (ParseException e) {
            throw new UserInputException("Wrong format of offerModifier", e);
        }
    }
}
