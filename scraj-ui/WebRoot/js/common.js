$(document).ready(function() {
	$(".go-top").on("click",function(){
	    $("html,body").animate({ scrollTop: 0 }, 600);		
	});
	
	$('table thead').on('click','input[type="checkbox"]',function(){
		var trs = $(this).closest("table").find("tbody").find("tr");
		for (var i = 0; i < trs.length; i++) {
			$(trs[i]).find("input[type='checkbox']:eq(0)").prop('checked',this.checked);
		}
	});
	
	$('table').on('click','tbody input[type="checkbox"]',function(event){
		if($(this).closest("td").index() == 0){
			var all = $(this).closest("table").find("thead").find("td:eq(0)").find("input[type='checkbox']");
			if(this.checked){
				var allCheck = true;
				var trs = $(this).closest("table").find("tbody").find("tr");
				for (var i = 0; i < trs.length; i++) {
					if($(trs[i]).find("td:eq(0) input[type='checkbox']").prop('checked') == false){
						allCheck = false;
					}
				}
				if(allCheck){
					all.prop('checked',this.checked);	
				}
			}else{
				all.prop('checked',this.checked);
			}
						
		}
	});
});

function reload(){
	window.location.reload();
}

function printPreview(oper) {
	var bdhtml=window.document.body.innerHTML;//获取当前页的html代码 
	var sprnstr="<!--startprint"+oper+"-->";//设置打印开始区域 
	var eprnstr="<!--endprint"+oper+"-->";//设置打印结束区域 
	var prnhtml=bdhtml.substring(bdhtml.indexOf(sprnstr)+sprnstr.length); //从开始代码向后取html 
	prnhtml=prnhtml.substring(0,prnhtml.indexOf(eprnstr));//从结束代码向前取html 
	prnhtml=prnhtml.replaceAll('href','aa');
	window.document.body.innerHTML=prnhtml; 
	window.print(); 
	window.document.body.innerHTML=bdhtml; 
}
 
function updateIframeH(){
	if(parent){
		parent.resizeIframe();
	}
}

function resizeIframe(){
	try{
		var iframe = document.getElementById("main-iframe");
		var iHeight = iframe.contentWindow.document.body.scrollHeight || iframe.contentWindow.document.documentElement.scrollHeight;
		var iHeight2 = parseInt($(iframe).css("min-height"));
		iframe.style.height = (iHeight > iHeight2 ? iHeight : iHeight2) + "px";
	}catch (e) {
	}
}

function removeEmptyElement(array){
	var result = [];
	for (var int = 0; int < array.length; int++) {
		if(array[int]){
			result.push(array[int]);
		}
	}
	if(result.length > 0){
		return result;		
	}else{
		return null;		
	}
}

function countDowner(count,dom,jumpHref){
	var jumpCount = count;
	if(dom){
		$(dom).text(jumpCount);	
	}
	window.countDown = setInterval(function(){
		jumpCount --;
		if( jumpCount == 0){
			clearInterval(window.countDown);
			if(jumpHref){
				goWebPage(jumpHref);	
			}
		}
		if(dom){
			$(dom).text(jumpCount);	
		}
	},1000);
}

function showImgOrDefault(dom){
	$.each($(dom),function(i,v){
		var src = $(this).attr("src");
		if(src == "" || src == null || src == "null" || src == getAbsoluteLink("") || src == getAbsoluteLink(null) || src == getAbsoluteLink("null")){
			$(this).attr('src',getAbsoluteLink('images/default.jpg'));
		}
	});
}

function formatDateTime(time,isHalf){
	if(time){
		var dt = null;
		if(typeof(time) == "string"){
			dt = new Date(time);
		}else if(time.time){
			dt = new Date(time.time);
		}else if(typeof(time) == "number"){
			dt = new Date(time);
		}
		if(dt){
			dt = dateToString(dt);
			if(isHalf && dt.indexOf(" ") > 0){
				dt = dt.split(" ")[0];
			}
			return dt;
		}	
	}
	return "";
}

function dateToString(dt){
	var month = (dt.getMonth() + 1) + "";
	var date = dt.getDate() + "";
	var hour = dt.getHours() + "";
	var minutes = dt.getMinutes() + "";
	var second = dt.getSeconds() + "";
	var year =  dt.getFullYear();
	if((""+month).length < 2){
		month = "0" + month;
	}
	if((""+date).length < 2){
		date = "0" + date;
	}
	if((""+hour).length < 2){
		hour = "0" + hour;
	}
	if((""+minutes).length < 2){
		minutes = "0" + minutes;
	}
	if(typeof(second) != "undefined" ){
		if((""+second).length < 2){
			second = "0" + second;
		}
		return year+"-"+month+"-"+date+" "+hour+":"+minutes+":"+second;
	}else{
		return year+"-"+month+"-"+date+" "+hour+":"+minutes;		
	}
}

function standardizeDate(time){
	time =  time.replaceAll("-","/");
	var dt = new Date(time);
	return dt;
}

function placeholderBug(){
	if( !('placeholder' in document.createElement('input')) ){   

	    $('input[placeholder],textarea[placeholder]').each(function(){    
	      var that = $(this),    
	      text= that.attr('placeholder');    
	      if(that.val()===""){    
	        that.val(text).addClass('placeholder');    
	      }    
	      that.focus(function(){    
	        if(that.val()===text){    
	          that.val("").removeClass('placeholder');    
	        }    
	      })    
	      .blur(function(){    
	        if(that.val()===""){    
	          that.val(text).addClass('placeholder');    
	        }    
	      })    
	      .closest('form').submit(function(){    
	        if(that.val() === text){    
	          that.val('');    
	        }    
	      });    
	    });    
	  }   
}

