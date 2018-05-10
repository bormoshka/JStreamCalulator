package ru.ulmc.bank.ui.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;
import ru.ulmc.bank.core.common.exception.UserInputException;
import ru.ulmc.bank.entities.configuration.SymbolConfig;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.UUID;

import static ru.ulmc.bank.ui.entity.RowStatus.NOT_CHANGED;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "inGridId", callSuper = false)
public class SymbolConfigModel {
    private final ThreadLocal<DecimalFormat> formatter = ThreadLocal.withInitial(() -> new DecimalFormat("##.##"));
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
        return formatter.get().format(symbolEntity.getBidBaseModifier() * 100);
    }

    public void setBidModifier(String bidModifier) {
        try {
            symbolEntity.setBidBaseModifier(formatter.get().parse(bidModifier.replace(".", ",")).doubleValue() / 100);
        } catch (ParseException e) {
            throw new UserInputException("Wrong format of bidModifier", e);
        }
    }

    public String getOfferModifier() {
        return formatter.get().format(symbolEntity.getOfferBaseModifier() * 100);
    }

    public void setOfferModifier(String offerModifier) {
        try {
            symbolEntity.setOfferBaseModifier(formatter.get().parse(offerModifier.replace(".", ",")).doubleValue() / 100);
        } catch (ParseException e) {
            throw new UserInputException("Wrong format of offerModifier", e);
        }
    }

    public String getMaxBidModifier() {
        return formatter.get().format(symbolEntity.getBidMaxModifier() * 100);
    }

    public void setMaxBidModifier(String bidModifier) {
        try {
            symbolEntity.setBidMaxModifier(formatter.get().parse(bidModifier.replace(".", ",")).doubleValue() / 100);
        } catch (ParseException e) {
            throw new UserInputException("Wrong format of bidModifier", e);
        }
    }

    public String getMaxOfferModifier() {
        return formatter.get().format(symbolEntity.getOfferMaxModifier() * 100);
    }

    public void setMaxOfferModifier(String offerModifier) {
        try {
            symbolEntity.setOfferMaxModifier(formatter.get().parse(offerModifier.replace(".", ",")).doubleValue() / 100);
        } catch (ParseException e) {
            throw new UserInputException("Wrong format of offerModifier", e);
        }
    }
}
