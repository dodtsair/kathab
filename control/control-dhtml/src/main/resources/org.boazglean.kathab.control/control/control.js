/*
 * The MIT License
 *
 * Copyright 2012 mpower.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


requirejs.config({
    //By default load any module IDs from js/lib
    baseUrl: '..',
});

requirejs(['org.jquery/jquery-js/jquery',
    'com.beebole.pure/pure-js/pure',
    'org.jquery/jquery-sparkline/jquery.sparkline',
    'org.boazglean.kathab.web/web-api/hash',
    'com.jqplot/jqplot/plugins/jqplot.pieRenderer',
    'com.jqplot/jqplot/plugins/jqplot.barRenderer',
    'com.jqplot/jqplot/plugins/jqplot.pointLabels',
],
function () {
    $(document).ready(function() {
        var levels = {
            'TRACE': {
                color: 'rgba(256, 256, 256, 256)',
            },
            'DEBUG': {
                color: 'rgba(0, 0, 256, 256)',
            },
            'INFO': {
                color: 'rgba(0, 256, 0, 256)',
            },
            'WARN': {
                color: 'rgba(256, 256, 0, 256)',
            },
            'ERROR': {
                color: 'rgba(256, 0, 0, 256)',
            },
        }
        //Inject the data into the dom
        //First we start with the render functions that define
        //where in the dom the data goes
        var prefixRender = $('.prefix-filter').compile(
            {
                '.@data-values' : function(arg) {
                    var barData = []
                    for(var prefix in arg.context.points) {
                        barData.push(arg.context.points[prefix]);
                    }
                    return JSON.stringify(barData);
                },
                '.@data-labels' : function(arg) {
                    var barLabels = [];
                    for(var prefix in arg.context.points) {
                        barLabels.push(prefix);
                    }
                    return JSON.stringify(barLabels);
                }
            });


        var levelRender = $('.level-filter').compile(
        {
            '.@data-values' : function(arg) {
                var pieData = [];
                for(var level in levels) {
                    if(arg.context[level] != undefined) {
                            pieData.push([level, arg.context[level]]);
                    }
                }
                return JSON.stringify(pieData);
            },
            '.@data-colors' : function(arg) {
                var pieColors = [];
                for(var level in levels) {
                    if(arg.context[level] != undefined) {
                            pieColors.push(levels[level].color);
                    }
                }
                return JSON.stringify(pieColors);
            },
        });

        //Then we bind to events triggered by the data loading.
        $(document).on("/summary/level", function(event, data) {
            $('.level-filter').replaceWith(levelRender(data));

            //Now that the data is in the dom render the graph
            var plot = $('.level-filter').jqplot([JSON.parse($('.level-filter').attr('data-values'))],
            {
                seriesDefaults: {
                    renderer: $.jqplot.PieRenderer,
                    rendererOptions: {
                        showDataLabels: false,
                        dataLabels: 'label',
                        padding: 0,
                        shadowAlpha: 0,
                    },
                },
                legend: {
                    show: true,
                    location: 'e',
                },
                title: {
                    show: false,
                },
                grid: {
                    drawBorder: false,
                    shadow: false,
                    background: 'rgba(256, 256, 256, 0)',
                },
                seriesColors: JSON.parse($('.level-filter').attr('data-colors')),
            });
        });
        $(document).on("/summary/prefix", function(event, data) {
            $('.prefix-filter').replaceWith(prefixRender(data));

            //Now that the data is in the dom render the graph
            var plot = $('.prefix-filter').jqplot([JSON.parse($('.prefix-filter').attr('data-values'))],
            {
                seriesDefaults: {
                    renderer: $.jqplot.BarRenderer,
                    rendererOptions: {
                        barDirection: 'horizontal',
                        barWidth: 15,
                    },
                    pointLabels: {
                        show: true,
                        location: 'w',
                        labels: JSON.parse($('.prefix-filter').attr('data-labels')),
                        edgeTolerance: -1000,
                    },
                },
                grid: {
                    drawBorder: false,
                    shadow: false,
                    background: 'rgba(256, 256, 256, 0)',
                    drawGridlines: false,
                },
                axes : {
                    yaxis: {
                        showTicks: false,
                    }
                }
            });
        });
        var fetchData = function() {
            var hash = $.hash();
            var hasLevel = hash == undefined || hash.level != undefined && hash.level.length > 0;
            var hasPrefix = hash == undefined || hash.prefix != undefined && hash.prefix.length > 0
            if(!hasLevel && !hasPrefix) {
                $.getScript('../../api/summary/level/all')
                $.getScript('../../api/summary/prefix/all')
            }
            else if(hasLevel && hasPrefix) {
                $.getScript('../../api/summary/level/level=' + hash.level + '&prefix=' + hash.prefix);
                $.getScript('../../api/summary/prefix/level=' + hash.level + '&prefix=' + hash.prefix);
            }
            else if(hasLevel && !hasPrefix) {
                $.getScript('../../api/summary/level/level=' + hash.level);
                $.getScript('../../api/summary/prefix/level=' + hash.level);
            }
            else if(!hasLevel && !hasPrefix) {
                $.getScript('../../api/summary/level/prefix=' + hash.prefix);
                $.getScript('../../api/summary/prefix/prefix=' + hash.prefix);
            }
        }

        $(window).bind( 'hashchange', fetchData);
        fetchData();
    });
});