function formatPrice(price){
	price = price + "";
	if(price.indexOf(".") > 0){
		var sub = price.split(".")[1];
		if(sub.length == 1){
			return price.split(".")[0] + "." + sub + "0";
		}else if(sub.length == 2){
			return price;
		}else{
			if(sub.charAt(2) > 4){
				var int = price.split(".")[0];
				var ssub = (parseInt(sub.substring(0,2)) + 1) + "";
				if(ssub.length == 1){
					ssub = "0" + ssub;
				}else if(ssub.length == 3){
					ssub = ssub.substring(1);
					int = parseInt(int) + 1;
				}
				return int + "." + ssub;
			}else{
				return price.split(".")[0] + "." + sub.substring(0,2);
			}
		}
	}else if(price == '0' || price == 0){
		return "0";
	}else{
		return price+".00";
	}
}

function formatNumber(num){
	num = num + "";
	if(num.indexOf(".") > 0){
		var sub = num.split(".")[1];
		if(sub.length == 1){
			return num;
		}else{
			if(sub.charAt(1) > 4){
				return num.split(".")[0] + 1;
			}else{
				return num.split(".")[0] + "." + sub.substring(0,1);
			}
		}
	}else{
		return num;
	}
}

function showTipInfo(msg){
	var max = 0.8;
	var div = $('<div style="position:absolute;background: #000;border-radius: 10px;padding: 10px;color: #fff;opacity:0;top:50%;max-width:'+(max *100)+'%">'+msg+'</div>');
	var out = $('<div style="position:fixed;width:100%;height:100%;"></div>').append(div);
	$("body").append(out);
	var bw = out.outerWidth();
	var x = div.outerWidth();
	var y = div.outerHeight();
	if(bw * max > x){
		div.css({"margin-left":(bw/2-x/2)+"px","margin-top":(-y/2)+"px"});	
	}else{
		div.css({"left":(((1-max)/2)*100)+"%","margin-top":(-y/2)+"px"});
	}
	div.animate({"opacity":1},1000,function(){
		setTimeout(function(){
			out.fadeOut("slow",function(){
				out.remove();
			});
		},500);
	});
}

function showToast(msg) {
	var container = $("body");/*top.$("#sys-toast-container");
	if(!container.length){
		container = $('<div id="sys-toast-container"></div>');
		top.$("body").append(container);
	}*/
	var last = container.find(".sys-toast").last();
	var top = 10;
	if(last.length){
		top = last.offset().top + last.outerHeight() + 10;
	}
	var dlg = $('<span class="sys-toast" style="background:rgba(0,0,0,0.75);color:white;padding: 5px 10px;position:fixed;z-index:2000;opacity:0;width:200px;min-height:60px;border-radius:5px;right:10px;top:'+top+'px;">'+msg+'</span>');
	container.append(dlg);
	dlg.animate({opacity:1},500,function(){
		setTimeout(function(){
			dlg.fadeOut("show",function(){
				dlg.remove();
			});
		},2000);
	});	
}

function getSysDialogOffsetTop(dlgh,height,st){
	if(dlgh > height){
		return height * 0.05 + st;
	}else{
		return height * 0.5 - dlgh / 2 + st;
	}
}

function resetSysDlgPosition(that){
	var dlg = $(that).closest(".sys-dialog");
	var height = $(dlg).closest("html").height();
	if(dlg.length){
		var sh = dlg.height();
		var st = dlg.closest("body").scrollTop() || dlg.closest("html").scrollTop();
		dlg.css({"top":getSysDialogOffsetTop(sh,height,st) + "px"});
		if(sh > height * 0.9){
			var tbh = 0;
			var th = dlg.find(".sys-dialog-head"); 
			if(th.length){
				tbh += th.outerHeight();
			}
			var tf = dlg.find(".sys-dialog-foot"); 
			if(tf.length){
				tbh += tf.outerHeight();
			}
			var h = height*0.9 - tbh;
			dlg.find(".sys-dialog-body").css({"height":(h > 300 ? h : 300) +"px","overflow":"auto"});	
		}
	}
}

$(window).resize(function(event){
	if($(".sys-dialog").length){
		var dlgs = $(".sys-dialog:visible");
		for (var i = 0; i < dlgs.length; i++) {
			resetSysDlgPosition(dlgs[i])			
		}
	}
});

function showSysDialog(did){
	var dlg = $("#"+did);
	showThisDialog(dlg);
}

function showThisDialog(dlg){
	var mask = dlg.siblings("#sys-mask");
	resetSysDlgPosition(dlg[0]);
	dlg.show();
	if(mask.length  == 0){
		mask = $('<div id="sys-mask"></div>');
	}
	mask.show();
	dlg.before(mask);
}

function addSysDialog(param){
	return gentSysDialog(param).attr("id");
}

