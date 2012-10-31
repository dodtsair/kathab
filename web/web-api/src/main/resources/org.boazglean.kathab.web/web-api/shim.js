requirejs.config({
    shim: {
        'com.beebole.pure/pure-js/pure': {
            //These script dependencies should be loaded before loading
            //pure.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'org.jquery/jquery-sparkline/jquery.sparkline': {
            //These script dependencies should be loaded before loading
            //sparkline.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'org.boazglean.kathab.web/web-api/jframe': {
            //These script dependencies should be loaded before loading
            //jframe.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'org.boazglean.kathab.web/web-api/hash': {
            //These script dependencies should be loaded before loading
            //hash.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'com.jqplot/jqplot/jquery.jqplot': {
            //These script dependencies should be loaded before loading
            //jqplot.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'com.jqplot/jqplot/plugins/jqplot.pieRenderer': {
            //These script dependencies should be loaded before loading
            //jqplot.js
            deps: ['com.jqplot/jqplot/jquery.jqplot'],
        },
        'com.jqplot/jqplot/plugins/jqplot.barRenderer': {
            //These script dependencies should be loaded before loading
            //jqplot.js
            deps: ['com.jqplot/jqplot/jquery.jqplot'],
        },
        'com.jqplot/jqplot/plugins/jqplot.categoryAxisRenderer': {
            //These script dependencies should be loaded before loading
            //jqplot.js
            deps: ['com.jqplot/jqplot/jquery.jqplot'],
        },
        'com.jqplot/jqplot/plugins/jqplot.canvasAxisTickRenderer': {
            //These script dependencies should be loaded before loading
            //jqplot.js
            deps: ['com.jqplot/jqplot/jquery.jqplot'],
        },
        'com.jqplot/jqplot/plugins/jqplot.dateAxisRenderer': {
            //These script dependencies should be loaded before loading
            //jqplot.js
            deps: ['com.jqplot/jqplot/jquery.jqplot'],
        },
        'com.jqplot/jqplot/plugins/jqplot.canvasTextRenderer': {
            //These script dependencies should be loaded before loading
            //jqplot.js
            deps: ['com.jqplot/jqplot/jquery.jqplot'],
        },
        'com.jqplot/jqplot/plugins/jqplot.pointLabels': {
            //These script dependencies should be loaded before loading
            //jqplot.js
            deps: ['com.jqplot/jqplot/jquery.jqplot'],
        },
    }
});