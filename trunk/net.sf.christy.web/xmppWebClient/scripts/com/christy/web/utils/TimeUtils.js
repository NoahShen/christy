jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass"
	],
	name: "com.christy.web.utils.TimeUtils",
	as: function() {
		var JClass = com.christy.web.clazz.JClass;
		
		com.christy.web.utils.TimeUtils = JClass.extend({
			init: function() {
			}
		});
		
		com.christy.web.utils.TimeUtils.currentTimeMillis = function() {
			var d = new Date();
			return d.getTime();
		}
	}
});