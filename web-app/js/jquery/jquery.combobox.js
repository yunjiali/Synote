/* Combobox widget for the autocomplete plugin. */
(function ($) {
    $.widget("ui.combobox", {
        _create: function () {
            var self = this,
				select = this.element.hide(),
				selected = select.children(":selected"),
				value = selected.val() ? selected.text() : "",
				regSearch = /^[^a-zA-Z0-9]*([a-zA-Z0-9])/i,
				comboData = select.children("option").map(function () {
					if (this.value ) {
						var text = $(this).text(), 
							labelHtml = self.options.label ? self.options.label(this) : text; //allows list customization
						
						return {
							label: labelHtml,
							value: $.trim(text),
							option: this
						};
					}
				});
				
            var input = this.input = $("<input type='text' />")
					.insertAfter(select)
					//Yunjia: this has been changed from value to value.trim()
					.val($.trim(value))
					.keydown( function( event ) {
							var keyCode = $.ui.keyCode;
							switch( event.keyCode ) {
								case keyCode.PAGE_UP:
								case keyCode.PAGE_DOWN:
								case keyCode.UP:
								case keyCode.DOWN:
								case keyCode.ENTER:
								case keyCode.NUMPAD_ENTER:
								case keyCode.TAB:
								case keyCode.ESCAPE:
									//let autocomplete handle these
									break;
								default:
									//prevent autocomplete doing anything
									event.stopImmediatePropagation();
									//only react to [a-zA-Z0-9]
									if ((event.keyCode < 91 && event.keyCode > 59)
										|| (event.keyCode < 58 && event.keyCode > 47)) {
										
										var str = String.fromCharCode(event.keyCode).toLowerCase(), currVal = input.val(), opt;
										
										//find all options whose first alpha character matches that pressed
										var matchOpt = select.children().filter(function() {
											var test = regSearch.exec(this.text);
											return (test && test.length == 2 && test[1].toLowerCase() == str);
										});
										
										if (!matchOpt.length ) return false;
										
										//if there is something selected we need to find the next in the list
										if (currVal.length) {
											var test = regSearch.exec(currVal);
											if (test && test.length == 2 && test[1].toLowerCase() == str) {
												//the next one that begins with that letter
												matchOpt.each(function(ix, el) {
													if (el.selected) {
														if ((ix + 1) <= matchOpt.length-1) {
															opt = matchOpt[ix + 1];
														}
														return false;
													}
												});
											}
										} 
										
										//fallback to the first one that begins with that character
										if (!opt)
											opt = matchOpt[0];
										
										//select that item
										opt.selected = true;
										input.val(opt.text);
										
										//if the dropdown is open, find it in the list
										if (input.autocomplete("widget").is(":visible")) {
											input.data("autocomplete").widget().children('li').each(function() {		
												var $li = $(this);
												if ($li.data("item.autocomplete").option == opt) {
													input.data("autocomplete").menu.activate(event,$li);
													return false;
												}
											});
										}
									}
									//ignore all other keystrokes
									return false;
									break;
								}
					  })
					.autocomplete({
					    delay: 0,
					    minLength: 0,
					    source: function (request, response) { response(comboData); },
					    select: function (event, ui) {
					        ui.item.option.selected = true;
					        self._trigger("selected", event, {
					            item: ui.item.option
					        });
					    },
					    change: function (event, ui) {
							if (!ui.item) {					
								var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex($(this).val()) + "$", "i"),
									valid = false;
								select.children("option").each(function () {
									if ($(this).text().match(matcher)) {
										this.selected = valid = true;
										return false;
									}
								});
								if (!valid) {
									// remove invalid value, as it didn't match anything
									$(this).val("");
									select.val("");
									input.data("autocomplete").term = "";
									return false;
								}
							}
					    }
					})
					.addClass("ui-widget ui-widget-content ui-corner-left")
					.click(function() { self.button.click(); })
					.bind("autocompleteopen", function(event, ui){
						//find the currently selected item and highlight it in the list
						var opt = select.children(":selected")[0];
						input.data("autocomplete").widget().children('li').each(function() {		
							var $li = $(this);
							if ($li.data("item.autocomplete").option == opt) {
								input.data("autocomplete").menu.activate(event,$li);
								return false;
							}
						});
					});

            input.data("autocomplete")._renderItem = function (ul, item) {
                return $("<li></li>")
					.data("item.autocomplete", item)
					.append("<a href='#'>" + item.label + "</a>")
					.appendTo(ul);
            };
			
            this.button = $("<button type='button'>&nbsp;</button>")
					.attr("tabIndex", -1)
					.attr("title", "Show All Items")
					.insertAfter(input)
					.button({
					    icons: {
					        primary: "ui-icon-triangle-1-s"
					    },
					    text: false
					})
					.removeClass("ui-corner-all")
					.addClass("ui-corner-right ui-button-icon")
					.click(function () {
					    // close if already visible
					    if (input.autocomplete("widget").is(":visible")) {
					        input.autocomplete("close");
					        return;
					    }

					    // pass empty string as value to search for, displaying all results
					    input.autocomplete("search", "");
					    input.focus();
					});
        },
		
		//allows programmatic selection of combo using the option value
        setValue: function (value) {
            var $input = this.input;
            $("option", this.element).each(function () {
                if ($(this).val() == value) {
                    this.selected = true;
                    $input.val(this.text);
					return false;
                }
            });
        },

        destroy: function () {
            this.input.remove();
            this.button.remove();
            this.element.show();
            $.Widget.prototype.destroy.call(this);
        }
    });
})(jQuery);
