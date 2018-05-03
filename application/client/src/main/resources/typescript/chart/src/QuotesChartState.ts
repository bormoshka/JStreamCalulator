import {QuoteEntity} from "./QuoteEntity";

export interface QuotesChartState {
    baseQuote: QuoteEntity;
    calcQuote: QuoteEntity;
    redrawChart: boolean;
    destroy: boolean;
}
