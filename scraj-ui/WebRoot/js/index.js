var heDD = {
    "completed": {
        0: "否",
        1: "是"
    },
    "confType": {
        0: "静态",
        1: "动态"
    },
    "intervalType":{
        0:"周期型",
        1:"定点型"
    }
};

var taskModel = {
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
    },
    "intervalTime": {
        "type": "integer",
        "title":"间隔时间",
        "list": {
            "show":true
        },
        "editor":{
            "show":true,
            "required": true,
            "format":"number"
        }
    },
    "intervalExp": {
        "type": "string",
        "title":"周期表达式",
        "list": {
            "show":true
        },
        "editor":{
            "show":true,
            "required": true
        }
    },
    "intervalType": {
        "type": "integer",
        "title":"	间断类型",
        "options":{
            0:"周期型",
            1:"定点型"
        },
        "list": {
            "show":true
        },
        "editor":{
            "show":true,
            "required": true
        }
    },
    "cookie": {
        "type": "string",
        "title":"cookie",
        "editor":{
            "show":true,
            "required": true,
            "format":"textarea"
        }
    },
    "confType": {
        "type": "integer",
        "title":"爬虫类型",
        "options": {
            0: "静态",
            1: "动态"
        },
        "list": {
            "show":true
        },
        "editor":{
            "show":true,
            "required": true
        }
    },
    "confPriority": {
        "type": "integer",
        "title":"优先级",
        "options": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
        "list": {
            "show":true
        },
        "editor":{
            "show":true,
            "required": true
        }
    },
    "confParallel": {
        "type": "integer",
        "title":"并行化",
        "options": [0, 1, 2],
        "list": {
            "show":true
        },
        "editor":{
            "show":true,
            "required": true
        }
    },
    "completed": {
        "type": "integer",
        "title":"配置完成",
        "options": {
            0: "否",
            1: "是"
        },
        "list": {
            "show":true
        },
        "editor":{
            "show":true,
            "required": true
        }
    },
    "conf": {
        "type": "string",
        "title":"配置"
    },
    "confGraph": {
        "type": "string",
        "title":"图形"
    },
    "switchFlag": {
        "type": "integer",
        "title":"开始状态",
        "options": {
            0: "关",
            1: "开"
        }
    }
};

$(function () {
    getDataList(1);
});

function changeIntervalType(that) {
    var form = $(that).closest("form");
    if(that.value == 1 ){
        form.find("#task-intervalTime").closest(".form-group").addClass("hide");
        form.find("#task-intervalExp").closest(".form-group").removeClass("hide");
    }else{
        form.find("#task-intervalTime").closest(".form-group").removeClass("hide");
        form.find("#task-intervalExp").closest(".form-group").addClass("hide");
    }
}

function createTask() {
    var html = $("#new-task-box").html();
    var params = {};
    params.title = "新建任务";
    params.width = 600;
    params.buttons = [];
    var btn2 = {};
    btn2.cls = "btn-cancel";
    btn2.text = " 取 消 ";
    params.buttons.push(btn2);
    var btn = {};
    btn.event = function (that,$dlg,event) {
        save(that,$dlg);
    };
    btn.cls = "btn-create";
    btn.text = " 保 存 ";
    params.buttons.push(btn);
    var btn3 = {};
    btn3.event = function (that,$dlg,event) {
        save(that,$dlg,true);
    };
    btn3.cls = "btn-create";
    btn3.text = " 保存并配置 ";
    params.buttons.push(btn3);
    params.content = html;
    var dlg = gentSysDialog(params);
    validate(dlg.find("form"));

    function save(that,$dlg,config){
        $dlg.find("form").isValid(function(v) {
            if (v) {
                var data = getFormData($dlg);
                var param = gentParams();
                window.lastCreateTask = param.tt;
                kgLoader({
                    type: 1,
                    url: SERVICE_URL_PREFIX + "save?" + $.param(param),
                    params: data,
                    success: function (data, state, XHR, params) {
                        if (data && params.tt == window.lastCreateTask) {
                            setFormData($dlg, {});
                            if (config) {
                                var id = data.id;
                                goWebPage("graph.html?id=" + id + "&t="+data.confType);
                            }
                            closeThisDialog($dlg);
                        }
                    },
                    that: that
                });
            }
        });
    }
}

function getDataList(page, that) {
    var container = $(".ajaxtable-container[data-method='getDataList']");
    var param = gentParams(page);
    window.lastGetDataList = param.tt;
    container.find('tbody').empty();
    kgLoader({
        url: SERVICE_URL_PREFIX + "infoList/"+param.pageNo+"/"+param.pageSize+"?"+$.param(param),
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastGetDataList) {
                data = data || {};
                data.rsCount = data.size;
                data.rsData = data.data;
                drawListDefaultTable(data, container, "getDataList(", params);
            }
        },
        that: container.find(".pagination")[0]
    });
}

