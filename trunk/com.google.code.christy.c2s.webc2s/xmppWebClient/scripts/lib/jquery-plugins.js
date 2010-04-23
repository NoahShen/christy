/******************************************************************************
 * jquery.i18n.properties
 * 
 * Dual licensed under the GPL (http://dev.jquery.com/browser/trunk/jquery/GPL-LICENSE.txt) and 
 * MIT (http://dev.jquery.com/browser/trunk/jquery/MIT-LICENSE.txt) licenses.
 * 
 * @version     1.0.x
 * @author      Nuno Fernandes
 * @url         www.codingwithcoffee.com
 * @inspiration Localisation assistance for jQuery (http://keith-wood.name/localisation.html)
 *              by Keith Wood (kbwood{at}iinet.com.au) June 2007
 * 
 *****************************************************************************/

(function($) {
$.i18n = {};

/** Map holding bundle keys (if mode: 'map') */
$.i18n.map = {};

/**
 * Load and parse message bundle files (.properties),
 * making bundles keys available as javascript variables.
 * 
 * i18n files are named <name>.js, or <name>_<language>.js or <name>_<language>_<country>.js
 * Where:
 *      The <language> argument is a valid ISO Language Code. These codes are the lower-case, 
 *      two-letter codes as defined by ISO-639. You can find a full list of these codes at a 
 *      number of sites, such as: http://www.loc.gov/standards/iso639-2/englangn.html
 *      The <country> argument is a valid ISO Country Code. These codes are the upper-case,
 *      two-letter codes as defined by ISO-3166. You can find a full list of these codes at a
 *      number of sites, such as: http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html
 * 
 * Sample usage for a bundles/Messages.properties bundle:
 * $.i18n.properties({
 *      name:      'Messages', 
 *      language:  'en_US',
 *      path:      'bundles'
 * });
 * @param  name			(string/string[], required) names of file to load (eg, 'Messages' or ['Msg1','Msg2'])
 * @param  language		(string, optional) language/country code (eg, 'en', 'en_US', 'pt_PT'). if not specified, language reported by the browser will be used instead.
 * @param  path			(string, optional) path of directory that contains file to load
 * @param  mode			(string, optional) whether bundles keys are available as JavaScript variables/functions or as a map (eg, 'vars' or 'map')
 * @param  callback     (function, optional) callback function to be called after script is terminated
 */
$.i18n.properties = function(settings) {
	// set up settings
    var defaults = {
        name:           'Messages',
        language:       '',
        path:           '',  
        mode:           'vars',       
        callback:       function(){}
    };
    settings = $.extend(defaults, settings);    
    if(settings.language === null || settings.language == '') {
	   settings.language = $.i18n.browserLang();//normaliseLanguageCode(navigator.language /* Mozilla */ || navigator.userLanguage /* IE */);
	}
	if(settings.language === null) {settings.language='';}
	
	// load and parse bundle files
	var files = getFiles(settings.name);
	for(i=0; i<files.length; i++) {
		// 1. load base (eg, Messages.properties)

/* comment by Noah
		loadAndParseFile(settings.path + files[i] + '.properties', settings.language, settings.mode);
  
        
        // 2. with language code (eg, Messages_pt.properties)
		if(settings.language.length >= 2) {
            loadAndParseFile(settings.path + files[i] + '_' + settings.language.substring(0, 2) +'.properties', settings.language, settings.mode);
		}
		// 3. with language code and country code (eg, Messages_pt_PT.properties)
        if(settings.language.length >= 5) {
            loadAndParseFile(settings.path + files[i] + '_' + settings.language.substring(0, 5) +'.properties', settings.language, settings.mode);
        }
*/   	

        loadAndParseFile(settings.path + files[i] + '_' + settings.language +'.properties', settings.language, settings.mode);

	}
	
	// call callback
	if(settings.callback){ settings.callback(); }
};


/**
 * When configured with mode: 'map', allows access to bundle values by specifying its key.
 * Eg, jQuery.i18n.prop('com.company.bundles.menu_add')
 */
$.i18n.prop = function(key, placeHolderValues, defaultValue) {
	var value = $.i18n.map[key];
	
	// add defaultValue by Noah
	if(value == null) { value = defaultValue;}
	
	if(value == null) { return '[' + key + ']'; }
	if(!placeHolderValues) {
    //if(key == 'spv.lbl.modified') {alert(value);}
		return value;
	}else{
		for(var i=0; i<placeHolderValues.length; i++) {
			var regexp = new RegExp('\\{('+i+')\\}', "g");
			value = value.replace(regexp, placeHolderValues[i]);
		}
		return value;
	}
};

/** Language reported by browser, normalized code */
$.i18n.browserLang = function() {
	return normaliseLanguageCode(navigator.language /* Mozilla */ || navigator.userLanguage /* IE */);
}


/** Load and parse .properties files */
function loadAndParseFile(filename, language, mode) {
    $.ajax({
        url:        filename,
        async:      false,
        contentType: "text/plain;charset=UTF-8",
        dataType:   'text',
        success:    function(data, status) {
                       var parsed = '';
                       var parameters = data.split( /\n/ );
                       var regPlaceHolder = /(\{\d+\})/g;
                       var regRepPlaceHolder = /\{(\d+)\}/g;
                       var unicodeRE = /(\\u.{4})/ig;
                       for(var i=0; i<parameters.length; i++ ) {
                           parameters[i] = parameters[i].replace( /^\s\s*/, '' ).replace( /\s\s*$/, '' ); // trim
                           if(parameters[i].length > 0 && parameters[i].match("^#")!="#") { // skip comments
                               var pair = parameters[i].split('=');
                               if(pair.length > 0) {
                                   /** Process key & value */
                                   var name = unescape(pair[0]).replace( /^\s\s*/, '' ).replace( /\s\s*$/, '' ); // trim
                                   var value = pair.length == 1 ? "" : pair[1];
                                   for(var s=2;s<pair.length;s++){ value +='=' + pair[s]; }
                                   value = value.replace( /"/g, '\\"' ); // escape quotation mark (")
                                   value = value.replace( /^\s\s*/, '' ).replace( /\s\s*$/, '' ); // trim  
                                   
                                   /** Mode: bundle keys in a map */
                                   if(mode == 'map' || mode == 'both') {
                                       // handle unicode chars possibly left out
                                       var unicodeMatches = value.match(unicodeRE);
                                       if(unicodeMatches) {
                                         for(var u=0; u<unicodeMatches.length; u++) {
                                            value = value.replace( unicodeMatches[u], unescapeUnicode(unicodeMatches[u]));
                                         }
                                       }
                                       // add to map
                                       $.i18n.map[name] = value;
                                   }
                                   
                                   /** Mode: bundle keys as vars/functions */
                                   if(mode == 'vars' || mode == 'both') {
                                       // make sure namespaced key exists (eg, 'some.key') 
                                       checkKeyNamespace(name);
                                       
                                       // value with variable substitutions
                                       if(regPlaceHolder.test(value)) {
                                           var parts = value.split(regPlaceHolder);
                                           // process function args
                                           var first = true;
                                           var fnArgs = '';
                                           var usedArgs = [];
                                           for(var p=0; p<parts.length; p++) {
                                               if(regPlaceHolder.test(parts[p]) && usedArgs.indexOf(parts[p]) == -1) {
                                                   if(!first) {fnArgs += ',';}
                                                   fnArgs += parts[p].replace(regRepPlaceHolder, 'v$1');
                                                   usedArgs.push(parts[p]);
                                                   first = false;
                                               }
                                           }
                                           parsed += name + '=function(' + fnArgs + '){';
                                           // process function body
                                           var fnExpr = '"' + value.replace(regRepPlaceHolder, '"+v$1+"') + '"';
                                           parsed += 'return ' + fnExpr + ';' + '};';
                                           
                                       // simple value
                                       }else{
                                           parsed += name+'="'+value+'";';
                                       }
                                   }
                               }
                           }
                       }
                       eval(parsed);
                   }
    });
}

/** Make sure namespace exists (for keys with dots in name) */
function checkKeyNamespace(key) {
	var regDot = /\./;
	if(regDot.test(key)) {
		var fullname = '';
		var names = key.split( /\./ );
		for(var i=0; i<names.length; i++) {
			if(i>0) {fullname += '.';}
			fullname += names[i];
			if(eval('typeof '+fullname+' == "undefined"')) {
				eval(fullname + '={};');
			}
		}
	}
}

/** Make sure filename is an array */
function getFiles(names) {
	return (names && names.constructor == Array) ? names : [names];
}

/** Ensure language code is in the format aa_AA. */
function normaliseLanguageCode(lang) {
    lang = lang.toLowerCase();
    if(lang.length > 3) {
        lang = lang.substring(0, 3) + lang.substring(3).toUpperCase();
    }
    return lang;
}

/** Unescape unicode chars ('\u00e3') */
function unescapeUnicode(str) {
  // unescape unicode codes
  var codes = [];
  var code = parseInt(str.substr(2), 16);
  if (code >= 0 && code < Math.pow(2, 16)) {
     codes.push(code);
  }
  // convert codes to text
  var unescaped = '';
  for (var i = 0; i < codes.length; ++i) {
    unescaped += String.fromCharCode(codes[i]);
  }
  return unescaped;
}

})(jQuery);






/*!*
 * @filename include.jquery.js
 * @name jQuery Include File
 * @type jQuery
 * @projectDescription Include a file (css and js) in a head of the document and execute
 * @date 08/07/2008
 * @version 1.0
 * @cat Ajax
 * @require
 * @author Alex
 * @param required none url String|Array The address of the plugin that will be inserted.
 * You can pass a indexed array of url
 * @param optional none callback Function The function to be executed after the file has loaded
 * @example
 * $.include('/foo/test/file.js');
 * @desc load the current script
 * @example
 * var files = ['test.js','another.js','onemore.js'];
 * $.include(files,function(){
 * 		//execute some code after all scripts are completed
 * });
 * @desc load all the script inside the array
 * @return false | Element (object)
 */

(function($) {

	$.extend({
		// TODO added by Noah
		defaultTag: 'script',
		// You can change the base path to be applied in all imports
		ImportBasePath: '',
		// Associative array storing wating tasks and their callback
		__WaitingTasks: new Object(),
		// Called when a single file is loaded successfully - update and check WaitingTasks to see if it's ok to load callback
		__loadedSuccessfully: function(taskId){
			if (taskId in $.__WaitingTasks){
				if (($.__WaitingTasks[taskId].loading -= 1) < 1){
					var callback = $.__WaitingTasks[taskId].task;
					if (typeof callback == 'function') {
						callback();
					}
					delete $.__WaitingTasks[taskId];
				}
			}
		},
		//pass a file name and return a array with file name and extension
		fileinfo:	function(data){
            data = data.replace(/^\s|\s$/g, "");
			var m;
            if (/\.\w+$/.test(data)) {
                m = data.match(/([^\/\\]+)\.(\w+)$/);
                if (m) {
					if (m[2] == 'js') {
						return {
							filename: m[1],
							ext: m[2],
							tag: 'script'
						};
					}
					else 
						if (m[2] == 'css') {
							return {
								filename: m[1],
								ext: m[2],
								tag: 'link'
							};
						}
						else {
							return {
								filename: m[1],
								ext: m[2],
								tag: null
							};
						}
				}
				else {
					return {
						filename: null,
						ext: null
					};
				}
            } else {
                m = data.match(/([^\/\\]+)$/);
                if (m) {
                	// TODO changed by Noah
                	return {
						filename: m[1],
						ext: null,
						tag: defaultTag
					};
					
//					return {
//						filename: m[1],
//						ext: null,
//						tag: null
//					};
				}
				else {
					return {
						filename: null,
						ext: null,
						tag: null
					};
				}	
            }
        },
		//Check if the file that is been included already exist and return a Boolean value
		fileExist: function(filename,filetype,attrCheck) {
			var elementsArray = document.getElementsByTagName(filetype);
			for(var i=0;i<elementsArray.length;i++) {
				if(elementsArray[i].getAttribute(attrCheck)==$.ImportBasePath+filename) {
					return true;
				}
			}
			return false;
		},
		//Create the element depending of the file type and return the element (Object)
		createElement: function(filename,filetype) {
			switch(filetype) {
				case 'script' :
				if (!$.fileExist(filename, filetype, 'src')) {
					var scriptTag = document.createElement(filetype);
					scriptTag.setAttribute('language', 'javascript');
					scriptTag.setAttribute('type', 'text/javascript');
					scriptTag.setAttribute('src', $.ImportBasePath + filename);
					return scriptTag;
				} else {
					return false;
				}
				break;
				case 'link' :
				if (!$.fileExist(filename, filetype, 'href')) {
					var styleTag = document.createElement(filetype);
					styleTag.setAttribute('type', 'text/css');
					styleTag.setAttribute('rel', 'stylesheet');
					styleTag.setAttribute('href', $.ImportBasePath + filename);
					return styleTag;
				} else {
					return false;
				}
				break;

				default :
					return false;
				break;
			}
		},
		cssReady: function(index, taskId) {
			function check() {
				if(document.styleSheets[index]){
					window.clearInterval(checkInterval);
					$.__loadedSuccessfully(taskId);
				}
			}
			var checkInterval = window.setInterval(check,200);
		},
		//The main function to insert the file
		include: function(file,callback) {
			var headerTag = document.getElementsByTagName('head')[0];
			var fileArray = [];
			//if file is string, give a single index element
			typeof file=='string' ? fileArray[0] = file : fileArray = file;
			// Create a unique id using the current time
			var taskId = new Date().getTime().toString();
			$.__WaitingTasks[taskId] = {'loading': fileArray.length, 'task': callback};
			//go through all the files
			for (var i = 0; i < fileArray.length; i++) {
				var elementTag = $.fileinfo(fileArray[i]).tag;
				var el = [];
				if (elementTag !== null) {
					el[i] = $.createElement(fileArray[i], elementTag);
					if (el[i]) {
						headerTag.appendChild(el[i]);
						if ($.browser.msie) {
							el[i].onreadystatechange = function(){
								if (this.readyState === 'loaded' || this.readyState === 'complete') {
									$.__loadedSuccessfully(taskId);
								}
							};
						}
						else {
							if (elementTag == 'link') {
								$.cssReady(i, taskId);
							}
							else {
                                if (/WebKit/i.test(navigator.userAgent)) {
                                    var _timer = setInterval(function(){
                                        if (/loaded|complete/.test(document.readyState)) {
                                            $.__loadedSuccessfully(taskId); // call of the call
                                        }
                                    }, 100);
                                }
								el[i].onload = function(){
									$.__loadedSuccessfully(taskId);
								};
							}
						}
					}else{
						$.__loadedSuccessfully(taskId);
					}
				} else {
					return false;
				}
			}
		}
	});

})(jQuery);




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