function gentSysDialog(param){
	var params = {
		obj:null,
		content:"",
		width:400,
//		height:"auto",
		footStyle:"",
		foot:"",
		buttons:[],
		hide:false,
		title:"",
		auto:true,
		reset:false
	};
	var sysDlg = null;
	var events ={};
	$.extend(params,param);
	var did = "sd"+new Date().getTime();
	var bodyDom = $('body');
	
	if(params.obj){
		if($(params.obj).closest(".sys-dialog").length > 0){
			did = $(params.obj).closest(".sys-dialog").attr("id");
			if(params.reset){
				$(params.obj).closest(".sys-dialog").remove();
				params.obj = bodyDom.data(did);
			}
		}else{
			bodyDom.data(did,params.obj.clone());			
		}
	}
//	if(params.obj && $(params.obj).closest(".sys-dialog").length > 0){
//		
//	}else{
		var existdlg = bodyDom.find(".sys-dialog");
		var ex = false;
		for (var i = 0; i < existdlg.length; i++) {
			var dlg = $(existdlg[i]);
			if(dlg.find(".sys-dialog-body").html() == params.content){
				sysDlg = dlg;
				did = dlg.attr("id");
				ex = true;
				break;
			}
		}
		if(!ex){
			var btnDefault = {};
			var style = "";
//			var styleHeight = "";
//			if(params.height != "auto"){
//				if(typeof(params.height) == "string" && params.height.indexOf("%") > 0){
//					styleHeight = "height:"+params.height + ";";
//				}else{
//					styleHeight = "height:"+params.height + "px;";	
//				}
//			}
			if(params.width){
				if(typeof(params.width) == "string" && params.width.indexOf("%") > 0){
					style = "style=\"width:"+params.width + ";margin-left:-"+parseFloat(params.width) / 2 + "%;\"";
				}else{
					style = "style=\"width:"+params.width + "px;margin-left:-"+params.width / 2 + "px;\"";	
				}
			}
			var foot = "";
			var footStyle = "";
			if(params.footStyle){
				footStyle = "style='"+params.footStyle+"'";
			}
			if(params.foot){
				foot = '<div class="sys-dialog-foot" '+footStyle+'>'+params.foot+'</div>';
			}else{
				if(params.buttons.length){
					foot = '<div class="sys-dialog-foot" '+footStyle+'>';
					for (var i = 0; i < params.buttons.length; i++) {
						var btn = params.buttons[i];
						var event = btn.event || "closeThisSysDialog(this,"+params.hide+")";
						var id = did + i;
						if(btn.isDefault){
							btnDefault.event = event;
							btnDefault.id = id;
						}
						events[id] = event;
						var text = btn.text || "确定";
						var cls = btn.cls || "btn-primary";
						foot += '<input type="button" class="btn ' + cls + '" value="'+text+'" id="'+id+'"/>';
						if(i != params.buttons.length - 1){
							foot += '&nbsp;&nbsp;&nbsp;&nbsp;';
						}
					}
					foot += '</div>';
				}
			}
			var head = "";
			if(params.title){
				head = '<div class="sys-dialog-head">'+params.title+'<div class="sys-dialog-close"></div></div>';
			}
//			bodyDom.find("#sys-mask").remove();
//			bodyDom.append('<div id="sys-mask"></div>');	
//			if($("#sys-mask").length == 0){
//				bodyDom.append('<div id="sys-mask"></div>');	
//			}	
			sysDlg = $('<div id="'+did+'" class="sys-dialog" '+style+'>'+head+
					'<div class="sys-dialog-content"><div class="sys-dialog-body clearfix">'+params.content+		
					'</div>'+foot+'</div></div>');
			bodyDom.append(sysDlg);
			sysDlg.find(".sys-dialog-close").on("click",{hide:params.hide},function(event){
				closeThisSysDialog(this,event.data.hide);
			})
			for ( var eid in events) {
				sysDlg.find("#"+eid).on("click",function(event){
					var eid = $(this).attr("id");
					if(!btnDefault.id){
						btnDefault.id = eid;
						btnDefault.event = events[eid];
					}
					if(typeof(events[eid]) == "string"){
						eval(events[eid]);
					}else{
						events[eid](this,$(this).closest(".sys-dialog"),event);
					}
				});
			}
			if(params.obj){
				sysDlg.find(".sys-dialog-body").append(params.obj);
			}
			sysDlg.find(".sys-dialog-head").on("mousedown",{dlg:sysDlg},function(event){
				if($(event.target).closest(".sys-dialog-close").length == 0){
					window.dragging = true;
	                var dlg = event.data.dlg;
	                window.dragObj = dlg;
					window.iX = event.clientX - dlg[0].offsetLeft + parseInt($(dlg).css("margin-left"));
					window.iY = event.clientY - dlg[0].offsetTop + parseInt($(dlg).css("margin-top"));
	                this.setCapture && this.setCapture();
	                return false;
				}                
			});
			bodyDom.mousemove(function(event) {
                if (window.dragging === true && window.dragObj) {
	                var oX = event.clientX - window.iX;
	                var oY = event.clientY - window.iY;
	                window.dragObj.css({"left":oX + "px", "top":oY + "px"});
	                return false;
                }
            });
            bodyDom.mouseup(function(event) {
            	if (window.dragging === true && window.dragObj) {
	                window.dragging = false;
	                window.dragObj[0].releaseCapture && window.dragObj[0].releaseCapture();
	                window.dragObj = null;
	                event.cancelBubble = true;
            	}
            });
		}
//	}
	if(params.auto){
		showThisDialog(sysDlg);
	}
	return sysDlg;
}

function closeThisDialog(dlg,hide){
	var mask = dlg.prev("#sys-mask");
	if(dlg.siblings(".sys-dialog:visible").length == 0){
		mask.remove();	
	}else{
		dlg.siblings(".sys-dialog:visible").last().before(mask);
	}
	if(hide){
		dlg.hide();
	}else{
		dlg.remove();
	}
}

function closeThisSysDialog(that,hide){
	var dlg = $(that).closest(".sys-dialog");
	closeThisDialog(dlg, hide);
}

function closeSysDialog(did,hide){
	var dlg = $("#"+did);
	closeThisDialog(dlg,hide);
}

function addAlertDialog(msg,event,hideico){
	if(!event && msg == $(".sys-dialog:visible").find(".sys-dialog-body").text()){
		return;
	}
	var ico = '<i class="fa fa-exclamation-triangle fa-2x" style="vertical-align: -4px;margin-right: 10px;color: #FC0;"></i>';
	if(hideico){
		ico = "";
	}
	var params = {};
	params.title = "提 示";
	params.width = 400;
	params.buttons = new Array();
	var btn = {};
	if(event){
		btn.event = event;		
	}
	btn.cls = "btn-primary";
	btn.text = " 确 定 ";
	params.buttons.push(btn);
	params.content = '<div style="text-align: center;padding: 40px 0 10px;">'+ico + msg+'</div>';
	params.auto = true;
	return addSysDialog(params);
}

