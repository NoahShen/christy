jingo.declare({
	require: [
	  "com.christy.web.clazz.JClass"
	],
	name: "com.christy.web.utils.StringUtils",
	as: function() {
		
		com.christy.web.utils.StringUtils = com.christy.web.clazz.JClass.extend({
			init: function(){
				
			}
		});
		
		com.christy.web.utils.StringUtils.hash = function(data, algorithm) {
			if (algorithm.toLowerCase() == "md5"
				|| algorithm.toLowerCase() == "md-5"){
				return MD5(data);
			}else if (algorithm.toLowerCase() == "sha1"
				|| algorithm.toLowerCase() == "sha-1"){
				return SHA1(data);
			}
		};
		
		
		
		
		hex_chr = "0123456789abcdef";
		
		function rhex(num) { 
			str = ""; 
			for(j = 0; j <= 3; j++) 
			str += hex_chr.charAt((num >> (j * 8 + 4)) & 0x0F) + 
			hex_chr.charAt((num >> (j * 8)) & 0x0F); 
			return str; 
		}
		
		function str2blks_MD5(str) { 
			nblk = ((str.length + 8) >> 6) + 1; 
			blks = new Array(nblk * 16); 
			for(i = 0; i < nblk * 16; i++) blks[i] = 0; 
			for(i = 0; i < str.length; i++) 
			blks[i >> 2] |= str.charCodeAt(i) << ((i % 4) * 8); 
			blks[i >> 2] |= 0x80 << ((i % 4) * 8); 
			blks[nblk * 16 - 2] = str.length * 8; 
			return blks; 
		}
		
		function add(x, y) { 
			var lsw = (x & 0xFFFF) + (y & 0xFFFF); 
			var msw = (x >> 16) + (y >> 16) + (lsw >> 16); 
			return (msw << 16) | (lsw & 0xFFFF); 
		}
		
		function rol(num, cnt) { 
			return (num << cnt) | (num >>> (32 - cnt)); 
		}
		
		function cmn(q, a, b, x, s, t) { 
			return add(rol(add(add(a, q), add(x, t)), s), b); 
		}
		
		function ff(a, b, c, d, x, s, t) { 
			return cmn((b & c) | ((~b) & d), a, b, x, s, t); 
		}
		
		function gg(a, b, c, d, x, s, t) { 
			return cmn((b & d) | (c & (~d)), a, b, x, s, t); 
		} 
		
		function hh(a, b, c, d, x, s, t) { 
			return cmn(b ^ c ^ d, a, b, x, s, t); 
		} 
		
		function ii(a, b, c, d, x, s, t) { 
			return cmn(c ^ (b | (~d)), a, b, x, s, t); 
		} 
		
		function MD5(str) { 
			x = str2blks_MD5(str); 
			var a = 1732584193; 
			var b = -271733879; 
			var c = -1732584194; 
			var d = 271733878; 
			for(i = 0; i < x.length; i += 16) 
			{ 
				var olda = a; 
				var oldb = b; 
				var oldc = c; 
				var oldd = d; 
				a = ff(a, b, c, d, x[i+ 0], 7 , -680876936); 
				d = ff(d, a, b, c, x[i+ 1], 12, -389564586); 
				c = ff(c, d, a, b, x[i+ 2], 17, 606105819); 
				b = ff(b, c, d, a, x[i+ 3], 22, -1044525330); 
				a = ff(a, b, c, d, x[i+ 4], 7 , -176418897); 
				d = ff(d, a, b, c, x[i+ 5], 12, 1200080426); 
				c = ff(c, d, a, b, x[i+ 6], 17, -1473231341); 
				b = ff(b, c, d, a, x[i+ 7], 22, -45705983); 
				a = ff(a, b, c, d, x[i+ 8], 7 , 1770035416); 
				d = ff(d, a, b, c, x[i+ 9], 12, -1958414417); 
				c = ff(c, d, a, b, x[i+10], 17, -42063); 
				b = ff(b, c, d, a, x[i+11], 22, -1990404162); 
				a = ff(a, b, c, d, x[i+12], 7 , 1804603682); 
				d = ff(d, a, b, c, x[i+13], 12, -40341101); 
				c = ff(c, d, a, b, x[i+14], 17, -1502002290); 
				b = ff(b, c, d, a, x[i+15], 22, 1236535329); 
				a = gg(a, b, c, d, x[i+ 1], 5 , -165796510); 
				d = gg(d, a, b, c, x[i+ 6], 9 , -1069501632); 
				c = gg(c, d, a, b, x[i+11], 14, 643717713); 
				b = gg(b, c, d, a, x[i+ 0], 20, -373897302); 
				a = gg(a, b, c, d, x[i+ 5], 5 , -701558691); 
				d = gg(d, a, b, c, x[i+10], 9 , 38016083); 
				c = gg(c, d, a, b, x[i+15], 14, -660478335); 
				b = gg(b, c, d, a, x[i+ 4], 20, -405537848); 
				a = gg(a, b, c, d, x[i+ 9], 5 , 568446438); 
				d = gg(d, a, b, c, x[i+14], 9 , -1019803690); 
				c = gg(c, d, a, b, x[i+ 3], 14, -187363961); 
				b = gg(b, c, d, a, x[i+ 8], 20, 1163531501); 
				a = gg(a, b, c, d, x[i+13], 5 , -1444681467); 
				d = gg(d, a, b, c, x[i+ 2], 9 , -51403784); 
				c = gg(c, d, a, b, x[i+ 7], 14, 1735328473); 
				b = gg(b, c, d, a, x[i+12], 20, -1926607734); 
				a = hh(a, b, c, d, x[i+ 5], 4 , -378558); 
				d = hh(d, a, b, c, x[i+ 8], 11, -2022574463); 
				c = hh(c, d, a, b, x[i+11], 16, 1839030562); 
				b = hh(b, c, d, a, x[i+14], 23, -35309556); 
				a = hh(a, b, c, d, x[i+ 1], 4 , -1530992060); 
				d = hh(d, a, b, c, x[i+ 4], 11, 1272893353); 
				c = hh(c, d, a, b, x[i+ 7], 16, -155497632); 
				b = hh(b, c, d, a, x[i+10], 23, -1094730640); 
				a = hh(a, b, c, d, x[i+13], 4 , 681279174); 
				d = hh(d, a, b, c, x[i+ 0], 11, -358537222); 
				c = hh(c, d, a, b, x[i+ 3], 16, -722521979); 
				b = hh(b, c, d, a, x[i+ 6], 23, 76029189); 
				a = hh(a, b, c, d, x[i+ 9], 4 , -640364487); 
				d = hh(d, a, b, c, x[i+12], 11, -421815835); 
				c = hh(c, d, a, b, x[i+15], 16, 530742520); 
				b = hh(b, c, d, a, x[i+ 2], 23, -995338651); 
				a = ii(a, b, c, d, x[i+ 0], 6 , -198630844); 
				d = ii(d, a, b, c, x[i+ 7], 10, 1126891415); 
				c = ii(c, d, a, b, x[i+14], 15, -1416354905); 
				b = ii(b, c, d, a, x[i+ 5], 21, -57434055); 
				a = ii(a, b, c, d, x[i+12], 6 , 1700485571); 
				d = ii(d, a, b, c, x[i+ 3], 10, -1894986606); 
				c = ii(c, d, a, b, x[i+10], 15, -1051523); 
				b = ii(b, c, d, a, x[i+ 1], 21, -2054922799); 
				a = ii(a, b, c, d, x[i+ 8], 6 , 1873313359); 
				d = ii(d, a, b, c, x[i+15], 10, -30611744); 
				c = ii(c, d, a, b, x[i+ 6], 15, -1560198380); 
				b = ii(b, c, d, a, x[i+13], 21, 1309151649); 
				a = ii(a, b, c, d, x[i+ 4], 6 , -145523070); 
				d = ii(d, a, b, c, x[i+11], 10, -1120210379); 
				c = ii(c, d, a, b, x[i+ 2], 15, 718787259); 
				b = ii(b, c, d, a, x[i+ 9], 21, -343485551); 
				a = add(a, olda); 
				b = add(b, oldb); 
				c = add(c, oldc); 
				d = add(d, oldd); 
			} 
			return rhex(a) + rhex(b) + rhex(c) + rhex(d); 
		}
		
		
		
		/**
		*
		*  Secure Hash Algorithm (SHA1)
		*  http://www.webtoolkit.info/
		*
		**/
		 
		function SHA1 (msg) {
		 
			function rotate_left(n,s) {
				var t4 = ( n<<s ) | (n>>>(32-s));
				return t4;
			};
		 
			function lsb_hex(val) {
				var str="";
				var i;
				var vh;
				var vl;
		 
				for( i=0; i<=6; i+=2 ) {
					vh = (val>>>(i*4+4))&0x0f;
					vl = (val>>>(i*4))&0x0f;
					str += vh.toString(16) + vl.toString(16);
				}
				return str;
			};
		 
			function cvt_hex(val) {
				var str="";
				var i;
				var v;
		 
				for( i=7; i>=0; i-- ) {
					v = (val>>>(i*4))&0x0f;
					str += v.toString(16);
				}
				return str;
			};
		 
		 
			function Utf8Encode(string) {
				string = string.replace(/\r\n/g,"\n");
				var utftext = "";
		 
				for (var n = 0; n < string.length; n++) {
		 
					var c = string.charCodeAt(n);
		 
					if (c < 128) {
						utftext += String.fromCharCode(c);
					}
					else if((c > 127) && (c < 2048)) {
						utftext += String.fromCharCode((c >> 6) | 192);
						utftext += String.fromCharCode((c & 63) | 128);
					}
					else {
						utftext += String.fromCharCode((c >> 12) | 224);
						utftext += String.fromCharCode(((c >> 6) & 63) | 128);
						utftext += String.fromCharCode((c & 63) | 128);
					}
		 
				}
		 
				return utftext;
			};
		 
			var blockstart;
			var i, j;
			var W = new Array(80);
			var H0 = 0x67452301;
			var H1 = 0xEFCDAB89;
			var H2 = 0x98BADCFE;
			var H3 = 0x10325476;
			var H4 = 0xC3D2E1F0;
			var A, B, C, D, E;
			var temp;
		 
			msg = Utf8Encode(msg);
		 
			var msg_len = msg.length;
		 
			var word_array = new Array();
			for( i=0; i<msg_len-3; i+=4 ) {
				j = msg.charCodeAt(i)<<24 | msg.charCodeAt(i+1)<<16 |
				msg.charCodeAt(i+2)<<8 | msg.charCodeAt(i+3);
				word_array.push( j );
			}
		 
			switch( msg_len % 4 ) {
				case 0:
					i = 0x080000000;
				break;
				case 1:
					i = msg.charCodeAt(msg_len-1)<<24 | 0x0800000;
				break;
		 
				case 2:
					i = msg.charCodeAt(msg_len-2)<<24 | msg.charCodeAt(msg_len-1)<<16 | 0x08000;
				break;
		 
				case 3:
					i = msg.charCodeAt(msg_len-3)<<24 | msg.charCodeAt(msg_len-2)<<16 | msg.charCodeAt(msg_len-1)<<8	| 0x80;
				break;
			}
		 
			word_array.push( i );
		 
			while( (word_array.length % 16) != 14 ) word_array.push( 0 );
		 
			word_array.push( msg_len>>>29 );
			word_array.push( (msg_len<<3)&0x0ffffffff );
		 
		 
			for ( blockstart=0; blockstart<word_array.length; blockstart+=16 ) {
		 
				for( i=0; i<16; i++ ) W[i] = word_array[blockstart+i];
				for( i=16; i<=79; i++ ) W[i] = rotate_left(W[i-3] ^ W[i-8] ^ W[i-14] ^ W[i-16], 1);
		 
				A = H0;
				B = H1;
				C = H2;
				D = H3;
				E = H4;
		 
				for( i= 0; i<=19; i++ ) {
					temp = (rotate_left(A,5) + ((B&C) | (~B&D)) + E + W[i] + 0x5A827999) & 0x0ffffffff;
					E = D;
					D = C;
					C = rotate_left(B,30);
					B = A;
					A = temp;
				}
		 
				for( i=20; i<=39; i++ ) {
					temp = (rotate_left(A,5) + (B ^ C ^ D) + E + W[i] + 0x6ED9EBA1) & 0x0ffffffff;
					E = D;
					D = C;
					C = rotate_left(B,30);
					B = A;
					A = temp;
				}
		 
				for( i=40; i<=59; i++ ) {
					temp = (rotate_left(A,5) + ((B&C) | (B&D) | (C&D)) + E + W[i] + 0x8F1BBCDC) & 0x0ffffffff;
					E = D;
					D = C;
					C = rotate_left(B,30);
					B = A;
					A = temp;
				}
		 
				for( i=60; i<=79; i++ ) {
					temp = (rotate_left(A,5) + (B ^ C ^ D) + E + W[i] + 0xCA62C1D6) & 0x0ffffffff;
					E = D;
					D = C;
					C = rotate_left(B,30);
					B = A;
					A = temp;
				}
		 
				H0 = (H0 + A) & 0x0ffffffff;
				H1 = (H1 + B) & 0x0ffffffff;
				H2 = (H2 + C) & 0x0ffffffff;
				H3 = (H3 + D) & 0x0ffffffff;
				H4 = (H4 + E) & 0x0ffffffff;
		 
			}
		 
			var temp = cvt_hex(H0) + cvt_hex(H1) + cvt_hex(H2) + cvt_hex(H3) + cvt_hex(H4);
		 
			return temp.toLowerCase();
		 
		}
		
		numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		
		
		com.christy.web.utils.StringUtils.randomString = function(length){
			if (length < 1){
				return null;
			}
			
			var str = "";
			for (var i = 0; i < length; ++i){
				var index = Math.floor(Math.random() * numbersAndLetters.length);
				str += numbersAndLetters.charAt(index);
			}
			return str;
		};
		
		com.christy.web.utils.StringUtils.escapeXml = function(xml) {
			
			
			var xml = xml.replace(/\&/g,'&amp;')
						.replace(/</g,'&lt;')
						.replace(/>/g, '&gt;')
						.replace(/\'/g, '&apos;')
						.replace(/\"/g, '&quot;');
		
			return xml;
		}
		
		com.christy.web.utils.StringUtils.unescapeXml = function(xml) {
		
			var xml = xml.replace(/\&amp;/g,'&')
						.replace(/\&lt;/g,'<')
						.replace(/\&gt;/g, '>')
						.replace(/\'/g, '\'')
						.replace(/\&quot;/g, '\"');
		
			return xml;
		}
		
		com.christy.web.utils.StringUtils.randomNumber = function(start, end) {
			var r = Math.random() * (start - end ) + end;
			return parseInt(r,10);
		}


		com.christy.web.utils.StringUtils.createXml = function(str) {
			if(document.all){
				var xmlDom=new ActiveXObject("Microsoft.XMLDOM");
				xmlDom.loadXML(str);
				return xmlDom;
			} else {
				return new DOMParser().parseFromString(str, "text/xml");
			}
		　　	
		}
		
		base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		base64DecodeChars = new Array(
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
		    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
		    -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
		    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
		    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
		    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1);
    
		com.christy.web.utils.StringUtils.utf16to8 = function(str) {
			 var out, i, len, c;

		    out = "";
		    len = str.length;
		    for(i = 0; i < len; i++) {
			    c = str.charCodeAt(i);
			    if ((c >= 0x0001) && (c <= 0x007F)) {
			        out += str.charAt(i);
			    } else if (c > 0x07FF) {
			        out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
			        out += String.fromCharCode(0x80 | ((c >>  6) & 0x3F));
			        out += String.fromCharCode(0x80 | ((c >>  0) & 0x3F));
			    } else {
			        out += String.fromCharCode(0xC0 | ((c >>  6) & 0x1F));
			        out += String.fromCharCode(0x80 | ((c >>  0) & 0x3F));
			    }
		    }
		    return out;
		}
		com.christy.web.utils.StringUtils.utf8to16 = function(str) {
			var out, i, len, c;
		    var char2, char3;
		
		    out = "";
		    len = str.length;
		    i = 0;
		    while(i < len) {
			    c = str.charCodeAt(i++);
			    switch(c >> 4)
			    {
			      case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
			        // 0xxxxxxx
			        out += str.charAt(i-1);
			        break;
			      case 12: case 13:
			        // 110x xxxx   10xx xxxx
			        char2 = str.charCodeAt(i++);
			        out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
			        break;
			      case 14:
			        // 1110 xxxx  10xx xxxx  10xx xxxx
			        char2 = str.charCodeAt(i++);
			        char3 = str.charCodeAt(i++);
			        out += String.fromCharCode(((c & 0x0F) << 12) |
			                       ((char2 & 0x3F) << 6) |
			                       ((char3 & 0x3F) << 0));
			        break;
			    }
		    }
		
		    return out;
		}
		
		com.christy.web.utils.StringUtils.base64encode = function(str) {
			var out, i, len;
		    var c1, c2, c3;
		
		    len = str.length;
		    i = 0;
		    out = "";
		    while(i < len) {
			    c1 = str.charCodeAt(i++) & 0xff;
			    if(i == len) {
			        out += base64EncodeChars.charAt(c1 >> 2);
			        out += base64EncodeChars.charAt((c1 & 0x3) << 4);
			        out += "==";
			        break;
			    }
			    c2 = str.charCodeAt(i++);
			    if(i == len) {
			        out += base64EncodeChars.charAt(c1 >> 2);
			        out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
			        out += base64EncodeChars.charAt((c2 & 0xF) << 2);
			        out += "=";
			        break;
			    }
			    c3 = str.charCodeAt(i++);
			    out += base64EncodeChars.charAt(c1 >> 2);
			    out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
			    out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >>6));
			    out += base64EncodeChars.charAt(c3 & 0x3F);
		    }
		    return out;
		}
		
		com.christy.web.utils.StringUtils.base64decode = function(str) {
			var c1, c2, c3, c4;
		    var i, len, out;
		
		    len = str.length;
		    i = 0;
		    out = "";
		    while(i < len) {
			    /* c1 */
			    do {
			        c1 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
			    } while(i < len && c1 == -1);
			    if(c1 == -1)
			        break;
			
			    /* c2 */
			    do {
			        c2 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
			    } while(i < len && c2 == -1);
			    if(c2 == -1)
			        break;
			
			    out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));
			
			    /* c3 */
			    do {
			        c3 = str.charCodeAt(i++) & 0xff;
			        if(c3 == 61)
			        return out;
			        c3 = base64DecodeChars[c3];
			    } while(i < len && c3 == -1);
			    
			    if(c3 == -1)
			        break;
			
			    out += String.fromCharCode(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2));
			
			    /* c4 */
			    do {
			        c4 = str.charCodeAt(i++) & 0xff;
			        if(c4 == 61)
			        return out;
			        c4 = base64DecodeChars[c4];
			    } while(i < len && c4 == -1);
			    
			    if(c4 == -1)
			        break;
			        
			    out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
		    
		    }
		    return out;
		}
	}
});
