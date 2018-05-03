import {QuotesChartState} from "./QuotesChartState";

declare var d3: any;

export class ChartsConnector {
    private static state: QuotesChartState;
    private static parent: any;
    private static onStateChangeWrapped;

    static init(this: any): void {
        log(this);
        this.getElement().innerHTML = "<div class='chart tierChart'></div>";
        ChartsConnector.parent = this;
        this.onStateChange = ChartsConnector.onStateChangeWrapped = () => {
            ChartsConnector.onStateChange(ChartsConnector.parent.getState())
        };
        ChartsConnector.innerInit();
        ChartsConnector.onStateChangeWrapped();
    };

    static onStateChange(state: QuotesChartState): void {
        log("Chart.onStateChange, state: ");
        log(state);
        try {
            if (state != null && state.destroy) {
                //ChartsConnector.tierChart.destroy();
                //window.removeEventListener('resize', ChartsConnector.resizeEvent);
                return;
            }
            if (state != null && state.baseQuote && state.calcQuote) {
                if (state.redrawChart) {
                    log("New symbol! Wow! Redrawing!");
                    ChartsConnector.innerInit();
                    //state.chartData.forEach(ChartsConnector.updateData);
                    //ChartsConnector.tierChart.init(ChartsConnector.collectedData, ChartsConnector.getTierChartSize());
                } else {
                    log("It's just an update.");
                    //state.chartData.forEach(ChartsConnector.updateData);
                    //ChartsConnector.tierChart.update(ChartsConnector.collectedData, false);
                }
            } else {
                ChartsConnector.reset();
            }
        } catch (ex) {
            log(ex);
            throw ex;
        }
    }

    private static innerInit() {
        ChartsConnector.reset();
    }

    private static reset() {
        //ChartsConnector.collectedData = [];
        document.getElementsByClassName("tierChart")[0].innerHTML = ChartsConnector.getInnerHtml();
    }

    private static getInnerHtml() {
        return "<div id='baseChart'></div><div id='calcChart'></div>";
    }

}

export let registerBridge = () => {
    log("Register functional bridge");
    window['ru_ulmc_bank_ui_veiws_charts_QuotesChart'] = ChartsConnector.init;
};

export function log(logMsg: any): void {
    if (true) {
        console.log(logMsg);
    }
}