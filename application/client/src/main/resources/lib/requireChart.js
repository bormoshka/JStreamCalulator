//requirejs("./VAADIN/gen/ChartsConnector", function (dep) {
//    //console.log(require(['chartsConnector'], function (s) {
//    //    console.log("ss" + s)
//    //}));
//    console.log("requireCharts");
//    console.log(this);
//    console.log(dep);
//    ru_sberbank_fxp_frontend_ui_views_monitors_PublishedQuotesChart = dep.chartsConnector;
//    window['ru_sberbank_fxp_frontend_ui_views_monitors_PublishedQuotesChart'] =
// dep.chartsConnector; });
(function () {
    SystemJS.import('ChartsConnector').then(function (m) {
        m.registerBridge();
    });
})();