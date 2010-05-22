// =========start of Pagination

(function($) {
	
	$.fn.Pagination = function(options) {
		this.opts = $.extend({
        	renderTo: $(document.body),
			total: 0,
			current: 0,
			onChanged: function() { }
        }, options);
        
        this.current = this.opts.current;
        this.total = this.opts.total;
		this._init();
	};
	
	$.fn.Pagination.prototype = {
		_init:function() {
			var _this = this;
			this.opts.renderTo = (typeof this.opts.renderTo == 'string' ? $(this.opts.renderTo) : this.opts.renderTo);
            
            var pageination = $("<div class='pagination'></div>");
            
            var firstPage = $("<span><a href='javascript:void(0);'>&lt;&lt;</a></span>");
            pageination.append(firstPage);
            
            var prevPage = $("<span><a href='javascript:void(0);'>&lt;</a></span>");
            pageination.append(prevPage);
            
            var pageInput = $("<input id='pageInput' value='" + this.current + "' class='itext' type='text'/>");
            pageInput.focus(function() {
            	$(this).select();
            });
            pageination.append(pageInput);
            var count = $("<input value=' / " + this.total + "' class='icount' readonly='readonly' type='text'/>");
            pageination.append(count);
            var go = $("<input name='go' value='GO' class='ibutton' type='button'/>");
			pageination.append(go);
			
			var nextPage = $("<span><a href='javascript:void(0);'>&gt;</a></span>");
			pageination.append(nextPage);
            var lastPage = $("<span><a href='javascript:void(0);'>&gt;&gt;</a></span>");
            pageination.append(lastPage);

			firstPage.click(function() {
				_this.setCurrentPage(1);
			});
			prevPage.click(function() {
				var currentPage = _this.getCurrentPage();
				_this.setCurrentPage(currentPage - 1);
			});
			
			go.click(function() {
				var input = pageInput.val();
				_this.setCurrentPage(input);
				
			});
			
			nextPage.click(function() {
				var currentPage = _this.getCurrentPage();
				_this.setCurrentPage(currentPage + 1);
			});
			
			lastPage.click(function() {
				_this.setCurrentPage(_this.getTotal());
			});
			
			this.opts.renderTo.empty();
            this.opts.renderTo.append(pageination);
            this.pageination = pageination;
		},
		
		getCurrentPage: function() {
            return this.current;
        },
        
        getTotal:function() {
        	return this.total;
        },
        
        setCurrentPage: function(current) {
        	var pageInput = this.pageination.find("#pageInput");
        	if(isNaN(current)) {
				pageInput.val(this.getCurrentPage());
				return;
			}

			var inputPage = parseInt(current);
			if (inputPage < 1 || inputPage > this.getTotal()) {
				pageInput.val(this.getCurrentPage());
				return;
			}
			if (this.current == inputPage) {
				return;
			}
			
        	this.current = inputPage;
        	pageInput.val(inputPage);
        	if (this.opts.onChanged) {
        		this.opts.onChanged(current);
        	}
        }
	};
})(jQuery);

// =========end of Pagination


// =========start of sysPanel
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
			
			var sysMessageContainer = $("<div id='sysMessageContainer'></div>");
			
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
			
			sysMessageContainer.append(content);
			
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
			
			sysMessageContainer.append(prevNext);
			
			panel.append(sysMessageContainer);
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

// =========end of sysPanel





