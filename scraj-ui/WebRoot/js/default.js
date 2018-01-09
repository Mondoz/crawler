$(function(){
	controlLeftBlock();
	setTimeout(controlRightBlock,200);

	$("#left-block").resizable({ handles: "e" ,start: function ( event ,ui ) {
		var min = parseInt(ui.element.css("min-width"));
		ui.element.resizable( "option", "minWidth", min );
	},
		resize: function( event, ui ) {
		$("#center-block").css("left", ui.size.width + "px");
		var $container = ui.element.find(".card-box-component");
		var w = $container.width();
		var min = parseInt($container.find("li").css("min-width"));
		var all = Math.floor(w / min);
		var pw = Math.floor(w / all);
		$container.find("li").css("width",pw+"px");
	}
	}).on("click",".tab-blue",function (event) {
		controlLeftBlock();
	});

	$("#right-block").on("mousedown",".box-controller",function (event) {
		if($(event.target).closest(".tab-blue").length){
			controlRightBlock();
		}else{
			var $item = $("#right-block");
			var x = parseInt($item.css("right"));
			var min = parseInt($item.css("min-width"));

			if(x == 0){
				var downPointer = event.pageX;
				var dowmSize =  $item.width();
				var $body = $("body");

				$body.on("mousemove",resizeWindowWH);

				$body.one("mouseup",function (event) {
					$("body").off("mousemove",resizeWindowWH);
				});

				function resizeWindowWH(event) {
					var move = event.pageX - downPointer;
					var upSize = dowmSize - move;
					if(upSize >= min) {
						$item.width(upSize);
						$("#center-block").css("right", upSize + "px");
					}
				}
			}
		}
	});

	// $(".card-box-component").resize(function(event){
	// 	var w = $(this).width();
	// 	var min = parseInt($(this).find("li").css("min-width"));
	// 	var all = Math.floor(w / min);
	// 	var pw = Math.floor(w / all);
	// 	$(this).find("li").css("width",pw+"px");
	// });
	//
	// $("#left-block").on("mousedown",".box-controller",function (event) {
	// 	if($(event.target).closest(".tab-blue").length){
	// 		controlLeftBlock();
	// 	}else{
	// 		var $item = $("#left-block");
	// 		var x = parseInt($item.css("left"));
	// 		var min = parseInt($item.css("min-width"));
    //
	// 		if(x == 0) {
	// 			var downPointer = event.pageX;
	// 			var dowmSize = $item.width();
	// 			var $body = $("body");
    //
	// 			$body.on("mousemove", resizeWindowWH);
    //
	// 			$body.one("mouseup", function (event) {
	// 				$("body").off("mousemove", resizeWindowWH);
	// 			});
    //
	// 			function resizeWindowWH(event) {
	// 				var move = event.pageX - downPointer;
	// 				var upSize = dowmSize + move;
	// 				if(upSize >= min){
	// 					$item.width(upSize);
	// 					$("#center-block").css("left", upSize + "px");
	// 				}
	// 			}
	// 		}
	// 	}
	// });

	$("#bottom-block").on("mousedown",".box-controller",function (event) {
		if($(event.target).closest(".tab-blue").length){
			controlBottomBlock();
		}else{
			var $item = $("#bottom-block");
			var x = parseInt($item.css("bottom"));
			if(x == 0) {
				$item.append("<div id='move-mask'></div>");

				var downPointer = event.pageY;
				var dowmSize = $item.height();
				var $body = $("body");

				$body.on("mousemove", resizeWindowWH);

				$body.one("mouseup", function (event) {
					$item.find("#move-mask").remove();
					$("body").off("mousemove", resizeWindowWH);
				});

				function resizeWindowWH(event) {
					var move = event.pageY - downPointer;
					var upSize = dowmSize - move;
					$item.height(upSize);
					$("#max-content").css("bottom", upSize + "px");
				}
			}
		}
	});

	$(".card-box").on("click",".box-title",function (event) {
		$(this).find("i.fa").toggleClass("fa-angle-down").toggleClass("fa-angle-up");
		$(this).next(".card-box-one").toggle();
	});
});

function controlRightBlock(){
	var aTime = 500;
	var $item = $('#right-block:visible');
	if($item.length){
		var w = $item.width();
		var $main = $('#center-block');
		var x = parseInt($item.css("right"));
		var pos = x == 0 ? -w : 0;
		var mx = Math.abs(x);

		var cursor = pos == 0 ? "ew-resize" : "auto";
		$item.find(".box-controller").css("cursor", cursor);

		// if(x == 0){
		// 	$item.find(".ui-resizable-handle").hide();
		// }else{
		// 	setTimeout(function () {
		// 		$item.find(".ui-resizable-handle").show();
		// 	},aTime * 2);
		// }

		$main.animate({right:mx},aTime);
		$item.animate({right:pos},aTime);
	}
}