function addSuccDialog(msg,event,hideico){
	if(!event && msg == $(".sys-dialog:visible").find(".sys-dialog-body").text()){
		return;
	}
	var ico = '<i class="fa fa-check-circle-o fa-2x" style="vertical-align: -4px;margin-right: 10px;color: #3C8DBC;"></i>';
	if(hideico){
		ico = "";
	}
	var params = {};
	params.title = "提 示";
	params.width = 400;
	params.buttons = new Array();
	var btn = {};
	if(event){
		btn.event = event;		
	}
	btn.cls = "btn-primary";
	btn.text = " 确 定 ";
	params.buttons.push(btn);
	params.content = '<div style="text-align: center;padding: 40px 0 10px;">'+ico + msg+'</div>';
	params.auto = true;
	return addSysDialog(params);
}

function addDeleteDialog(event){
	return addConfirmDialog('确认删除？', event);
}

function addDetailDialog(title,data,callback,width){
	var html = $('<pre></pre>').text(data).html();
	if($.isPlainObject(data)){
		html = gentHTML(data);
	}else if($.isArray(data)){
		html = "";
		for(var i = 0;i<data.length;i++){
			html += gentHTML(data[i])+"<br/>";
		}
	}
	function gentHTML(item) {
		var html = $("<div><table class='detail-box' width='100%'></table></div>");
		for ( var key in item) {
			var tr = $("<tr><td>"+key+"</td><td></td></tr>");
			tr.find("td:eq(1)").text(item[key]);
			html.children("table").append(tr);
		}
		return html.html();
	}
	var params = {};
	params.title = title;
	params.width = width || 600;
	params.buttons = new Array();
	var btn = {};
	if(callback){
		btn.event = callback;	
	}
	btn.cls = "btn-primary";
	btn.text = " 确 定 ";
	params.buttons.push(btn);
	params.content = '<div style="padding: 20px 0 10px;">'+html+'</div>';
	params.auto = true;
	return addSysDialog(params);
}

function addImgDialog(title,src,width){
	var img = '<img src="'+src+'" style="max-width:100%" onload="resetSysDlgPosition(this)">';
	var html = '<a href="javascript:window.open(\''+src+'\')">'+img+'</a>';
	var params = {};
	params.title = title || "照片查看";
	params.width = width || 800;
	params.content = html;
	params.auto = true;
	return addSysDialog(params);
}

function addConfirmDialog(title,event){
	var params = {};
	params.title = "提 示";
	params.width = 400;
	params.buttons = new Array();
	var btn2 = {};
	btn2.cls = "btn-cancel";
	btn2.text = " 取 消 ";
	params.buttons.push(btn2);
	var btn = {};
	btn.event = event;
	btn.cls = "btn-primary";
	btn.text = " 确 定 ";
	params.buttons.push(btn);
	params.content = '<div style="text-align: center;padding: 40px 0 10px;">'+title+'</div>';
	params.auto = true;
	return addSysDialog(params);
}

function addSuperConfirmDialog(title,event,tip){
	tip = tip || title;
	var params = {};
	params.title = title;
	params.width = 400;
	params.buttons = new Array();
	var btn2 = {};
	btn2.cls = "btn-cancel";
	btn2.text = " 取 消 ";
	params.buttons.push(btn2);
	var btn = {};
	btn.event = event;
	btn.cls = "btn-primary";
	btn.text = " 确 定 ";
	params.buttons.push(btn);
	params.content = '<div style="padding: 30px 0 10px;"><div>请输入"'+tip+'"确认进行'+title+'操作</div><div><input id="super-confirm-input" class="super-confirm-input"></div></div>';
	params.auto = true;
	return addSysDialog(params);
}

function goWebPage(page,isTop){
	if(isTop){
		top.location.href = getAbsoluteLink(page);
	} else{
		window.location.href = getAbsoluteLink(page);
	}
}

function goNewWebPage(page){
	 window.open(getAbsoluteLink(page));	
}

function getWebRoot(){
	var href = window.location.href;
	var appName = window.defaultAppName || "";
	if(href.indexOf("/" + appName + "/", 4) > 0 ){
		return href.substring(0, href.indexOf("/" + appName + "/", 8) + appName.length + 2);
	}else{
		return href.substring(0, href.indexOf("/", 8) + 1);
	}
}

function getAbsoluteLink(str){
	if(str && str != null){
		if(str.indexOf("http://") == 0 || str.indexOf("https://") == 0){
			return str;
		}
		return getWebRoot() + str; 		
	}else{
		return str;
	}
}

function getTopJsonNode(nodes,nodeid){
	var findedId = nodeid;
	while (true && nodes.length > 0) {
		for(var i = 0;i < nodes.length;i++){
			if(nodes[i].id == findedId){
				findedId = nodes[i].pid;
				if(findedId == 0){
					return nodes[i];
				}
			}
		}
	}
}

function getParentJsonNode(nodes,nodeid){
	for(var i = 0;i < nodes.length;i++){
		if(nodes[i].id == nodeid){
			nodeid = nodes[i].pid;
			for(var i = 0;i < nodes.length;i++){
				if(nodes[i].id == nodeid){
					return nodes[i];
				}
			}
		}
	}
}

function getJsonNode(nodes,nodeid){
	for(var i = 0;i < nodes.length;i++){
		if(nodes[i].id == nodeid){
			return nodes[i];
		}
	}
}

function getChildrenJsonNodes(nodes,nodeid){
	var chilrenNode = new Array();
	for(var i = 0;i < nodes.length;i++){
		if(nodes[i].pid == nodeid){
			chilrenNode.push(nodes[i]);
		}
	}
	return chilrenNode;
}

