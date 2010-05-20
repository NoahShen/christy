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