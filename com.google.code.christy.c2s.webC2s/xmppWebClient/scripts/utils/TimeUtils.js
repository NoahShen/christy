TimeUtils = jClass.extend({
	init: function() {
	}
});

TimeUtils.currentTimeMillis = function() {
	var d = new Date();
	return d.getTime();
}