function resetUrlParm(k,v,link){
	if(!link){
		link = window.location.href;		
	}
	if(typeof(k) == "string"){
		var s = "&"+k+"=";
		var ss = "?"+k+"=";
		var c = "";
		if(link.indexOf("#") > 0){
			c = "#"+link.split("#")[1];
			link = link.split("#")[0];
		}
		if(link.indexOf(s) > 0){
			var b = link.split(s)[0];
			
			var a = "";
			if(link.split(s)[1].indexOf("&") > 0){
				var at = link.split(s)[1];
				a = at.substring(at.indexOf("&"));
			}
			if(v == null){
				return b + a + c;
			}else{
				return b + s + v + a + c;
			}
		}else if(link.indexOf(ss) > 0){
			var b = link.split(ss)[0];
			var a = "";
			if(link.split(ss)[1].indexOf("&") > 0){
				var at = link.split(ss)[1];
				a = at.substring(at.indexOf("&"));
			}
			if(v == null){
				return b + a.replace("&", "?") + c;
			}else{
				return b + ss + v + a + c;
			}
		}else{
			if(v == null){
				return link + c;
			}else{
				if(link.indexOf("?") > 0){
					return link + s + v + c;						
				}else{
					return link + ss + v + c;						
				}
			}	
		}
	}else if(typeof(k) == "object"){
		for ( var p in k ){ // 方法 
		   link = resetUrlParm(p,k[p],link);
		}
		return link;
	}
}

function getDecodeParam(name) {
	var r = getUrlParam(name);
	if(r != null){
		try{
			r = decodeURIComponent(escape(r));			
		}catch (e) {
			
		}
	}
	return r;
}

function getUrlParam(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}

String.prototype.replaceAll = function(reallyDo, replaceWith, ignoreCase) {
	if (!RegExp.prototype.isPrototypeOf(reallyDo)) { 
		return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi": "g")), replaceWith); 
	} else { 
		return this.replace(reallyDo, replaceWith); 
	}
};

function gentPage(pageParms){
	if(!isNaN(pageParms.data)){
		var total,event;
		if(pageParms.data || pageParms.data === 0){
			$(pageParms.p).data("total",pageParms.data);
			total = pageParms.data;
			$(pageParms.p).data("event",pageParms.event);
			event = pageParms.event;
		}else{
			total = $(pageParms.p).data("total");
			event = $(pageParms.p).data("event");
		}
		setNormalPage({count:pageParms.count,total:total,cur:pageParms.cur,p:pageParms.p,tag:"li",curtag:"active",splite:"",event:event,hideBA:true,all:(pageParms.all || 9),reloadable:pageParms.reloadable});
	}
}

function setNormalPage(pageParms) {
	//var totalitem = pageParms.totalitem;
	var reloadable = pageParms.reloadable || false;
	var total = pageParms.total;
	var all = pageParms.all || 9;
	var half = parseInt(all / 2);
	var first = 1;
	var last = total;
	var center = parseInt(pageParms.cur);
	var cur = parseInt(pageParms.cur);
	var tag = pageParms.tag || "span";
	var hideBA = pageParms.hideBA || false;
	var baClass = hideBA ? "hide":"";
	var curtag = pageParms.curtag || "";
	var splite = "&nbsp;&nbsp;";
	if(typeof(pageParms.splite) != undefined){
		splite = pageParms.splite;
	}
	if (last > all) {
		if (center - half > 1) {
			first = center - half;
		}
		if (last - center < half) {
			first = last - all + 1;
		} else {
			last = first + all - 1;
		}
	}
	var list = '';
	var totalShow = "";
	if (pageParms.total == 0) {
		list = '<'+tag+' class="page_no_data">没有相关数据...</'+tag+'>';
	} else {
		if (reloadable || first != cur) {
			list += '<'+tag+' class="page_text"><a href="javascript:void(0);" onclick="'
					+ pageParms.event
					+ '1,this)">&lt;&lt;</a></'+tag+'>'+splite+'<'+tag+' class="page_text '+baClass+'"><a href="javascript:void(0);" onclick="'
					+ pageParms.event + (cur - 1) + ',this)">&lt;</a></'+tag+'>'+splite+splite;
		} else {
			list += '<'+tag+' class="page_text hide"><a href="javascript:void(0);">&lt;&lt;</a></'+tag+'>'+splite+'<'+tag+' class="page_text '+baClass+'"><a href="javascript:void(0);">&lt;</a></'+tag+'>'+splite+splite;
		}
		for ( var i = first; i <= last; i++) {
			if (i == cur) {
				list += '<'+tag+' class="page_focus '+curtag+'"><a href="javascript:void(0);" onclick="'
				+ pageParms.event + i + ',this)">'
					+ i + '</a></'+tag+'>'+splite;
			} else {
				list += '<'+tag+' class="page_num"><a href="javascript:void(0);" onclick="'
						+ pageParms.event + i + ',this)">' + i + '</a></'+tag+'>'+splite;
			}
		}
		if (reloadable || total != cur) {
			list += splite+'<'+tag+' class="page_text '+baClass+'"><a href="javascript:void(0);" onclick="'
					+ pageParms.event
					+ (cur + 1)
					+ ',this)">&gt;</a></'+tag+'>'+splite+'<'+tag+' class="page_text"><a href="javascript:void(0);" onclick="'
					+ pageParms.event + total + ',this)">&gt;&gt;</a></'+tag+'>';
		} else {
			list += splite+'<'+tag+' class="page_text '+baClass+'"><a href="javascript:void(0);">&gt;</a></'+tag+'>'+splite+'<'+tag+' class="page_text hide"><a href="javascript:void(0);">&gt;&gt;</a></'+tag+'>';
		}
		if(pageParms.count){
			totalShow = '<div class="page_total">总计'+pageParms.count+'条数据</div>';	
		}
	}
	pageParms.p.empty().append(list);
	if(pageParms.count){
		pageParms.p.siblings(".page_total").remove();
		pageParms.p.after(totalShow);
	}
}