// =========start of Tab
﻿/**
 * @fileoverview Giant Interective Group, Inc. Javascript Library v#version.
 * 该Javascript UI库是基于jQuery的扩展。
 * <pre>
 * Copyright (C) 2004-2009 Giant Interective Group, Inc. All rights reserved.
 * 版权所有 2004-2009 上海巨人网络科技有限公司
 * </pre>
 * 
 * fixed By Noah about callBackHideEvent(index)
 * 
 * @version 1.0.0, #date 2009-04-06
 * @author  Zhangkai
 * Depend on jQuery 1.3.x
 */
 (function($) {
    var isShow = false;
    $.fn.tab = function(options) {
        this.opts = $.extend({},$.fn.tab.defaults, options);
		this._init();
		this.disableArr=[];
	}	
	$.fn.tab.prototype={
		_init:function(){
			var _this = this;
			if($(_this.opts.tabList).length>0){
				$(_this.opts.tabList).each(function(index){
						$(this).bind(_this.opts.eventType,function(){
							//判断是否禁用，是否效果还在执行中，是否在当前选中的按钮上
							if($.inArray(index,_this.disableArr)==-1&&(!isShow)&&$(this).attr("class").indexOf(_this.opts.tabActiveClass)==-1){
								//callback
								if(_this.opts.callBackStartEvent){
									_this.opts.callBackStartEvent(index);
								}
								$(_this.opts.tabList).removeClass(_this.opts.tabActiveClass);
								$(this).addClass(_this.opts.tabActiveClass);
								
								if (_this.opts.tabParentActiveClass) {
									$(_this.opts.tabList).parent().removeClass(_this.opts.tabParentActiveClass);
									$(this).parent().addClass(_this.opts.tabParentActiveClass);
								}
								
								
								_this._showContent(index);
							}
						});
					});
			}
		},
		_showContent:function(index){
			isShow = true;
			var _this = this;
			switch(_this.opts.showType){
				case "show":
					var visibleJqObj = $(_this.opts.contentList+":visible");
					visibleJqObj.hide();
					//callback
					if(_this.opts.callBackHideEvent){
						_this.opts.callBackHideEvent(visibleJqObj.index());
					}
					$(_this.opts.contentList).eq(index).show();
					if(_this.opts.callBackShowEvent){
						_this.opts.callBackShowEvent(index);
					}
					isShow =false;
					break;
				case "fade":
					var visibleJqObj = $(_this.opts.contentList+":visible");
					visibleJqObj.fadeOut(_this.opts.showSpeed,function(){
						//callback
						if(_this.opts.callBackHideEvent){
							_this.opts.callBackHideEvent(visibleJqObj.index());
						}
						$(_this.opts.contentList).eq(index).fadeIn(function(){
							//callback
							if(_this.opts.callBackShowEvent){
								_this.opts.callBackShowEvent(index);
							}
							isShow =false;
						});
					});
					break;
				case "slide":
					var visibleJqObj = $(_this.opts.contentList+":visible");
					visibleJqObj.slideUp(_this.opts.showSpeed,function(){
						//callback
						if(_this.opts.callBackHideEvent){
							_this.opts.callBackHideEvent(visibleJqObj.index());
						}
						$(_this.opts.contentList).eq(index).slideDown(function(){
							//callback
							if(_this.opts.callBackShowEvent){
								_this.opts.callBackShowEvent(index);
							}
							isShow =false;
						});
					});
					break;
			}
		},
		setDisable:function(index){
			//如果不存在
			var _this = this;
			if($.inArray(index,this.disableArr)==-1){
				this.disableArr.push(index);
				$(_this.opts.tabList).eq(index).addClass(_this.opts.tabDisableClass);
			}
		},
		setEnable:function(index){
			//如果不存在
			var _this = this;
			var i =$.inArray(index,this.disableArr);
			if(i>-1){
				this.disableArr.splice(i,1);
				$(_this.opts.tabList).eq(index).removeClass(_this.opts.tabDisableClass);
			}
		},
		triggleTab:function(index){
			$(this.opts.tabList).eq(index).trigger(this.opts.eventType);
		}
	}
    $.fn.tab.defaults = {
	   tabList:".ui-tab-container .ui-tab-list li",
	   contentList:".ui-tab-container .ui-tab-content",
       tabActiveClass:"ui-tab-active",
	   tabDisableClass:"ui-tab-disable",
	   eventType:"click",									//触发事件，有click和mouseover两种类型
	   showType:"show",										//显示方式，show 直接显示，fade渐变，slide滑动
	   showSpeed:200,										//显示速度，单位为毫秒
	   callBackStartEvent:null,
	   callBackHideEvent:null,
	   callBackShowEvent:null
    };
})(jQuery);

// =========end of Tab