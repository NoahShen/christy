﻿/// <reference path="jquery-1.2.6.js" />
/// <reference path="jquery.dimensions.js" />


(function($){

    $.layoutEngine = function(layoutSettings){        
       
        $(window).resize(function(){            
            render(layoutSettings);            
        });
        
        // Render Layout
         render(layoutSettings);
    };
    
    /* Get Version */
    $.layoutEngine.Version = function(){        
        return "1.0.0";        
    };
    
    /* DOCK enums */
    $.layoutEngine.DOCK = 
    {
        TOP     : 0,
        LEFT    : 1,
        FILL    : 2,
        RIGHT   : 3,
        BOTTOM  : 4,
        NONE    : 5        
    };
    
    /* Get Top Margin */
    getTopMargin = function(item){        
        return item.MarginTop ? item.MarginTop : (item.Margin ? item.Margin : 0);        
    };
    
    /* Get Left Margin */
    getLeftMargin = function(item){        
        return item.MarginLeft ? item.MarginLeft : (item.Margin ? item.Margin : 0);        
    };
    
    /* Get Bottom Margin */
    getBottomMargin = function(item){        
        return item.MarginBottom ? item.MarginBottom : (item.Margin ? item.Margin : 0);        
    };
    
    /* Get Right Margin */
    getRightMargin = function(item){        
        return item.MarginRight ? item.MarginRight : (item.Margin ? item.Margin : 0);        
    };
    
    /* Render layout */
    render = function(layoutSettings){
    
         // Reset the body margin and padding
        $(document.body).css({margin: 0, padding: 0});
        $(document.forms[0]).height($(window).height());
        
        var xDOC = $("#"+layoutSettings.EleID);
        var Top = 0 + getTopMargin(layoutSettings);
        var Left =  0 + getLeftMargin(layoutSettings);
        
        switch(layoutSettings.Dock)
        {
            
            case $.layoutEngine.DOCK.FILL :                
                xDOC.height($(window).height() - getBottomMargin(layoutSettings) - getTopMargin(layoutSettings));
                xDOC.width($(window).width() - getRightMargin(layoutSettings) - getLeftMargin(layoutSettings));
                xDOC.css({position: "absolute", top: Top, left: Left});                
            break;            
        }
        
        renderChildren(layoutSettings);
            
    };
    
    /* Render Children */
    renderChildren = function(currentObject){
        
        var currentContainer = $("#"+currentObject.EleID); 
        var containerWidth = currentContainer.innerWidth();
        var containerHeight = currentContainer.innerHeight();
        
        var Top = 0;
        var Left = 0;
        var Bottom = containerHeight;
        var Right = containerWidth;
        
        var topList = $.grep(currentObject.Children, function(item){
            return item.Dock == $.layoutEngine.DOCK.TOP ;
        });
        var leftList = $.grep(currentObject.Children, function(item){
            return item.Dock == $.layoutEngine.DOCK.LEFT ;
        });
        var fillList = $.grep(currentObject.Children, function(item){
            return item.Dock == $.layoutEngine.DOCK.FILL ;
        });
        var rightList = $.grep(currentObject.Children, function(item){
            return item.Dock == $.layoutEngine.DOCK.RIGHT ;
        });
        var bottomList = $.grep(currentObject.Children, function(item){
            return item.Dock == $.layoutEngine.DOCK.BOTTOM ;
        });
        
        $.each(topList, function(index,item){
            
            var currentItem = $("#"+item.EleID);
            
            var currentTop = Top + getTopMargin(item);
            var currentLeft = Left + getLeftMargin(item);
            var widthDiff = getLeftMargin(item) + getRightMargin(item);
            var heightDiff = getTopMargin(item) + getBottomMargin(item);
            
            currentItem.css({position: "absolute", top: currentTop, left: currentLeft});
            currentItem.height(item.Height);
            currentItem.width(containerWidth - widthDiff);            
            Top += (item.Height + heightDiff);
            
        });
        
        
        $.each(bottomList, function(index,item){
            
            var currentItem = $("#"+item.EleID);
           
            var currentLeft = Left + getLeftMargin(item);
            var widthDiff = getLeftMargin(item) + getRightMargin(item);
            var heightDiff = getTopMargin(item) + getBottomMargin(item);
            
            Bottom -= (item.Height + heightDiff);
            var currentBottom = Bottom + getTopMargin(item);
            
            currentItem.css({position: "absolute", top: currentBottom, left: currentLeft});
            currentItem.height(item.Height);
            currentItem.width(containerWidth - widthDiff);
            
        });
        
        $.each(leftList, function(index,item){
            
            var currentItem = $("#"+item.EleID);
            
            var currentTop = Top + getTopMargin(item);
            var currentLeft = Left + getLeftMargin(item);
            var widthDiff = getLeftMargin(item) + getRightMargin(item);
            var heightDiff = getTopMargin(item) + getBottomMargin(item);
            
            currentItem.css({position: "absolute", top: currentTop, left: currentLeft});
            currentItem.width(item.Width);
            currentItem.height(Bottom - Top - heightDiff);            
            Left += (item.Width + widthDiff);            
            
            if(item.Children && item.Children.length > 0)
            {
                renderChildren(item);
            }  
        });
        
        $.each(rightList, function(index,item){
            
            var currentItem = $("#"+item.EleID);
            
            var currentTop = Top + getTopMargin(item);            
            var widthDiff = getLeftMargin(item) + getRightMargin(item);
            var heightDiff = getTopMargin(item) + getBottomMargin(item);
            
            Right -= (item.Width + widthDiff);
            var currentRight = Right + getLeftMargin(item);
            
            currentItem.css({position: "absolute", top: currentTop, left: currentRight});
            currentItem.width(item.Width);
            currentItem.height(Bottom - Top - heightDiff); 
            
            if(item.Children && item.Children.length > 0)
            {
                renderChildren(item);
            }          
                
        });
        
        $.each(fillList, function(index,item){
            
            var currentItem = $("#"+item.EleID);
            
            var currentTop = Top + getTopMargin(item);
            var currentLeft = Left + getLeftMargin(item);
            var widthDiff = getLeftMargin(item) + getRightMargin(item);
            var heightDiff = getTopMargin(item) + getBottomMargin(item);
            
            currentItem.css({position: "absolute", top: currentTop, left: currentLeft});
            currentItem.width(Right - Left - widthDiff);
            currentItem.height(Bottom - Top - heightDiff);
            
            if(item.Children && item.Children.length > 0)
            {
                renderChildren(item);
            }             
        });
        
        
        
    };

})(jQuery);