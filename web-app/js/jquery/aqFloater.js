/* aqFloater v1.3.3
   Floats an element that attaches itself to a part of the browser window.
   Copyright (C) 2011 paul pham <http://aquaron.com/jquery/aqFloater>

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
(function($){$.fn.aqFloater=function(o){var _0=$.extend({offsetX:0,offsetY:0,attach:'',duration:50,opacity:1,overlay:0,overlayOpacity:0.8,overlayBackground:'black',offsetFromWindow:100,zIndex:2000},o);var _8=function(o){var _11=(_0.attach.match(/n/)?0:(_0.attach.match(/s/)?($(window).height()-o.outerHeight(true)-10):Math.round(($(window).height()-o.height())/2)));var _10=(_0.attach.match(/w/)?0:(_0.attach.match(/e/)?($(window).width()-o.outerWidth(true)-10):Math.round(($(window).width()-o.width())/2)));o.animate({top:(_11+$(document).scrollTop()+_0.offsetY),left:(_10+$(document).scrollLeft()+_0.offsetX)},{queue:false,duration:_0.duration,complete:function(){if(_0.overlay&&_0.attach===''){_9(o)}else{$('#aqFloater').hide()}}})};var _9=function(o){var _5=$(document).width(),_4=$(document).height(),_3=0,_2=0;if(_0.overlay>1){_5=o.outerWidth()+_0.overlay*2;_4=o.outerHeight()+_0.overlay*2;_3=o.position().top-_0.overlay;_2=o.position().left-_0.overlay}if($('#aqFloater').length){$('#aqFloater').css({width:_5,height:_4,top:_3,left:_2}).show();return false}o.css({margin:0});$('<div\/>').attr('id','aqFloater').css({width:_5,height:_4,position:'absolute',opacity:_0.overlayOpacity,zIndex:_0.zIndex-1,top:_3,left:_2,backgroundColor:_0.overlayBackground}).show().appendTo('body')};return this.each(function(){var _1=$(this).css({position:'absolute',opacity:_0.opacity,zIndex:_0.zIndex}).bind('close',function(){if(_0.overlay){$('#aqFloater').hide()}_1.hide();return true});if(_0.offsetFromWindow>0){var _7=parseInt($(window).width(),10)-_0.offsetFromWindow,_6=parseInt($(window).height(),10)-_0.offsetFromWindow;if(_1.width()>_7){_1.css({width:_7,height:'auto'})}if(_1.height()>_6){_1.css({width:'auto',height:_6})}}$(window).bind('scroll resize',function(){if(_1.is(':visible')){_8(_1)}}).trigger('scroll');return true})}})(jQuery);