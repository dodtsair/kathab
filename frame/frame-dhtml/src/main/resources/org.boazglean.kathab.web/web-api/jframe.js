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
$(document).ready(function(){
    var staticattrib = {
        "src": ["script", "iframe", "frame", "img", "input", "source", "video"],
        "href": ["link", "a", "area", "base"],
        "codebase": ["applet"],
        "manifest": ["html"],
        "formaction": ["button"],
        "poster": ["video"],
        "icon": ["command"],
        "cite": ["blockquote", "del", "q", "ins"],
        "usemap": ["input"],
        "longdesc": ["iframe", "frame", "img"],
        "profile": ["head"],
        "action": ["form"],
        "background": ["body"]
    }
    var xml = function($xmls) {
        var xmls = "";
        $.each($xmls, function(pos) {
        if (window.ActiveXObject){
            xmls = xmls + $xmls[pos].xml;
        } else {
            var serializer = new XMLSerializer();
            xmls = xmls + serializer.serializeToString($xmls[pos]);
        }
        console.log("XML:" + xmls);
        })
        return xmls;
    };
    $('.jframe').each (function () {
        var $wrapper = $(this);
        var jframeSrc = $wrapper.attr("data-src")   ;
        $.get($wrapper.attr("data-src"), "xml").done(function(data) {
            var $jframe = $($.parseXML(data));
            var text = $jframe.find('body').attr("id");
            var fetchId = $jframe.find('body');
            $.each(staticattrib, function(index, value) {
                var attrib = index;
                $.each(value, function() {
                    var tag = this;
                    $jframe.find(tag + '[' + attrib + '^="."]').each(function (index) {
                        $(this).attr(attrib, $wrapper.attr("data-src") + "/../" + $(this).attr(attrib));
                    });
                });
            });
            $('head').append(xml($jframe.find('head').contents().not('script')));
            $wrapper.html(xml($jframe.find('body').contents().not('script')));

            $jframe.contents().find('script').each( function (index) {
                var script   = document.createElement("script");
                script.type  = "text/javascript";
                script.src   = $(this).attr("src");
                document.body.appendChild(script);
            });
        });
    });
});


