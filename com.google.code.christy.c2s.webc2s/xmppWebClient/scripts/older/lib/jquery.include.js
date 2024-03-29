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