var _canvas = null;

$(function(){
    var param = gentParams();
    window.lastGetFormat = param.tt;
    kgLoader({
        url: SERVICE_URL_PREFIX + "sfe/formatters?"+$.param(param),
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastGetFormat) {
                window.components = genComponents(genFormatter(data));
                pageInit();
            }
        },
        that: $("body")[0]
    });
});

function pageInit(){
    var m = getDecodeParam("m");
    if(m == "conf"){
        $("#cmFlowTplSave").addClass("hide");
        $("#cmSave").addClass("hide");
        $("#cmTmpSave").addClass("hide");
    }else if(m == "flow"){
        $("#cmConfTplSave").addClass("hide");
        $("#cmSave").addClass("hide");
        $("#cmTmpSave").addClass("hide");
        $("#right-block").addClass("hide");
    }else{
        $("#cmConfTplSave").addClass("hide");
        $("#cmFlowTplSave").addClass("hide");
    }
    var id = getDecodeParam("id");
    if(id){
        if(m == "conf"){
            initConfTpl(id,function(data){
                initCanvas(data);
            });
        }else if(m == "flow"){
            initFlowTpl(id,function(data){
                initCanvas(data);
            });
        }else{
            initConfig(id,function(data){
                initCanvas(data);
            });
        }
    }else{
        initCanvas();
    }

    $("#template-type").find("li[data-template-type]" ).on("click",function(event){
        if(!$(this).hasClass("active")){
            $(this).addClass("active").siblings(".active").removeClass("active");
            if($(this).attr("data-template-type") == "conf"){
                initConfTemplate(1);
            }else{
                initFlowTemplate(1);
            }
        }
        event.stopPropagation()
    });

    $(".card-box-template>ul").on("click","li",function(event){
        var id = $(this).attr("tid");
        var d = $(this).parent().data("data")[id];
        addConfirmDialog("是否使用模板["+d.name+"]?",function (that,$dlg) {
           // console.log(d);
            closeThisDialog($dlg);
            var data = $("body").data("data");
            data.conf = d.conf;
            data.confGraph = d.confGraph;
            if(_canvas){
                _canvas.clear();
            }
            initCanvas(data);
        });
    });
}

function mAlert(messages,s){
    var alertModal = $('#alertModal');
    if(!messages) messages = "";
    if(!s) s = 2000;
    alertModal.find(".modal-body").html(messages);
    alertModal.modal('toggle');
    setTimeout(function(){alertModal.modal("hide")},s);
}

