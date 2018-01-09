var heDD = {
    "confType": {
        0: "静态",
        1: "动态"
    }
};
$(function () {
    getDataList(1);
});

function createFlowTpl() {
    var html = $("#new-flow-box").html();;
    var params = {};
    params.title = "新建流程模板";
    params.width = 450;
    params.buttons = [];
    var btn2 = {};
    btn2.cls = "btn-cancel";
    btn2.text = "取消";
    params.buttons.push(btn2);
    var btn = {};
    btn.event = function (that,$dlg,event) {
        save(that,$dlg);
    };
    btn.cls = "btn-create";
    btn.text = "创建";
    params.buttons.push(btn);

    params.content = html;
    var dlg = gentSysDialog(params);
    validate(dlg.find("form"));

    function save(that,$dlg){
        $dlg.find("form").isValid(function(v) {
            if (v) {
                var data = getFormData($dlg);
                var param = gentParams();
                window.lastCreateFlowTpl = param.tt;
                kgLoader({
                    type: 1,
                    url: SERVICE_URL_PREFIX + "template/SaveFlowTemplate?" + $.param(param),
                    params: data,
                    success: function (data, state, XHR, params) {
                        if (data && params.tt == window.lastCreateFlowTpl) {
                            setFormData($dlg, {});
                            var id = data.id;
                            goWebPage("graph.html?m=flow&id=" + id + "&t="+data.confType);
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
    var sItems = getSearchItem(container);
    console.log(sItems);
    var param = gentParams(page);
    $.extend(param,sItems);
    window.lastGetDataList = param.tt;
    container.find('tbody').empty();
    kgLoader({
        url: SERVICE_URL_PREFIX + "template/GetFlowTemplate?"+$.param(param),
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

function gentOptions(text, v) {
    var del = redererIcon("item-del", "fa fa-trash-o fa-fw", "删除").on("click", delFlowTpl);
    var modify = redererIcon("item-modify", "fa fa-pencil fa-fw", "修改").on("click", modifyFlowTpl);
    var conf = redererIcon("item-conf", "fa fa-gear fa-fw", "修改配置").on("click", confFlowTpl);
    var html = $("<div></div>").append(del).append(modify).append(conf);
    return html;
}

function rendererConfType(v) {
    return heDD.confType[parseInt(v)];
}

function delFlowTpl(event) {
    var d = $(event.target).closest("tr").data("data");
    addAlertDialog("确认删除模板吗?"+d.name+"?",function(that,$dlg){
        var param = gentParams();
        var formData = {};
        formData.id = d.id;
        window.lastDelFlowTpl = param.tt;
        kgLoader({
            type: 1,
            url: SERVICE_URL_PREFIX + "template/DeleteFlowTemplate?" + $.param(param),
            params: formData,
            success: function (data, state, XHR, params) {
                if (data && params.tt == window.lastDelFlowTpl) {
                    $(event.target).closest("tr").remove();
                    closeThisDialog($dlg);
                }
            },
            that: that
        });
    });
}

function modifyFlowTpl(event) {
    var button = event.target;
    var html = $("#new-flow-box").html();
    var params = {};
    params.title = "修改流程模板";
    params.width = 450;
    params.buttons = [];
    var btn2 = {};
    btn2.cls = "btn-cancel";
    btn2.text = "取消";
    params.buttons.push(btn2);
    var btn = {};
    btn.event = function (that,$dlg,event) {
        save(that,$dlg,button);
    };
    btn.cls = "btn-create";
    btn.text = "修改";
    params.buttons.push(btn);
    params.content = html;
    var dlg = gentSysDialog(params);
    var d = $(button).closest("tr").data("data");
    setFormData( dlg,d);
    validate(dlg.find("form"));
    function save(that,$dlg,button) {
        $dlg.find("form").isValid(function (v) {
            if (v) {
                var formData = getFormData($dlg);
                var param = gentParams();
                window.lastModifyFlowTpl = param.tt;
                kgLoader({
                    type: 1,
                    url: SERVICE_URL_PREFIX + "template/ModifyFlowTemplate?" + $.param(param),
                    params: formData,
                    success: function (data, state, XHR, params) {
                        if (data && params.tt == window.lastModifyFlowTpl) {
                            repaintLine($(button), formData);
                            setFormData($dlg, {});
                            closeThisDialog($dlg);
                        }
                    },
                    that: that
                });
            }
        });
    }
}

function confFlowTpl(event) {
    var d = $(event.target).closest("tr").data("data");
    goWebPage("graph.html?m=flow&id="+d.id + "&t="+d.confType);
}

function validate(form){
    form.validator({
        theme:"default",
        fields: {
            'name':'required;'
        }
    });
}