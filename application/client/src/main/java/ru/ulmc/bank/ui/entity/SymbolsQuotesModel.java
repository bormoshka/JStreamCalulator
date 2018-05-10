package ru.ulmc.bank.ui.entity;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"symbol", "volume"}, callSuper = false)
public class SymbolsQuotesModel {
    private boolean isParent = false;
    private String symbol;
    private String volume = "";
    private String baseBid;
    private String baseOffer;
    private String baseDate;

    private String calcBid;
    private String calcOffer;
    private String calcDate;

    public SymbolsQuotesModel(String symbol) {
        this.symbol = symbol;
        isParent = true;
    }
}