function controlLeftBlock(){
	var aTime = 500;
	var $item = $("#left-block");
	var w = $item.width();
	var $main = $('#center-block');
	var x = parseInt($item.css("left"));
	var pos = x == 0 ? -w : 0;
	var mx = Math.abs(x);

	// var cursor = pos == 0 ? "ew-resize" : "auto";
	$item.find(".box-controller").css("cursor", "auto");

	if(x == 0){
		$item.find(".ui-resizable-handle").hide();
	}else{
		setTimeout(function () {
			$item.find(".ui-resizable-handle").show();
		},aTime * 2);
	}

	$main.animate({left:mx},aTime);
	$item.animate({left:pos},aTime);
}

function controlBottomBlock(){
	var aTime = 500;
	var $item = $("#bottom-block");
	var w = $item.height();
	var $main = $('#max-content');
	var x = parseInt($item.css("bottom"));
	var pos = x == 0 ? -w : 0;
	var mx = Math.abs(x);

	var cursor = pos == 0 ? "ns-resize" : "auto";
	$item.find(".box-controller").css("cursor", cursor);

	// if(x == 0){
	// 	$item.find(".ui-resizable-handle").hide();
	// }else{
	// 	setTimeout(function () {
	// 		$item.find(".ui-resizable-handle").show();
	// 	},aTime * 2);
	// }

	$main.animate({bottom:mx},aTime);
	$item.animate({bottom:pos},aTime);
}

/* ajax & kgloader start */
function checkLogin(){
	// var accessToken	= $.cookie("accessToken");
	// if(!accessToken && top.location.pathname.indexOf("/login.html") < 0){
	// 	top.location.href = "login.html";
	// }
}

function resetAimgEvent(){
	$("body").on("click","a",function(event){
		var href = $(this).attr("href");
		if(href){
			var tmp = href.split(".");
			if(tmp.length > 1){
				var postFix = tmp[tmp.length - 1];
				switch (postFix) {
					case "jpg":
					case "png":
					case "gif":
					case "ico":
						parent.addImgDialog("", href, null);
						event.preventDefault();
						break;
					default:
						break;
				}
			}
		}
	});
}

function dealSendData(sendData){
	var jsonSendData = {};
	if(sendData.indexOf('?') > -1 && sendData.indexOf('?') < sendData.indexOf('&')){
		var pre = sendData.substring(0,sendData.indexOf('?'));
		pre.split('&').forEach(function(param){
			param = param.split('=');
			jsonSendData[param[0]] = param[1];
		});
		var middle = sendData.substring(sendData.indexOf('?') + 1);
		if(sendData.indexOf('&params={')>0){
			middle = sendData.substring(sendData.indexOf('?') + 1,sendData.indexOf('&params={'));
			jsonSendData.params = sendData.substring(sendData.indexOf('&params={')+"&params=".length);
		}
		middle.split('&').forEach(function(param){
			param = param.split('=');
			jsonSendData[param[0]] = param[1];
		});
	}else{
		var pre = sendData.substring(0,sendData.indexOf('&params='));
		pre.split('&').forEach(function(param){
			param = param.split('=');
			jsonSendData[param[0]] = param[1];
		});
		sendData = sendData.substring(sendData.indexOf('&params=')+'&params='.length);
		jsonSendData.params = sendData;
	}
	if(jsonSendData.params){
		var jsonSendDataPost = JSON.parse(jsonSendData.params);
		for(var k in jsonSendDataPost){
			jsonSendData[k] = jsonSendDataPost[k];
		}
	}
	return jsonSendData;
}

function changeAjaxObj(that,bool){
	if(that){
		if(that.tagName == "INPUT" || that.tagName == "BUTTON"){
			$(that).prop("disabled",bool);
		}else if(that.tagName == "A"){
			if(bool){
				$(that).children().addClass("hide");
				$(that).append('<i class="fa fa-spinner fa-spin fa-fw ajax-loading"></i>');
			}else{
				$(that).find(".ajax-loading").remove();
				$(that).children().removeClass("hide");
			}
		}else{
			if(bool){
				var out = "";
				if(that.tagName == "UL"){
					out = $('<li class="ajax-loading"></li>');
					$(that).html(out.append('<div style="position: relative;padding:100px 0;"><div class="loading"><i class="fa fa-spinner fa-spin"></i></div></div>'));
				}else if(that.tagName == "BODY" || $(that).hasClass("sys-dialog-content")){
					$(that).append('<div class="ajax-loading"><div class="loading"><i class="fa fa-spinner fa-spin"></i></div></div>');
				}
			}else{
				$(that).find(".ajax-loading").remove();
			}
		}
	}
}

