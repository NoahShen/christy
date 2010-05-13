(function($) {
	var defaultOpts = {
		panelAdded: function() {},
		panelRemoved: function() {}
	}
	$.fn.extend({
		
		sysPanel: function(opts) {
			if (opts != null) {
				defaultOpts = opts;
			}
			var panel = $(this).addClass("syspanel");
			
			var content = $("<div>" +
								"<table>" +
									"<tr>" +
										"<td>" +
											"<div class='messagepanel'></div>" +
										"</td>" +
										"<td valign='top'>" +
											"<div class='syspanel-close'>X</div>" +
										"</td>" +
									"</tr>" +
								"</table>" +
							"</div>");
			content.find(".syspanel-close").click(function(){
				var messagepanel = content.find(".messagepanel");
				var currentPanel = messagepanel.children("div:visible");
				var nextShowPanel = currentPanel.next();
				if (!nextShowPanel[0]) {
					nextShowPanel = currentPanel.prev();
				}
				currentPanel.remove();
				
				nextShowPanel.siblings().hide();
				nextShowPanel.show();
				
				if (defaultOpts.panelRemoved) {
					defaultOpts.panelRemoved(currentPanel);
				}
			});	
			
			panel.append(content);
			
			var prevNext = $("<div></div>")
									.addClass("prevNext");
			var prev = $("<span></span>")
							.text("<");
			prev.click(function(){
				var messagepanel = content.find(".messagepanel");
				var currentPanel = messagepanel.children("div:visible");
				var prevPanel = currentPanel.prev();
				if (prevPanel[0]) {
					prevPanel.siblings().hide();
					prevPanel.show();
				}
			});
			prevNext.append(prev);
			
			var next = $("<span></span>")
							.text(">");
			next.click(function(){
				var messagepanel = content.find(".messagepanel");
				var currentPanel = messagepanel.children("div:visible");
				var nextPanel = currentPanel.next();
				if (nextPanel[0]) {
					nextPanel.siblings().hide();
					nextPanel.show();
				}
			});
			prevNext.append(next);
			
			panel.append(prevNext);
		},
		
		addPanel: function(newPanel) {
			var panel = $(this);
			var messagepanel = panel.find(".messagepanel");
			
			if (messagepanel.children().size() > 0) {
				newPanel.hide();
			}
			messagepanel.append(newPanel);
			
			if (defaultOpts.panelAdded) {
				defaultOpts.panelAdded(newPanel);
			}
		},
		
		removePanel: function(panel) {
			var nextShowPanel = panel.next();
				if (!nextShowPanel[0]) {
					nextShowPanel = panel.prev();
				}
				panel.remove();
				
				nextShowPanel.siblings().hide();
				nextShowPanel.show();
				
				if (defaultOpts.panelRemoved) {
					defaultOpts.panelRemoved(panel);
				}
			
		},
		getPanelCount: function() {
			var messagepanel = $(this).find(".messagepanel");
			return messagepanel.children().size();
		}
	});
})(jQuery);