function md5(sMessage) {
	function RotateLeft(lValue, iShiftBits) {
		return (lValue << iShiftBits) | (lValue >>> (32 - iShiftBits));
	}

	function AddUnsigned(lX, lY) {
		var lX4, lY4, lX8, lY8, lResult;
		lX8 = (lX & 0x80000000);
		lY8 = (lY & 0x80000000);
		lX4 = (lX & 0x40000000);
		lY4 = (lY & 0x40000000);
		lResult = (lX & 0x3FFFFFFF) + (lY & 0x3FFFFFFF);
		if (lX4 & lY4)
			return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
		if (lX4 | lY4) {
			if (lResult & 0x40000000)
				return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
			else
				return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
		} else
			return (lResult ^ lX8 ^ lY8);
	}

	function F(x, y, z) {
		return (x & y) | ((~x) & z);
	}

	function G(x, y, z) {
		return (x & z) | (y & (~z));
	}

	function H(x, y, z) {
		return (x ^ y ^ z);
	}

	function I(x, y, z) {
		return (y ^ (x | (~z)));
	}

	function FF(a, b, c, d, x, s, ac) {
		a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac));
		return AddUnsigned(RotateLeft(a, s), b);
	}

	function GG(a, b, c, d, x, s, ac) {
		a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac));
		return AddUnsigned(RotateLeft(a, s), b);
	}

	function HH(a, b, c, d, x, s, ac) {
		a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac));
		return AddUnsigned(RotateLeft(a, s), b);
	}

	function II(a, b, c, d, x, s, ac) {
		a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac));
		return AddUnsigned(RotateLeft(a, s), b);
	}

	function ConvertToWordArray(sMessage) {
		var lWordCount;
		var lMessageLength = sMessage.length;
		var lNumberOfWords_temp1 = lMessageLength + 8;
		var lNumberOfWords_temp2 = (lNumberOfWords_temp1 - (lNumberOfWords_temp1 % 64)) / 64;
		var lNumberOfWords = (lNumberOfWords_temp2 + 1) * 16;
		var lWordArray = Array(lNumberOfWords - 1);
		var lBytePosition = 0;
		var lByteCount = 0;
		while (lByteCount < lMessageLength) {
			lWordCount = (lByteCount - (lByteCount % 4)) / 4;
			lBytePosition = (lByteCount % 4) * 8;
			lWordArray[lWordCount] = (lWordArray[lWordCount] | (sMessage
					.charCodeAt(lByteCount) << lBytePosition));
			lByteCount++;
		}
		lWordCount = (lByteCount - (lByteCount % 4)) / 4;
		lBytePosition = (lByteCount % 4) * 8;
		lWordArray[lWordCount] = lWordArray[lWordCount]
				| (0x80 << lBytePosition);
		lWordArray[lNumberOfWords - 2] = lMessageLength << 3;
		lWordArray[lNumberOfWords - 1] = lMessageLength >>> 29;
		return lWordArray;
	}

	function WordToHex(lValue) {
		var WordToHexValue = "", WordToHexValue_temp = "", lByte, lCount;
		for (lCount = 0; lCount <= 3; lCount++) {
			lByte = (lValue >>> (lCount * 8)) & 255;
			WordToHexValue_temp = "0" + lByte.toString(16);
			WordToHexValue = WordToHexValue
					+ WordToHexValue_temp.substr(
							WordToHexValue_temp.length - 2, 2);
		}
		return WordToHexValue;
	}
	var x = Array();
	var k, AA, BB, CC, DD, a, b, c, d;
	var S11 = 7, S12 = 12, S13 = 17, S14 = 22;
	var S21 = 5, S22 = 9, S23 = 14, S24 = 20;
	var S31 = 4, S32 = 11, S33 = 16, S34 = 23;
	var S41 = 6, S42 = 10, S43 = 15, S44 = 21;
	x = ConvertToWordArray(sMessage);
	a = 0x67452301;
	b = 0xEFCDAB89;
	c = 0x98BADCFE;
	d = 0x10325476;
	for (k = 0; k < x.length; k += 16) {
		AA = a;
		BB = b;
		CC = c;
		DD = d;
		a = FF(a, b, c, d, x[k + 0], S11, 0xD76AA478);
		d = FF(d, a, b, c, x[k + 1], S12, 0xE8C7B756);
		c = FF(c, d, a, b, x[k + 2], S13, 0x242070DB);
		b = FF(b, c, d, a, x[k + 3], S14, 0xC1BDCEEE);
		a = FF(a, b, c, d, x[k + 4], S11, 0xF57C0FAF);
		d = FF(d, a, b, c, x[k + 5], S12, 0x4787C62A);
		c = FF(c, d, a, b, x[k + 6], S13, 0xA8304613);
		b = FF(b, c, d, a, x[k + 7], S14, 0xFD469501);
		a = FF(a, b, c, d, x[k + 8], S11, 0x698098D8);
		d = FF(d, a, b, c, x[k + 9], S12, 0x8B44F7AF);
		c = FF(c, d, a, b, x[k + 10], S13, 0xFFFF5BB1);
		b = FF(b, c, d, a, x[k + 11], S14, 0x895CD7BE);
		a = FF(a, b, c, d, x[k + 12], S11, 0x6B901122);
		d = FF(d, a, b, c, x[k + 13], S12, 0xFD987193);
		c = FF(c, d, a, b, x[k + 14], S13, 0xA679438E);
		b = FF(b, c, d, a, x[k + 15], S14, 0x49B40821);
		a = GG(a, b, c, d, x[k + 1], S21, 0xF61E2562);
		d = GG(d, a, b, c, x[k + 6], S22, 0xC040B340);
		c = GG(c, d, a, b, x[k + 11], S23, 0x265E5A51);
		b = GG(b, c, d, a, x[k + 0], S24, 0xE9B6C7AA);
		a = GG(a, b, c, d, x[k + 5], S21, 0xD62F105D);
		d = GG(d, a, b, c, x[k + 10], S22, 0x2441453);
		c = GG(c, d, a, b, x[k + 15], S23, 0xD8A1E681);
		b = GG(b, c, d, a, x[k + 4], S24, 0xE7D3FBC8);
		a = GG(a, b, c, d, x[k + 9], S21, 0x21E1CDE6);
		d = GG(d, a, b, c, x[k + 14], S22, 0xC33707D6);
		c = GG(c, d, a, b, x[k + 3], S23, 0xF4D50D87);
		b = GG(b, c, d, a, x[k + 8], S24, 0x455A14ED);
		a = GG(a, b, c, d, x[k + 13], S21, 0xA9E3E905);
		d = GG(d, a, b, c, x[k + 2], S22, 0xFCEFA3F8);
		c = GG(c, d, a, b, x[k + 7], S23, 0x676F02D9);
		b = GG(b, c, d, a, x[k + 12], S24, 0x8D2A4C8A);
		a = HH(a, b, c, d, x[k + 5], S31, 0xFFFA3942);
		d = HH(d, a, b, c, x[k + 8], S32, 0x8771F681);
		c = HH(c, d, a, b, x[k + 11], S33, 0x6D9D6122);
		b = HH(b, c, d, a, x[k + 14], S34, 0xFDE5380C);
		a = HH(a, b, c, d, x[k + 1], S31, 0xA4BEEA44);
		d = HH(d, a, b, c, x[k + 4], S32, 0x4BDECFA9);
		c = HH(c, d, a, b, x[k + 7], S33, 0xF6BB4B60);
		b = HH(b, c, d, a, x[k + 10], S34, 0xBEBFBC70);
		a = HH(a, b, c, d, x[k + 13], S31, 0x289B7EC6);
		d = HH(d, a, b, c, x[k + 0], S32, 0xEAA127FA);
		c = HH(c, d, a, b, x[k + 3], S33, 0xD4EF3085);
		b = HH(b, c, d, a, x[k + 6], S34, 0x4881D05);
		a = HH(a, b, c, d, x[k + 9], S31, 0xD9D4D039);
		d = HH(d, a, b, c, x[k + 12], S32, 0xE6DB99E5);
		c = HH(c, d, a, b, x[k + 15], S33, 0x1FA27CF8);
		b = HH(b, c, d, a, x[k + 2], S34, 0xC4AC5665);
		a = II(a, b, c, d, x[k + 0], S41, 0xF4292244);
		d = II(d, a, b, c, x[k + 7], S42, 0x432AFF97);
		c = II(c, d, a, b, x[k + 14], S43, 0xAB9423A7);
		b = II(b, c, d, a, x[k + 5], S44, 0xFC93A039);
		a = II(a, b, c, d, x[k + 12], S41, 0x655B59C3);
		d = II(d, a, b, c, x[k + 3], S42, 0x8F0CCC92);
		c = II(c, d, a, b, x[k + 10], S43, 0xFFEFF47D);
		b = II(b, c, d, a, x[k + 1], S44, 0x85845DD1);
		a = II(a, b, c, d, x[k + 8], S41, 0x6FA87E4F);
		d = II(d, a, b, c, x[k + 15], S42, 0xFE2CE6E0);
		c = II(c, d, a, b, x[k + 6], S43, 0xA3014314);
		b = II(b, c, d, a, x[k + 13], S44, 0x4E0811A1);
		a = II(a, b, c, d, x[k + 4], S41, 0xF7537E82);
		d = II(d, a, b, c, x[k + 11], S42, 0xBD3AF235);
		c = II(c, d, a, b, x[k + 2], S43, 0x2AD7D2BB);
		b = II(b, c, d, a, x[k + 9], S44, 0xEB86D391);
		a = AddUnsigned(a, AA);
		b = AddUnsigned(b, BB);
		c = AddUnsigned(c, CC);
		d = AddUnsigned(d, DD);
	}
	var temp = WordToHex(a) + WordToHex(b) + WordToHex(c) + WordToHex(d);
	return temp.toLowerCase();
}