function kgLoader(options){
	var defaultOptions = {
		type: 0,
		url:"",
		params:"",
		success:$.noop,
		error:$.noop,
		complete:$.noop,
		headers:null,
		that:null,
		dataFilter:null
	};
	var option = $.extend({},defaultOptions,options);
	var type = option.type;
	var url = option.url;
	var params=option.params;
	var success=option.success;
	var error=option.error;
	var complete=option.complete;
	var headers=option.headers;
	var that=option.that;
	var dataFilter=option.dataFilter;

	changeAjaxObj(that,true);
	if(crossDomain){
		if(url.indexOf("&params={")>0){
			top.addAlertDialog("请把url中的params作为第一个参数后再次尝试!");
			return null;
		}else{
			return $.ajax({
				type: "POST",
				data: {
					type: type,
					url: url,
					params : typeof(params) == "object" ? JSON.stringify(params) : params
				},
				headers:headers,
				ifModified: true,
				crossDomain: true,
				dataType: "json",
				statusCode: {404: function() {top.addAlertDialog('404 service not found');}},
				url: LOADSERVICE_URL,
				cache: false,
				dataFilter :function(data, type){
					if(dataFilter){
						return dataFilter(data, type);
					}else{
						return data;
					}
				},
				success : function(data,state,XHR){
					data = dealJsonCallback(data);
					var sendData = decodeURIComponent(this.data);
					sendData = sendData.substring(sendData.indexOf("&") + 1);
					var jsonSendData = dealSendData(sendData);
					if(success){
						success(data,state,XHR,jsonSendData);
					}
				},
				error:function(XMLHttpRequest, textStatus, errorThrown){
					if(error){
						error(XMLHttpRequest, textStatus, errorThrown);
					}else if(textStatus == "parsererror"){
						// top.location.href = "login.html";
						error(XMLHttpRequest, textStatus, errorThrown);
					}
				},
				complete:function(XHR, TS){
					changeAjaxObj(that,false);
					if(complete){
						complete(XHR, TS);
					}
				}
			});
		}
	}else{
		if(url.indexOf("http://") != 0){
			//url = RESTFUL_SERVICE_KC_URL+'/' + url;
		}
		if(params){
			for(var k in params){
				if(typeof params[k] == "object"){
					params[k] = JSON.stringify(params[k]);
				}
			}
		}
		return $.ajax({
			type: type == 0 ? "GET" : "POST",
			data: params,
			headers:headers,
			ifModified: true,
			crossDomain: true,
			statusCode: {404: function() {top.addAlertDialog('404 service not found');}},
			url: url,
			cache: false,
			dataFilter :function(data, type){
				if(dataFilter){
					return dataFilter(data, type);
				}else{
					return data;
				}
			},
			success : function(data,state,XHR){
				data = dealJsonCallback(data);
				var sendData = decodeURIComponent(this.data);
				var jsonSendData = {};
				if(sendData){
					$.extend(jsonSendData,unParam(sendData));
				}
				sendData = "url="+this.url;
				if(sendData.indexOf('?') > -1 ){
					var pre = sendData.substring(0,sendData.indexOf('?'));
					sendData = sendData.substring(sendData.indexOf('?') + 1);
					$.extend(jsonSendData,unParam(pre),unParam(sendData));
				}
				if(success){
					success(data,state,XHR,jsonSendData);
				}
			},
			error:function(data){
				if(error){
					error(data);
				}
			},
			complete:function(data){
				changeAjaxObj(that,false);
				if(complete){
					complete(data);
				}
			}
		});
	}
}

function dealJsonCallback(data){
	if(typeof(data) == "string"){
		data = JSON.parse(data);
	}
	if(data.code){
		if(data.code == 200){
			if(!data.data && data.data != 0){
				return true;
			}
			if(data.data.length == 0){
				return true;
			}
			return data.data;
		// }else if(data.code == 427){
		// 	$.cookie("accessToken","");
		// 	$.cookie("userId","");
		// 	top.addAlertDialog('请重新登录',function(){
		// 		top.location.href = "login.html";
		// 	});
		// 	return false;
		}else{
			window.hideToast ? "" : top.addAlertDialog(data.msg);
			return false;
		}
	}else{
		return data;
	}
}

function gentParams(page,pageSize){
	var params = {};
	// var userId = $.cookie("userId");
	// var accessToken	= $.cookie("accessToken");
	// params.userId = userId;
	// params.accessToken = accessToken;
	if(page){
		params.pageSize = (pageSize || 10);
		params.pageNo = page;
	}
	var tt = new Date().getTime();
	params.tt = tt;
	return params;
}
/* ajax & kgloader end */