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

requirejs(['org.jquery/jquery-js/jquery', 'com.beebole.pure/pure-js/pure', 'org.jquery/jquery-sparkline/jquery.sparkline', 'org.boazglean.kathab.web/web-api/hash'],
function () {
    $(document).ready(function() {
        var prefixRender = $('.prefix-filter').compile(
            {
                '.prefix-entry': {
                    'point<-points' :{
                        'div.prefix-name':'point.key',
                        'div.prefix-bar@data-values': function(arg) {
                            var sparkline = [];
                            sparkline.push(arg.context.mean);
                            sparkline.push(arg.context.points[arg.pos].value);
                            sparkline.push(arg.context.mean + arg.context.stdDeviation*2);
                            sparkline.push(arg.context.mean + arg.context.stdDeviation);
                            sparkline.push(arg.context.mean - arg.context.stdDeviation);
                            return sparkline.join();
                        }
                    },
                    sort:function(a,b) {
                        return b.value - a.value;
                    }
                },
            }
        )

        var levelRender = $('.level-filter').compile(
        {
            '.@data-values' : function(arg) {
                var pieKeys = [];
                var pieData = [];
                for(var dataPoint in arg.context) {
                    pieKeys.push(dataPoint);
                }
                pieKeys.sort();
                for(var keyPos in pieKeys) {
                    pieData.push(arg.context[pieKeys[keyPos]])
                }
                return pieData.join();
            }
        });
        $(document).on("/summary/level", function(event, data) {
            $('.level-filter').replaceWith(levelRender(data));
            $(".level-filter").sparkline('html', {
                type: 'pie',
                sliceColors: ['rgba(0,0,256,100)','rgba(256,0,0,100)','rgba(0,256,0,100)','rgba(256,256,256,100)','rgba(256,256,0,100)'],
                width: '100%',
                height: '100%',
                tagValuesAttribute: 'data-values',
            });
        });
        $(document).on("/summary/prefix", function(event, data) {
            $('.prefix-filter').replaceWith(prefixRender(data));
            $('.prefix-bar').sparkline('html', {
                type: 'bullet',
                targetColor: 'rgba(0,0,0,100)',
                rangeColors: ['rgba(100,100,100,100)','rgba(66,66,66,100)','rgba(33,33,33,100)'],
                width: '100%',
                tagValuesAttribute: 'data-values',
            });
        });
        var fetchData = function() {
            if(window.location.hash == undefined || window.location.hash.length == 0) {
                $.getScript('../../api/summary/level/all')
                $.getScript('../../api/summary/prefix/all')
            }
            else {
                var hash = $.hash();
                if(hash.level != undefined && hash.level.length > 0)
                {
                    $.getScript('../../api/summary/level/level=' + hash.level)
                }
                else {
                    $.getScript('../../api/summary/level/all')
                }
                if(hash.prefix != undefined && hash.prefix.length > 0)
                {
                    $.getScript('../../api/summary/prefix/prefix=' + hash.prefix)
                }
                else {
                    $.getScript('../../api/summary/prefix/all')
                }
            }
        }

        $(window).bind( 'hashchange', fetchData);
        fetchData();
    });
});