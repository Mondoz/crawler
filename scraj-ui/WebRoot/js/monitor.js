var heDD = {

};

var monitorModel = {
    "name": {
        "type": "string",
        "title":"名称",
        "list": {
            "show":true
        },
        "editor":{
            "show":true,
            "required": true
        }
    }

};

$(function () {
    getDataList(1);
});

function getDataList(page, that) {
    var container = $(".ajaxtable-container[data-method='getDataList']");
    var sItems = getSearchItem(container);
    console.log(sItems);
    var param = gentParams(page);
    $.extend(param,sItems);
    window.lastGetDataList = param.tt;
    container.find('tbody').empty();
    kgLoader({
        url: SERVICE_URL_PREFIX + "monitor/"+param.pageNo+"/"+param.pageSize+"?"+$.param(param),
        success: function (data, state, XHR, params) {
            if (params.tt == window.lastGetDataList) {
                data = data || {};
                data.rsCount = data.size;
                data.rsData = data.data;
                drawListDefaultTable(data, container, "getDataList(", params);
            }
        },
        that: container.find(".pagination")[0]
    });
}

function viewTask(event){
    var d = $(event.target).closest("tr").data("data");
    var param = gentParams(1,10);
    window.lastViewTask = param.tt;
    kgLoader({
        type:0,
        url: SERVICE_URL_PREFIX + 'monitor/'+d._id+'/data' + '?' + $.param(param),
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastViewTask) {
                addDetailDialog('预览',data.rsData,null,1000);
            }
        },
        that: $("body")[0]
    });
}

function gentOptions(text, v) {
    // var start = redererIcon("item-start", "fa fa-play fa-fw", "启动").on("click", startTask);
    // if (v.switchFlag == 1) {
    //     start = redererIcon("item-stop", "fa fa-stop fa-fw", "停止").on("click", stopTask);
    // }
    var view = redererIcon("item-view", "fa fa-eye fa-fw", "查看").on("click", viewTask);
    // var conf = redererIcon("item-conf", "fa fa-gear fa-fw", "修改配置").on("click", confTask);
    var html = $("<div></div>").append(view);//.append(modify).append(conf);
    return html;
}