function initCanvas(data){
    $("body").data("data",data);
    var processData = [];
    var graphType = getDecodeParam("t") || 0;

    if(data && data.confGraph){
        try {
            var confGraph = JSON.parse(data.confGraph);
            processData = confGraph.graph;
            if(processData.length){
                if(getDecodeParam("t") && getDecodeParam("t") != confGraph.type){
                    mAlert("爬虫类型与已存在组件类型不服，请修改后再操作",2000);
                }
                graphType = confGraph.type;
            }else{
                graphType = getDecodeParam("t") || confGraph.type;
            }
        }catch (e){

        }
    }

    initComponents(graphType);

    function initComponents(type){
        var lis = "";
        window.schemas = {};

        var components = type == 1 ? window.components.dynamic:window.components.static;
        $.each(components,function (k,v) {
            lis += '<li data-component-type="'+( v.innerCommands.length ? 1 : 0)+'" data-component-name="'+v.title+'">\
			<i class="'+v.icon+'"></i>\
			'+v.description+'\
			</li>';
            window.schemas[v.title] = v;
        })
        $(".card-box-component ul").html(lis);

        $( ".card-box-component li" ).draggable({
            helper: "clone"
        });

        $("#component-type").find("li[data-graph-type='"+type+"']" ).addClass("active").siblings(".active").removeClass("active");
    }

    $("#component-type").find("li[data-graph-type]" ).off("click").on("click",function(event){
        if(!$(this).hasClass("active")){
            if($("#max-content").find(".process-step").length == 0){
                if(getDecodeParam("t") && $(this).attr("data-graph-type") != getDecodeParam("t")){
                    mAlert("请先将爬虫类型修改后再操作?",2000);
                }else{
                    $(this).addClass("active").siblings(".active").removeClass("active");
                    initComponents($(this).attr("data-graph-type"));
                }
            }else{
                mAlert("静态组件和动态组件不能混用",2000);
            }
        }
        event.stopPropagation()
    });

    $( "#max-content" ).droppable({
        hoverClass: "content-active",
        accept: ":not(.process-step)",
        drop: function( event, ui ) {
            var text = ui.draggable.text();
            var mLeft = (ui.position.left-$(this).offset().left+$(this).scrollLeft())+"px";
            var mTop = (ui.position.top-$(this).offset().top+$(this).scrollTop())+"px";
            var componentType = ui.draggable.data("componentType");
            var componentName = ui.draggable.data("componentName");
            var wh = "";
            if(componentType == 0){
                wh = "width:120px;height:35px;";
            }else if(componentType == 1){
                wh = "width:300px;height:200px;";
            }
            var processId = new Date().getTime();
            var itemData = {"id":processId,"process_data":{"classid":componentName,"prop":{}},"process_name":text,"process_to":"","resizable":componentType == 1,"style":"left:"+mLeft+";top:"+mTop+";"+wh};
            var node = _canvas.addProcess(itemData);
            _canvas.selectNode(processId);
            _canvas.validate();
            //_canvas.resizeCanvas();
        }
    });

    var previewStep = function(endId){
        var processInfo = _canvas.getProcessInfo();
        var tmp = {};
        var map = {};
        $.each(processInfo,function(i,n){
            var id = n.id;
            var $p = $("#window"+id);
            var prop = $p.data("process_data").prop || null;
            tmp[id] = prop;
            map[id] = n.process_data.classid;
        });
        var relation = _canvas.getProcessRelation();
        var r = [];
        function gentRelation(rx,arr,data){
            for(var i = 0;i<arr.length;i++){
                var n = arr[i];
                var k = '';
                for(k in n){
                    var o  = {};
                    o[map[k]] = data[k];
                    rx.push(o);
                    if(n[k].length){
                        o[map[k]].innerCommands = [];
                        gentRelation(o[map[k]].innerCommands,n[k],data);
                    }
                }
                if(endId == k){
                    break;
                }
            }
        }
        gentRelation(r,relation,tmp);
        var type = $("li.active[data-graph-type]" ).attr("data-graph-type");
        var url = "preview/static";
        if(type == 1){
            url = "preview/dynamic";
        }
        var formData = {};
        formData.conf = r;
        var param = gentParams();
        window.lastPreviewStep = param.tt;
        kgLoader({
            type:1,
            url: SERVICE_URL_PREFIX + url + '?' + $.param(param),
            params:formData,
            success: function (data, state, XHR, params) {
                if (data && params.tt == window.lastPreviewStep) {
                    addDetailDialog('预览结果',data,null,1000);
                }
            },
            that: $("body")[0]
        });
    };

    var showAttr = function (id) {
        var $item = $("#window"+id);
        var processData = $item.data("process_data") || {};
        var componentName = processData.classid;
        $(".j-e[data-component-name='"+componentName+"']").show().siblings(".j-e").hide();
        var v = window.schemas[componentName];
        var d = processData.prop || {};
        var je = $('<div class="j-e scroll-1" data-component-name="'+v.title+'"></div>');
        var button = _canvas.isSubProcess(id) ? "" : '<button class="btn btn-default fr" id="p-'+id+'" style="margin-right: 20px;">预览</button>';
        $("#right-block .right-block").html(je).prepend('<div class="box-title">'+v.description+button+'</div>');

        $("#p-"+id).on("click",function (event) {
            previewStep(id);
        });

        var jeditor = loadJSONEditor(je[0],v,d);

        jeditor.on('change',function() {
            processData.prop = jeditor.getValue();
            $item.data("process_data",processData);
        });

        function loadJSONEditor($editor,schema,startval){
            JSONEditor.defaults.options.theme = 'bootstrap3';
            JSONEditor.defaults.options.iconlib = 'fontawesome4';
            JSONEditor.defaults.options.required_by_default = true;
            JSONEditor.defaults.options.disable_properties = true;
            JSONEditor.defaults.options.disable_collapse = true;
            JSONEditor.defaults.options.disable_edit_json = true;
            JSONEditor.defaults.options.disable_array_reorder = true;
            // JSONEditor.defaults.options.object_layout = "grid";


            var jsoneditor = new JSONEditor($editor,{
                schema: schema,
                startval: startval
            });

            return jsoneditor;
        }
    };

    var gentGraphInfo = function(){
        var processInfo = _canvas.getProcessInfo();
        var tmp = {};
        var map = {};
        $.each(processInfo,function(i,n){
            var id = n.id;
            var $p = $("#window"+id);
            var prop = $p.data("process_data").prop || null;
            tmp[id] = prop;
            map[id] = n.process_data.classid;
        });
        var relation = _canvas.getProcessRelation();
        var r = [];
        function gentRelation(rx,arr,data){
            $.each(arr,function(i,n){
                for(k in n){
                    var o  = {};
                    o[map[k]] = data[k];
                    rx.push(o);
                    if(n[k].length){
                        o[map[k]].innerCommands = [];
                        gentRelation(o[map[k]].innerCommands,n[k],data);
                    }
                }
            });
        }
        gentRelation(r,relation,tmp);
        var confGraph = {};
        confGraph.type = $("li.active[data-graph-type]" ).attr("data-graph-type");
        confGraph.graph = processInfo;
        return {r:JSON.stringify(r),confGraph:JSON.stringify(confGraph)};
    };

    var saveConf = function (t,tag){
        var graphInfo = gentGraphInfo();
        var formData = {};
        formData.id = getDecodeParam("id");
        formData.conf = graphInfo.r;
        formData.confGraph = graphInfo.confGraph;
        formData.completed = tag;
        var param = gentParams();
        window.lastSaveConf = param.tt;
        kgLoader({
            type:1,
            url: SERVICE_URL_PREFIX + "modifyConf",
            params:formData,
            success: function (data, state, XHR, params) {
                if (data && params.tt == window.lastSaveConf) {
                    $("body").data("data",data);
                    mAlert("success");
                }
            },
            that: $("body")[0]
        });
    };

    var saveConfTpl = function (t){
        var m = getDecodeParam("m");
        var orgData = $("body").data("data");
        if(m == "conf" && orgData){
            modify(orgData);
        }else{
            var html = "<div style='padding:20px 10px 10px'><input id='save-name' class='form-control' placeholder='请输入模版名?'></div>";
            var params = {};
            params.title = "保存配置模板";
            params.width = 420;
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
            btn.text = "保存";
            params.buttons.push(btn);
            params.content = html;
            var dlg = gentSysDialog(params);
        }

        function save(that,$dlg){
            var graphInfo = gentGraphInfo();
            var formData = {};
            formData.name = $dlg.find("#save-name").val();
            formData.conf = graphInfo.r;
            formData.confGraph = graphInfo.confGraph;
            var param = gentParams();
            window.lastSaveConfTpl = param.tt;
            kgLoader({
                type:1,
                url: SERVICE_URL_PREFIX + "template/SaveConfigTemplate",
                params:orgData,
                success: function (data, state, XHR, params) {
                    if (data && params.tt == window.lastSaveConfTpl) {
                        $("body").data("data",data);
                        mAlert("success");
                    }
                },
                that: $("body")[0]
            });
        }

        function modify(orgData){
            var graphInfo = gentGraphInfo();
            orgData.conf = graphInfo.r;
            orgData.confGraph = graphInfo.confGraph;
            var param = gentParams();
            window.lastModifyConfTpl = param.tt;
            kgLoader({
                type:1,
                url: SERVICE_URL_PREFIX + "template/ModifyConfigTemplate",
                params:orgData,
                success: function (data, state, XHR, params) {
                    if (data && params.tt == window.lastModifyConfTpl) {
                        $("body").data("data",data);
                        mAlert("success");
                    }
                },
                that: $("body")[0]
            });
        }
    };

    var saveFlowTpl = function (t){
        var m = getDecodeParam("m");
        var orgData = $("body").data("data");
        if(m == "flow" && orgData){
            modify(orgData);
        }else{
            var html = "<div style='padding:20px 10px 10px'><input id='save-name' class='form-control' placeholder='请输入模版名?'></div>";
            var params = {};
            params.title = "保存流程模板";
            params.width = 420;
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
            btn.text = "保存";
            params.buttons.push(btn);
            params.content = html;
            var dlg = gentSysDialog(params);
        }

        function save(that,$dlg){
            var graphInfo = gentGraphInfo();
            var formData = {};
            formData.name = $dlg.find("#save-name").val();
            formData.conf = graphInfo.r;
            formData.confGraph = graphInfo.confGraph;
            var param = gentParams();
            window.lastSaveFlowTpl = param.tt;
            kgLoader({
                type:1,
                url: SERVICE_URL_PREFIX + "template/SaveFlowTemplate",
                params:orgData,
                success: function (data, state, XHR, params) {
                    if (data && params.tt == window.lastSaveFlowTpl) {
                        $("body").data("data",data);
                        mAlert("success");
                    }
                },
                that: $("body")[0]
            });
        }

        function modify(orgData){
            var graphInfo = gentGraphInfo();
            orgData.conf = graphInfo.r;
            orgData.confGraph = graphInfo.confGraph;
            var param = gentParams();
            window.lastModifyFlowTpl = param.tt;
            kgLoader({
                type:1,
                url: SERVICE_URL_PREFIX + "template/ModifyFlowTemplate",
                params:orgData,
                success: function (data, state, XHR, params) {
                    if (data && params.tt == window.lastModifyFlowTpl) {
                        $("body").data("data",data);
                        mAlert("success");
                    }
                },
                that: $("body")[0]
            });
        }
    };

    _canvas = $("#max-content").Flowdesign({
        "processData":processData
        ,canvasMenus:{
            "cmTmpSave": function(t) {
                saveConf(t,0);
            },
            "cmSave": function(t) {
                saveConf(t,1);
            },
            "cmConfTplSave": function(t) {
                saveConfTpl(t);
            },
            "cmFlowTplSave": function(t) {
                saveFlowTpl(t);
            },
            "cmClear": function(t) {
                if(confirm("你确定清除所有连线吗?"))
                {
                    _canvas.clear();
                }
            },
            "cmClearSelect": function(t) {
                if(confirm("你确定清除所有选中的步骤吗?"))
                {
                    var p = $(".process-step.on");
                    _canvas.delProcesses(p);
                }
            },
            "cmRefresh":function(t){
                location.reload();//_canvas.refresh();
            }
        }
        ,processMenus: {
            "pmDelete":function(t)
            {
                if(confirm("你确定删除步骤吗?"))
                {
                    var activeId = _canvas.getActiveId();//右键当前的ID
                    if(_canvas.delProcess(activeId)){
                        $("#right-block .right-block").html("");
                    }
                }
            },
            "pmAttribute":function(t)
            {
                var activeId = _canvas.getActiveId();
                showAttr(activeId);
            },
            "pmClearSelect": function(t) {
                if(confirm("你确定清除所有选中的步骤吗?"))
                {
                    var p = $(".process-step.on");
                    _canvas.delProcesses(p);
                }
            },
            "pmClear":function(t)
            {
                if(confirm("你确定清除该步骤的所有连线吗?"))
                {
                    var activeId = _canvas.getActiveId();
                    _canvas.linkClear(activeId);
                }
            }

        }
        ,onRepeatLink:function(){

        }
        ,onClick:function(){
            var activeId = _canvas.getActiveId();
            showAttr(activeId);
        }
        ,onDbClick:function(){
            var activeId = _canvas.getActiveId();
            showAttr(activeId);
        }
    });

    initFlowTemplate(1);
}