function removeHTMLTag(str) {
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str=str.replace(/&nbsp;/ig,'');//去掉&nbsp;
    return str;
}

function removeHTMLTagWithoutImg(str){
	if(str.indexOf("<img ") != 0){
		str = removeHTMLTag(str);
	}
	return str;
}

function lazyload(option) {
	var settings = {
	   defObj: null,
	   defHeight: 0
	};
	settings = $.extend(settings, option || {});
	//var defHeight = settings.defHeight, defObj = (typeof settings.defObj == "object") ? settings.defObj.find("img") : $(settings.defObj).find("img");
	var pageTop = function () {
	   return document.documentElement.clientHeight + Math.max(document.documentElement.scrollTop, document.body.scrollTop) - settings.defHeight;
	};
	var imgLoad = function () {
	   defObj.each(function () {
		   if ($(this).offset().top <= pageTop()) {
			   var src2 = $(this).attr("src2");
			   if (src2) {
				   $(this).attr("src", src2).removeAttr("src2");
			   }
		   }
	   });
	};
	imgLoad();
	$(window).bind("scroll", function () {
	   imgLoad();
	});
}

function limitWords(text,num){
	if(text.length >= num){
		return text.substring(0,num) + '  ...';
	}else{
		return text;
	}
}

function html_encode(str){
	if (typeof(str) != "string" || str.length == 0) return "";
	return str.replace(/&/g, '&amp;').replace(/\"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function html_decode(str){
	if (typeof(str) != "string" || str.length == 0) return "";
	return str.replace(/&amp;/g, '&').replace(/&quot;/g, '\"').replace(/&lt;/g, '<').replace(/&gt;/g, '>');
}

function unParam(str){
	var rs = {};
	str.split('&').forEach(function(param){
		if(param.indexOf("[") > 0 && param.indexOf("[") < param.indexOf("=")){
			var keyFalse = param.substring(0,param.indexOf("="));
			var keyTrue = param.substring(0,param.indexOf("["));
			var val = param.substring(param.indexOf("=") + 1);
			dealParam(keyFalse,keyTrue,val,rs);
		}else{
			param = param.split('=');
			rs[param[0]] = param[1];	
		}
	});
	return rs;
}

function dealParam(str2,key,val,rs){
	var k = key;
	var t = rs;
	var a = str2.substring(str2.indexOf("[") + 1,str2.indexOf("]"));
	var r = str2.substring(str2.indexOf("]") + 1);
	if(a && !$.isNumeric(a)){
		if($.isArray(rs)){
			if(rs.length <= key){
				rs.push({});	
			}
			k = a;
			t = rs[key];
		}else{
			if(!rs[key]){
				rs[key] = {};	
			}
			k = a;
			t = rs[key];
		}
	}else{
		if($.isArray(rs)){
			if(!rs[key]){
				rs[key] = [];	
			}
			k = a;
			t = rs[key];
		}else{
			if(!rs[key]){
				rs[key] = [];
			}
			k = a;
			t = rs[key]; 
		}
	}
	if(r.indexOf("[") == 0){
		dealParam(r,k,val,t);
	}else{
		if(a && !$.isNumeric(a)){
			t[a] = val;
		}else{
			if(!a){
				t.push(val);
			}else{
				t[a] = val;	
			}
		}
	}
}

function deapMerge(a,b,arr){
	if($.isPlainObject(a)){
		for ( var k in a) {
			if(b[k]){
				deapMerge(a[k], b[k]);
			}else{
				arr.push(b);
			}
		}
	}else if($.isArray(a)){
		var len = a.length;
		for (var i = 0; i < len; i++) {
			for (var j = 0; j < b.length; j++) {
				if(($.isPlainObject(a[i]) && $.isPlainObject(b[j]))||($.isArray(a[i]) && $.isArray(b[j]))){
					deapMerge(a[i], b[j], a);	
				}else{
					a.push(b[j]);
				}
			}
		}
	}else{
		$.merge(a, b); 
	}
}

function setTextData($out,data,j,h){
	for(var k in data){
		if(j){
			data[k] = dealNull(data[k]);
		}
		var $obj = $out.find("[name='"+k+"']");
		if($obj.length && $obj[0].tagName == "IMG"){
			$obj.attr("src",data[k]);
		}else{
			if(h){
				$obj.html(data[k]);	
			}else{
				$obj.text(data[k]);	
			}
		}
	}
}

function getFormItemData(v,data,attr){
	var d = "";
	var n = $(v).attr(attr || "name");
	if(n){
		d = data[n];
		if(n.indexOf(",") >= 0){
			d = "";
			var ns = n.split(",");
			var ex = false;
			for (var k = 0; k < ns.length; k++) {
				if(ns[k]){
					ex = true;
					var dx = data[ns];
					if(ns.indexOf("|") >= 0){
						var ks = ns.split("|");
						for (var l = 0; l < ks.length; l++) {
							if(dx){
								break;
							}else{
								if(data[ks[l]]){
									dx = data[ks[l]];
								}
							}
						}
					}
					d += " " + dx;
				}
			}
			if(ex){
				d = d.substring(1);
			}
		}
	}
	return d;
}

function setFormData($out,data,j){
	var inputs = $out.find("input[name][type=checkbox],input[name][type=radio]");
	inputs.prop("checked",false);
	inputs = $out.find("input[name],select[name],textarea[name],img,div[name]");
	$.each(inputs,function(i,v){
		var d = getFormItemData(v,data);
		updateFormData(v,d,j);
	});
	
	function updateFormData(v,d,j){
		if($(v)[0].tagName == "IMG"){
			if(d){
				d = dealNull(d);
			}			
			$(v).attr("src",d);
		}else if($(v).attr("type") == "checkbox"){
			if($.isArray(d)){
				var array = d;
				$(v).prop("checked",false);	
				$(v).removeAttr("checked");	
				for (var i = 0; i < array.length; i++) {
					if(array[i] == $(v).val()){
						$(v).prop("checked",true);
						$(v).attr("checked",true);	
					}
				}
			}else/* if(typeof(d) == "string")*/{
				if(d == true || d == $(v).val()){
					$(v).prop("checked",true);
					$(v).attr("checked",true);	
				}else{
					$(v).prop("checked",false);	
					$(v).removeAttr("checked");				
				}
			}
		}else if($(v).attr("type") == "radio"){
			if(d == $(v).val()){
				$(v).prop("checked",true);
				$(v).attr("checked",true);	
			}else{
				$(v).prop("checked",false);	
				$(v).removeAttr("checked");				
			}
		}else{
			if(j){
				d = dealNull(d);
			}
			if($(v)[0].tagName == "DIV"){
				$(v).html(d);
			}else{
				$(v).val(d);
				
			}
		}
	}
}

function getFormData($out,getUnChecked){
	var mdata = {};
	var inputs = $out.find("input[name],select[name],textarea[name]");
	$.each(inputs,function(i,v){
		var n = $(v).attr("name");
		if($(v).attr("type") == "checkbox"){
			if(getUnChecked){
				mdata[n] = v.checked;
			}else{
				if(v.checked){
					if(!mdata[n]){
						mdata[n] = [];
					}
					mdata[n].push($(v).val());	
				}
			}
		}else if($(v).attr("type") == "radio" && !v.checked){
		}else{
			mdata[n] = $(v).val();
		}
	});
	return mdata;
}

function dealNull(obj){
	if($.isArray(obj)){
		for (var i = 0; i < obj.length; i++) {
			obj[i] = dealNull(obj[i]);
		}
	}else if($.isPlainObject(obj)){
		for(var k in obj){
			obj[k] = dealNull(obj[k]);
		}
	}else{
		if(obj === 0){
			
		}else{
			obj = obj || "";	
		}
	}
	return obj;
}

/*add zero*/
function pad(num, n) { 
	return (Array(n).join(0) + num).slice(-n); 
}