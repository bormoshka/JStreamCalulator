export interface QuoteEntity {
    symbol: string;
    prices: Array<PriceEntity>;
}

export interface PriceEntity {
    volume: string;
    bid: string;
    offer: string;
    visible: boolean;
}