function initConfig(id,callback){
    var param = gentParams();
    window.lastInitConfig = param.tt;
    kgLoader({
        url: SERVICE_URL_PREFIX + "getConf/"+id+"?"+$.param(param),
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastInitConfig) {
                callback(data);
            }
        },
        that: $("body")[0]
    });
}

function initConfTpl(id,callback){
    var param = gentParams();
    param.id = id;
    window.lastInitConfTpl = param.tt;
    kgLoader({
        url: SERVICE_URL_PREFIX + "template/GetIdConfigTemplate?"+$.param(param),
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastInitConfTpl) {
                callback(data);
            }
        },
        that: $("body")[0]
    });
}

function initFlowTpl(id,callback){
    var param = gentParams();
    param.id = id;
    window.lastInitFlowTpl = param.tt;
    kgLoader({
        url: SERVICE_URL_PREFIX + "template/GetIdFlowTemplate?"+$.param(param),
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastInitFlowTpl) {
                callback(data);
            }
        },
        that: $("body")[0]
    });
}

function initConfTemplate(page){
    var param = gentParams(page);
    param.confType = getConfType();
    window.lastInitConfTemplate = param.tt;
    kgLoader({
        url: SERVICE_URL_PREFIX + "template/GetConfigTemplate?"+$.param(param),
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastInitConfTemplate) {
                gentPage({data:Math.ceil(data.size / params.pageSize),cur:params.pageNo,p:$(".card-box-template .pagination"),event:"initConfTemplate("});
                var lis = "";
                var d = {};
                $.each(data.data,function(i,n){
                    lis += "<li tid='"+n.id+"' class='line-hidden'>"+n.name+"</li>";
                    d[n.id] = n;
                });
                $(".card-box-template>ul").html(lis).data("data",d);
            }
        },
        that: $(".card-box-template .pagination")[0]
    });
}

function initFlowTemplate(page){
    var param = gentParams(page);
    param.confType = getConfType();
    window.lastInitFlowTemplate = param.tt;
    kgLoader({
        url: SERVICE_URL_PREFIX + "template/GetFlowTemplate?"+$.param(param),
        success: function (data, state, XHR, params) {
            if (data && params.tt == window.lastInitFlowTemplate) {
                gentPage({data:Math.ceil(data.size / params.pageSize),cur:params.pageNo,p:$(".card-box-template .pagination"),event:"initFlowTemplate("});
                var lis = "";
                var d = {};
                $.each(data.data,function(i,n){
                    lis += "<li tid='"+n.id+"' class='line-hidden'>"+n.name+"</li>";
                    d[n.id] = n;
                });
                $(".card-box-template>ul").html(lis).data("data",d);
            }
        },
        that: $(".card-box-template .pagination")[0]
    });
}

function getConfType(){
    return $(".active[data-graph-type]").attr("data-graph-type");
}