function rendererCompleted(v) {
    return heDD.completed[parseInt(v)];
}

function rendererConfType(v) {
    return heDD.confType[parseInt(v)];
}

function rendererIntervalType(v) {
    return heDD.intervalType[parseInt(v)];
}

function gentOptions(text, v) {
    var start = redererIcon("item-start", "fa fa-play fa-fw", "启动").on("click", startTask);
    if (v.switchFlag == 1) {
        start = redererIcon("item-stop", "fa fa-stop fa-fw", "停止").on("click", stopTask);
    }
    var modify = redererIcon("item-modify", "fa fa-pencil fa-fw", "修改").on("click", modifyTask);
    var conf = redererIcon("item-conf", "fa fa-gear fa-fw", "修改配置").on("click", confTask);
    // 20170215
    var del = redererIcon("item-delete", "fa fa-trash-o fa-fw", "删除").on("click", deleteTask);
    var html = $("<div></div>").append(start).append(modify).append(conf).append(del);
    return html;
}

function startTask(event) {
    var d = $(this).closest("tr").data("data");
    if(d.completed == 1){
        updateTaskStatus(this,1);
    }else{
        addAlertDialog("请先配置完成后启动");
    }
}

function stopTask(event) {
    updateTaskStatus(this,0);
}

function modifyTask(event) {
    var button = event.target;
    var html = $("#new-task-box").html();
    var params = {};
    params.title = "修改任务";
    params.width = 600;
    params.buttons = [];
    var btn2 = {};
    btn2.cls = "btn-cancel";
    btn2.text = " 取 消 ";
    params.buttons.push(btn2);
    var btn = {};
    btn.event = function (that,$dlg,event) {
        save(that,$dlg,button);
    };
    btn.cls = "btn-create";
    btn.text = " 修 改 ";
    params.buttons.push(btn);
    var btn3 = {};
    btn3.event = function (that,$dlg,event) {
        save(that,$dlg,button,true);
    };
    btn3.cls = "btn-create";
    btn3.text = " 完成并修改配置 ";
    params.buttons.push(btn3);
    params.content = html;
    var dlg = gentSysDialog(params);
    var d = $(button).closest("tr").data("data");
    setFormData( dlg,d);
    dlg.find("#task-intervalType").trigger("change");
    validate(dlg.find("form"));
    function save(that,$dlg,button,config) {
        $dlg.find("form").isValid(function (v) {
            if (v) {
                var formData = getFormData($dlg);
                var param = gentParams();
                window.lastModifyTask = param.tt;
                kgLoader({
                    type: 1,
                    url: SERVICE_URL_PREFIX + "modifyInfo?" + $.param(param),
                    params: formData,
                    success: function (data, state, XHR, params) {
                        if (data && params.tt == window.lastModifyTask) {
                            repaintLine($(button), formData);
                            setFormData($dlg, {});
                            if (config) {
                                var id = data.id;
                                goWebPage("graph.html?id=" + id + "&t="+data.confType);
                            }
                            closeThisDialog($dlg);
                        }
                    },
                    error: function () {
                        repaintLine($(button), formData);
                        closeThisDialog($dlg);
                    },
                    that: that
                });
            }
        });
    }
}

function confTask(event) {
    var d = $(event.target).closest("tr").data("data");
    goWebPage("graph.html?id="+d.id + "&t="+d.confType);
}

function updateTaskStatus(that,switchFlag){
    var d = $(that).closest("tr").data("data");
    var data = {};
    data.id = d.id;
    data.switchFlag = switchFlag;
    var param = gentParams();
    window.lastUpdateTaskStatus = param.tt;
    kgLoader({
        type:1,
        url: SERVICE_URL_PREFIX + "control?"+$.param(param),
        params:data,
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastUpdateTaskStatus) {
                repaintLine($(that),{switchFlag:data.switchFlag});
            }
        },
        that: that
    });
}

// 20170215 
function deleteTask(event) {
	var d = $(event.target).closest("tr").data("data");
    addAlertDialog("确认删除任务["+d.name+"]吗?",function(that,$dlg){
    	var param = {"tt" : new Date().getTime()};
        window.lastDelFlowTpl = param.tt;
        kgLoader({
            type: 1,
            url: SERVICE_URL_PREFIX + "delete/" + d.id + "?" + $.param(param),
            success: function (data, state, XHR, params) {
                if (data && params.tt == window.lastDelFlowTpl) {
                	$(event.target).closest("tr").remove();// remove or reload?
                    closeThisDialog($dlg);
                }
            },
            that: that
        });
    });
}

function validate(form){
    form.validator({
        theme:"default",
        fields: {
            'name':'required;'
        }
    });
}