$(function(){
	bindListDefaultEvent();
}); 

function bindListDefaultEvent(){
	$("body").on("click",".search-button",function(){
		var fun = $(this).closest(".ajaxtable-container").attr("data-method");
		eval(fun)(1,this);
	});

	$("body").on("click","th.nsort",function(){
		var sort = "";
		$(this).siblings("th.nsort").find(".fa").removeClass("fa-caret-up").removeClass("fa-caret-down").addClass("fa-sort");
		if($(this).find(".fa").hasClass("fa-sort")){
			$(this).find(".fa").removeClass("fa-sort").addClass("fa-caret-down");
		}else if($(this).find(".fa").hasClass("fa-caret-down")){
			$(this).find(".fa").removeClass("fa-caret-down").addClass("fa-caret-up");
		}else if($(this).find(".fa").hasClass("fa-caret-up")){
			$(this).find(".fa").removeClass("fa-caret-up").addClass("fa-sort");
		}
		var fun = $(this).closest(".ajaxtable-container").attr("method");
		eval(fun)(1,this);
	});
}

function getSearchOrder(container){
	var searchOrder = null;
	var sortItem = container.find("th.nsort").find(".fa-caret-down,.fa-caret-up");
	if(sortItem.length){
		var orderField = sortItem.closest("th").attr("name");
		var orderType = $(sortItem).hasClass("fa-caret-down") ? "desc":"asc";
		searchOrder = {"orderField":orderField,"orderType":orderType};
	}
	return searchOrder;
}

function getSearchItem(container){
	var searchItems = {};
	var  searchItem = container.find(".search-item");
	for ( var i = 0;i< searchItem.length;i++) {
		if($(searchItem[i]).val() != ""){
			searchItems[$(searchItem[i]).attr("name")] = $(searchItem[i]).val();	
		}
	}
	return searchItems;
}

function getSearchItems(container){
	var searchItems = [];
	var  searchItem = container.find(".search-item");
	for ( var i = 0;i< searchItem.length;i++) {
		if($(searchItem[i]).val() != ""){
			searchItems.push({"col":$(searchItem[i]).attr("name"),"value":$(searchItem[i]).val()});	
		}
	}
	return searchItems;
}

function drawListDefaultTable(data,container,event,params){
	if(data && data.rsData && data.rsData.length){
		gentPage({data:Math.ceil(data.rsCount / params.pageSize),cur:params.pageNo,p:container.find(".pagination"),event:event});
		data = dealNull(data.rsData);
		$.each(data, function(i, v){
			container.find('tbody').append(drawLine(v,container));
		});
	}else{
		container.find(".pagination").html("没有相关数据");
	}
}

function drawLine(v,container){
	var ths = container.find('thead').find("th,td");
	var tr = $('<tr iid="'+v.id+'"></tr>');
	for (var i = 0; i < ths.length; i++) {
		var th = $(ths[i]);
		var name = th.attr("data-he-name");
		var type = th.attr("data-he-type");
		var renderer = th.attr("data-he-renderer");
		var w = th.attr("data-he-w");
		var td = $("<td></td>");
		var text = getFormItemData(th,v,"data-he-name");
		if(type == "checkbox"){
			td.html('<input type="checkbox" value="'+text+'">');
		}else if(type == "radio"){
			td.html('<input type="radio" value="'+text+'" name="'+name+'">');			
		}else{
			if(type == "date"){
				text = formatDateTime(text);
			}else if(type == "link"){
				text = "<a href='"+text+"' target='_blank' title='"+text+"'>打开链接</a>";
			}
			if(renderer){
				text = eval(renderer)(text,v);	
			}
			if(w){
				td.css("width",w).html('<div style="width:'+w+'px" class="line-hidden" title="'+text+'"></div>').children().html(text);
			}else{
				td.html(text);
			}
		}
		tr.append(td);
	}
	var t = tr.data("data",v);
	return t;
}

function repaintLine($obj,data){
	var line = $obj.closest("tr");
	var orgData = line.data("data");
	$.extend(orgData,data);
	var container = line.closest(".ajaxtable-container");
	line.replaceWith(drawLine(orgData,container));
}

function redererIcon(className,icon,title){
	return $('<a href="javascript:void(0);" class="'+className+'"><i class="'+icon+'" title="'+title+'"></i></a>');
}
