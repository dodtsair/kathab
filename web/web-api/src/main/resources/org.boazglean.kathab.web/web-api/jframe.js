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
    $('.jframe').load(function () {
        var $jframe = $(this);
        var $wrapper = $(document.createElement('div'));
        $jframe.each(function() {
            $.each(this.attributes, function(i, attrib) {
                $wrapper.attr(attrib.name, attrib.value);
            });
        });
        $wrapper.append($jframe.contents().find('body').contents().not('script'));
        var $head = $(document.createElement('head'));
        $head.append($jframe.contents().find('head').find('link'))
        $.each(staticattrib, function(index, value) {
            var attrib = index;
            $.each(value, function() {
                var tag = this;
                ($wrapper, $head).find(tag + '[' + attrib + '^="."]').each(function (index) {
                    $(this).attr(attrib, $jframe.attr("src") + "/../" + $(this).attr(attrib));
                });
            });
        });
        $('head').append($head.contents());
        $wrapper.insertBefore($jframe);

        $jframe.contents().find('script').each( function (index) {
            var script   = document.createElement("script");
            script.type  = "text/javascript";
            script.src   = $jframe.attr("src") + "/../" + $(this).attr("src");
            document.body.appendChild(script);
        })
        $jframe.remove();
    });
});


