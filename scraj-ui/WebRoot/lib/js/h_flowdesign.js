(function ($) {
    var defaults = {
        processData: {},
        onRepeatLink: function () {
            alert("步骤连接重复");
        },
        onClick: function () {

        },
        onDbClick: function () {

        },
        onDelete: function(){
            
        },
        canvasMenus: {
            "one": function (t) {
            }
        },
        processMenus: {
            "one": function (t) {
            }
        },
        debug: false,
        menuStyle: {
            border: '1px solid #5a6377',
            minWidth: '150px',
            padding: '5px 0'
        },
        itemStyle: {
            fontFamily: 'verdana',
            color: '#333',
            border: '0',
            padding: '5px 40px 5px 20px'
        },
        itemHoverStyle: {
            border: '0',
            color: '#fff',
            backgroundColor: '#5a6377'
        },
        mtAfterDrop: function (params) {
        },
        connectorPaintStyle: {
            lineWidth: 2,
            strokeStyle: "#49afcd",
            joinstyle: "round"
        },
        connectorHoverStyle: {
            lineWidth: 4,
            strokeStyle: "#328dcc"
        },
        connectorErrorStyle: {
            lineWidth: 2,
            strokeStyle: "#ee2525",
            dashstyle:"4 2"
        }
    };

    $.fn.Flowdesign = function (options) {
        var _canvas = $(this);
        var opts = $.extend(true,{},defaults,options);
        opts.aConnections = [];
        opts.activeId = 0;
        opts.copyId = 0;

        var contextmenu = {
            bindings: opts.canvasMenus,
            menuStyle: opts.menuStyle,
            itemStyle: opts.itemStyle,
            itemHoverStyle: opts.itemHoverStyle
        };

        $(this).contextMenu('canvasMenu', contextmenu);

        jsPlumb.importDefaults({
            DragOptions: {cursor: 'pointer'},
            EndpointStyle: {fillStyle: '#225588'},
            Endpoint: ["Dot", {radius: 1}],
            ConnectionOverlays: [
                ["Arrow", {location: 1, width:12, length:10, foldback:0.5}]
            ],
            Anchor: 'Continuous',
            ConnectorZIndex: 5,
            HoverPaintStyle: opts.connectorHoverStyle
        });

        if ($.browser && $.browser.msie && $.browser.version < '9.0') {
            jsPlumb.setRenderMode(jsPlumb.VML);
        } else {
            jsPlumb.setRenderMode(jsPlumb.SVG);
        }

        var _getProcessMatrix = function(p){
            var process = null;
            switch ($.type(p)){
                case "object":
                    process = p;
                    break;
                case "string":
                    process = $("#window" + p);
                    break;
            }
            var processMatrix = [];
            var pLeft = parseInt(process.css('left'));
            var pTop = parseInt(process.css('top'));
            var pWidth = process.width();
            var pHeight = process.height();
            processMatrix.push(pTop);
            processMatrix.push(pLeft + pWidth);
            processMatrix.push(pTop + pHeight);
            processMatrix.push(pLeft);
            return processMatrix;
        };

        var _getProcessesMatrix = function(){
            var processesMatrix = {};
            _canvas.find("div.process-step").each(function (i) {
                if ($(this).attr('id')) {
                    var processMatrix = {};
                    var pId = $(this).attr('process_id');
                    processMatrix = _getProcessMatrix($(this));
                    processesMatrix[pId] = processMatrix;
                }
            });
            return processesMatrix;
        };

        var _processRelation = function (s,t) {
            var sourceProcessMatrix = null;
            var targetProcessMatrix = null;
            switch ($.type(s)){
                case "array":
                    sourceProcessMatrix = s;
                    break;
                case "object":
                case "string":
                    sourceProcessMatrix = _getProcessMatrix(s);
                    break;
            }
            switch ($.type(t)){
                case "array":
                    targetProcessMatrix = t;
                    break;
                case "object":
                case "string":
                    targetProcessMatrix = _getProcessMatrix(t);
                    break;
            }
            if(sourceProcessMatrix && targetProcessMatrix){
                var x01 = sourceProcessMatrix[3];
                var x02 = sourceProcessMatrix[1];
                var y01 = sourceProcessMatrix[0];
                var y02 = sourceProcessMatrix[2];

                var x11 = targetProcessMatrix[3];
                var x12 = targetProcessMatrix[1];
                var y11 = targetProcessMatrix[0];
                var y12 = targetProcessMatrix[2];

                if(x01 > x11 && x02 < x12 && y01 > y11 && y02 < y12) {
                    return 1;//children
                }else if(x01 < x11 && x02 > x12 && y01 < y11 && y02 > y12) {
                    return -1;//parent
                }else{
                    var zx = Math.abs(x01+x02-x11-x12) ;
                    var x = Math.abs(x01-x02)+Math.abs(x11-x12) ;
                    var zy = Math.abs(y01+y02-y11-y12) ;
                    var y = Math.abs(y01-y02)+Math.abs(y11-y12) ;

                    if(zx <= x && zy <= y) {
                        return 2; //cross brother
                    }else {
                        return 0; //brother
                    }
                }

                return 1;
            }else{
                return -2;
            }
        };

        var _getElderProcesses = function (p){
            var processesMatrix = _getProcessesMatrix();
            var processMatrix = null;
            var pId = null;
            if($.type(p) == 'string'){
                processMatrix = processesMatrix[p];
                pId = p;
            } else{
                processMatrix = _getProcessMatrix(p);
                pId = p.attr("process_id");
            }
            var elderProcesses = [];
            $.each(processesMatrix,function(i, n){
                if(i!= pId && _processRelation(processMatrix,n) == 1){
                    elderProcesses.push($("#window" + i));
                }
            });
            return elderProcesses;
        };

        var _getParentProcess = function (p){
            var elderProcesses = _getElderProcesses(p);
            var parentProcess = null;
            $.each(elderProcesses,function (j,v) {
                var isParent = true;
                $.each(elderProcesses,function(i, n){
                    if(i!= j && _processRelation(v,n) == -1){
                        isParent = false;
                        return false;
                    }
                });
                if(isParent){
                    parentProcess = v;
                    return false;
                }
            });
            return parentProcess;
        };

        var _getJuniorProcesses = function (p){
            var processesMatrix = _getProcessesMatrix();
            var processMatrix = null;
            var pId = null;
            if($.type(p) == 'string'){
                processMatrix = processesMatrix[p];
                pId = p;
            } else{
                processMatrix = _getProcessMatrix(p);
                pId = p.attr("process_id");
            }
            var juniorProcesses = [];
            $.each(processesMatrix,function(i, n){
                if(i!= pId && _processRelation(processMatrix,n) == -1){
                    juniorProcesses.push($("#window" + i));
                }
            });
            return juniorProcesses;
        };

        var _getChildrenProcesses = function (p){
            var juniorProcesses = _getJuniorProcesses(p);
            var childrenProcesses = [];
            $.each(juniorProcesses,function (j,v) {
                var isChild = true;
                $.each(juniorProcesses,function(i, n){
                    if(i!= j && _processRelation(v,n) == 1){
                        isChild = false;
                        return false;
                    }
                });
                if(isChild){
                    childrenProcesses.push(v);
                }
            });
            return childrenProcesses;
        };

        var _getBrotherProcesses = function (p){
            var process = null;
            switch ($.type(p)){
                case "object":
                    process = p;
                    break;
                case "string":
                    process = $("#window" + p);
                    break;
            }
            var parentProcess = _getParentProcess(p);
            var tempProcesses = [];
            if(parentProcess != null){
                tempProcesses = _getChildrenProcesses(parentProcess);
            }else {
                var temp = _canvas.find("div.process-step");
                $.each(temp,function(i,n){
                    n = $(n);
                    if(!_isSubProcess(n)){
                        tempProcesses.push(n);
                    }
                });
            }
            var brotherProcesses = [];
            $.each(tempProcesses,function(i,n){
                n = $(n);
                if(n.attr("process_id") != process.attr("process_id")){
                    brotherProcesses.push(n);
                }
            });
            return brotherProcesses;
        };

        var _isBrotherProcess = function (p1,p2){
            var brotherProcesses = _getBrotherProcesses(p1);
            var process = null;
            switch ($.type(p2)){
                case "object":
                    process = p2;
                    break;
                case "string":
                    process = $("#window" + p2);
                    break;
            }
            var isBrother = false;
            $.each(brotherProcesses,function(i,n){
                n = $(n);
                if(n.attr("process_id") == process.attr("process_id")){
                    isBrother = true;
                    return false;
                }
            });
            return isBrother;
        };

        var _isSubProcess = function (p) {
            var processesMatrix = _getProcessesMatrix();
            var pId = null;
            switch ($.type(p)){
                case "object":
                    pId = p.attr("process_id");
                    break;
                default:
                    pId = p;
                    break;
            }
            var processMatrix = processesMatrix[pId];
            var isSub = false;
            $.each( processesMatrix, function(i, n){
                if(i!= pId && _processRelation(processMatrix,n) == 1){
                    isSub = true;
                    return false;
                }
            });
            return isSub;
        };

        var _createNode = function (row){
            var nodeDiv = document.createElement('div');
            var nodeId = "window" + row.id, icon = row.icon || 'glyphicon-random', resizeClass = row.resizable ? "process-resize" : "";
            $(nodeDiv).attr("id", nodeId)
                .attr("style", row.style)
                .attr("process_to", row.process_to)
                .attr("process_id", row.id)
                .data("process_data",row.process_data)
                .attr("process_resizable",row.resizable)
                .addClass("process-step " + resizeClass)
                .html('<span class="process-ban"><i class="glyphicon glyphicon-ban-circle"></i></span><span class="process-flag"><i class="glyphicon ' + icon + '"></i></span>' + row.process_name+"")
                .mousedown(function (e) {
                    if (e.which == 3 || e.which == 1) {
                        _activeNode(row.id);
                    }
                    if (e.which == 3) {
                        contextmenu.bindings = opts.processMenus;
                        $(this).contextMenu('processMenu', contextmenu);
                    }
                });
            return nodeDiv;
        };

        var _activeNode = function (p) {
            var process = null;
            switch ($.type(p)){
                case "object":
                    process = p;
                    break;
                case "string":
                default:
                    process = $("#window" + p);
                    break;
            }
            opts.activeId = process.attr("process_id");
            process.addClass("process-active").siblings(".process-step").removeClass("process-active");
        };

        var _validate = function (){
            var processesMatrix = _getProcessesMatrix();
            $.each( processesMatrix, function(j, v){
                var isError = false;
                $.each( processesMatrix, function(i, n){
                    if(i!= j){
                        var r = _processRelation(v,n);
                        switch (r){
                            case -1:
                                break;
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                $("#window"+i).addClass("process-step-error");
                                isError = true;
                                break;
                            default:
                                break;
                        }
                    }
                });
                if(isError){
                    $("#window"+j).addClass("process-step-error");
                }else{
                    $("#window"+j).removeClass("process-step-error");
                }
            });
        };

        var _moveProcess = function(process,dLeft,dTop){
            var oLeft = parseInt(process.attr('oLeft'));
            var oTop = parseInt(process.attr('oTop'));
            var st = _canvas.scrollTop();
            var sl = _canvas.scrollLeft();
            process.css({left:(oLeft+sl+dLeft)+"px",top:(oTop+st+dTop)+"px"});
            jsPlumb.repaint(process.attr("id"));
        };

        var _initNodeEvent = function ($objs) {
            jsPlumb.draggable($objs,{ start:function(event,ui){
                // console.log(this);
                _canvas.find("div.process-step").each(function (i) {
                    if ($(this).attr('id')) {
                        $(this).attr('oLeft',$(this).position().left);
                        $(this).attr('oTop',$(this).position().top);
                    }
                });
            },drag:function(event,ui){
                var dLeft = ui.position.left - ui.originalPosition.left;
                var dTop = ui.position.top - ui.originalPosition.top;
                var that = this;
                var pId = $(this).attr('process_id');
                var processesMatrix = _getProcessesMatrix();
                var processMatrix = processesMatrix[pId];
                var processToMove = [];
                $.each( processesMatrix, function(i, n){
                    if(i!= pId){
                        var r = _processRelation(processMatrix,n);
                        switch (r){
                            case -1:
                                $("#window" + i).addClass("process-move");
                                break;
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            default:
                                break;
                        }
                    }
                });
                $(".process-step.on").addClass("process-move");
                $(".process-step.process-move").each(function(i){
                    _moveProcess($(this),dLeft,dTop);
                });
                _validate();
                var id = $(this).attr("id");
                var cs = $.merge(jsPlumb.getConnections({source:id}),jsPlumb.getConnections({target:id}));
                $.each(cs,function(i,n){
                    if(!_isBrotherProcess($("#"+n.sourceId),$("#"+n.targetId))){
                        n.setType("error");
                    }else{
                        n.setType("normal");
                    }
                });
            },stop:function(event,ui){
                $(".process-step.process-move").removeClass("process-move");
                if(opts.debug){
                    var id = $(this).attr("id");
                    var cs = $.merge(jsPlumb.getConnections({source:id}),jsPlumb.getConnections({target:id}));
                    $.each(cs,function(i,n){
                        if(n.hasType("error")){
                            jsPlumb.detach(n);
                        }
                    });
                }
                //_resizeCanvas();
            }
                
            });
            _canvas.find("div.process-resize").resizable({resize: function( event, ui ) {
                _validate();
                var id = $(this).attr("id");
                jsPlumb.repaint(id);
            }});

            $objs.find(".process-flag").each(function (i, e) {
                var p = $(e).parent();
                jsPlumb.makeSource($(e), {
                    parent: p,
                    anchor: "Continuous",
                    endpoint: ["Dot", {radius: 1}],
                    connector: ["Flowchart", {stub: [5, 5]}],
                    connectorStyle: opts.connectorPaintStyle,
                    hoverPaintStyle: opts.connectorHoverStyle,
                    dragOptions: {},
                    maxConnections: -1
                });
            });

            jsPlumb.makeTarget($objs, {
                dropOptions: {hoverClass: "hover", activeClass: "active"},
                anchor: "Continuous",
                maxConnections: -1,
                endpoint: ["Dot", {radius: 1}],
                paintStyle: {fillStyle: "#ec912a", radius: 1},
                hoverPaintStyle: this.connectorHoverStyle,
                beforeDrop: function (params) {
                    if (params.sourceId == params.targetId) return false;
                    var j = 0;
                    var conn = opts.conn || [];
                    for(var i = 0; i<conn.length;i++){
                        var str = $('#' + params.sourceId).attr('process_id') + ',' + $('#' + params.targetId).attr('process_id');
                        var str1 = $('#' + params.targetId).attr('process_id') + ',' + $('#' + params.sourceId).attr('process_id');
                        if (str == conn[i] || str1 == conn[i]) {
                            j++;
                            break;
                        }
                    }
                    if (j > 0) {
                        opts.onRepeatLink();
                        return false;
                    } else {
                        var sourceId = $("#" + params.sourceId).attr('process_id');
                        var targetId = $("#" + params.targetId).attr('process_id');

                        if(_isBrotherProcess(sourceId,targetId)){
                            opts.mtAfterDrop({
                                sourceId: sourceId,
                                targetId: targetId
                            });
                            return true;
                        }else{
                            return false;
                        }
                    }
                }
            });
        };

        var _resizeCanvas = function(){
            var pMatrix = _getProcessesMatrix();
            var minTop;
            var minLeft;
            $.each(pMatrix,function(k,v){
                minTop = minTop ? minTop > v[0] ? v[0] : minTop : v[0];
                minLeft = minLeft ? minLeft > v[3] ? v[3] : minLeft : v[3];
            });
            var h = _canvas.height();
            var w = _canvas.width();
            var st = _canvas.scrollTop();
            var sl = _canvas.scrollLeft();
            if(minTop < 0){
                _canvas.find("div.process-step").each(function (i) {
                    if ($(this).attr('id')) {
                        var t = parseInt($(this).css("top"));
                        $(this).css("top",t + h + "px")
                    }
                });
                _canvas.scrollTop(st + h);
            }else if( minTop > h ){
                _canvas.find("div.process-step").each(function (i) {
                    if ($(this).attr('id')) {
                        var t = parseInt($(this).css("top"));
                        $(this).css("top",t - minTop + h + "px")
                    }
                });
                _canvas.scrollTop(st - minTop + h);
            }
            if(minLeft < 0){
                _canvas.find("div.process-step").each(function (i) {
                    if ($(this).attr('id')) {
                        var t = parseInt($(this).css("left"));
                        $(this).css("left",t + w + "px")
                    }
                });
                _canvas.scrollLeft(sl + w);
            }else if( minLeft > w ){
                _canvas.find("div.process-step").each(function (i) {
                    if ($(this).attr('id')) {
                        var t = parseInt($(this).css("left"));
                        $(this).css("left",t - minLeft + w + "px")
                    }
                });
                _canvas.scrollLeft(sl - minLeft + w);
            }
            if(minTop < 0 || minLeft < 0 || minTop > h || minLeft > w){
                jsPlumb.repaintEverything();
            }
        }

        var _setConnections = function (conn, remove) {
            if (!remove) opts.aConnections.push(conn);
            else {
                var idx = -1;
                for (var i = 0; i < opts.aConnections.length; i++) {
                    if (opts.aConnections[i] == conn) {
                        idx = i;
                        break;
                    }
                }
                if (idx != -1) opts.aConnections.splice(idx, 1);
            }
            if (opts.aConnections.length > 0) {
                var arr = [];
                for (var j = 0; j < opts.aConnections.length; j++) {
                    var from = $('#' + opts.aConnections[j].sourceId).attr('process_id');
                    var target = $('#' + opts.aConnections[j].targetId).attr('process_id');
                    arr.push(from + "," + target);
                }
                opts.conn = arr;
            } else {
                opts.conn = [];
            }
            jsPlumb.repaintEverything();
        };

        var _reOrderJunior = function (p) {
            var process = null;
            switch ($.type(p)){
                case "object":
                    process = p;
                    break;
                case "string":
                    process = $("#window" + p);
                    break;
            }
            var children = _getChildrenProcesses(process);
            $.each(children,function(i,n){
                process.parent().append(n);
                _reOrderJunior(n);
            });
        };

        var _getOriginProcesses = function () {
            var ps = [];
            _canvas.find("div.process-step").each(function (i) {
                if (!_isSubProcess($(this))) {
                    ps.push($(this));
                }
            });
            return ps;
        }

        var _nodeRelation = function(){
            var pi = _processBA();
            var tarr = [];
            for(it in pi){
                $.each(pi[it],function(k,v){
                    var o = {};
                    o[it] = v;
                    tarr.push(o);
                });
            }
            var r = [];
            var op = _getOriginProcesses();
            function gentRelation(rn,p){
                var id = p.attr("process_id");
                var ri = {};
                ri[id] = [];
                rn.push(ri);
                var cp = _getChildrenProcesses(p);
                $.each(cp,function (i,v) {
                    gentRelation(ri[id],v);
                });
                if(ri[id].length > 1){
                    ri[id] = resetOrder(ri[id]);
                }
            }

            function resetOrder(rs){
                var container = $("<div></div>");
                $.each(rs,function(i,v){
                    for(k in v){
                        var o = $("<i id='"+k+"'></i>").data("data",v[k]);
                        container.append(o);
                    }
                });
                $.each(tarr,function(i,v){
                    for(k in v){
                        var kv = v[k];
                        console.log("#"+k);
                        var kd = container.find("#"+k);
                        if(kd.length){
                            if(!kd.next("#"+kv).length){
                                var s = container.find("#"+kv).addClass("fixed").index();
                                var e = kd.index();
                                for(var i = s + 1;i<e;i++){
                                    if(!$(container.find("i").get(i)).hasClass("fixed")){
                                        e = i;
                                    }
                                }
                                if(s > 0){
                                    kd.after(container.find("i:gt("+(s-1)+"):lt("+e+")"));
                                }else{
                                    kd.after(container.find("i:lt("+e+")"));
                                }
                                console.log(container);
                            }else{
                                kd.next("#"+kv).addClass("fixed");
                            }
                        }else{
                            console.log("存在错误连线");
                        }
                    }
                });
                var rt = [];
                container.find("i").each(function(i){
                    var k = $(this).attr("id");
                    var v = $(this).data("data");
                    var o = {};
                    o[k] = v;
                    rt.push(o);
                });
                return rt;
            }

            $.each(op,function (i,v) {
                gentRelation(r,v);
            });
            r = resetOrder(r);
            return r;
        };

        var _processBA = function(){
            var aProcessData = {};
            var conn = opts.conn || [];
            for(var i = 0;i<conn.length;i++){
                var processVal = conn[i].split(",");
                if (processVal.length == 2) {
                    if (!aProcessData[processVal[0]]) {
                        aProcessData[processVal[0]] = [];
                    }
                    aProcessData[processVal[0]].push(processVal[1]);
                }
            }
            return aProcessData;
        };

        var _processInfo = function(){
                var aProcessData = _processBA();
                var processData = [];
                _canvas.find("div.process-step").each(function (i) {
                    if ($(this).attr('id')) {
                        var pId = $(this).attr('process_id');
                        var d = {};
                        d.id = pId;
                        d.process_name = $.trim($(this).text());
                        d.resizable = $(this).attr('process_resizable') == "true";
                        d.process_data = $(this).data('process_data');
                        d.process_to = aProcessData[pId] ? aProcessData[pId].toString() : "";
                        d.style =  $(this).attr("style");
                        processData.push(d);
                    }
                });
                return processData;
        };

        var _delProcess = function(p){
            var pId = null;
            switch ($.type(p)){
                case "object":
                    pId = $(p).attr("process_id");
                    break;
                case "string":
                    pId = p;
                    break;
            }
            if (pId <= 0) return false;
            jsPlumb.detachAllConnections("window" + pId);
            $("#window" + pId).remove();
            return true;
        };

        var processData = opts.processData;

        if (processData) {
            $.each(processData, function (i, row) {
                var nodeDiv = _createNode(row);
                _canvas.append(nodeDiv);
                _reOrderJunior($(nodeDiv));
            });
        }

        var timeout = null;

        _canvas.on('click', ".process-step", function () {
            clearTimeout(timeout);
            var obj = this;
            timeout = setTimeout(opts.onClick, 300);
        }).on('dblclick', ".process-step", function () {
            clearTimeout(timeout);
            opts.onDbClick();
        }).on('mousedown', ".process-step", function () {
            _reOrderJunior($(this));
            _canvas.children("._jsPlumb_endpoint,svg").appendTo(_canvas);
        }).areaSelect({
            sisClass:"process-step",onClass:"on",shiftKey:false,onRightClick:function(event,objs){},onItemSelectedChange:function(objs){}
        });

        jsPlumb.bind("jsPlumbConnection", function (info) {
            _setConnections(info.connection)
        });
        jsPlumb.bind("jsPlumbConnectionDetached", function (info) {
            _setConnections(info.connection, true);
        });
        jsPlumb.bind("click", function (c) {
            if (confirm("你确定取消连接吗?"))
                jsPlumb.detach(c);
        });

        jsPlumb.registerConnectionType("normal", {
            connector: ["Flowchart", {stub: [5, 5]}],
            paintStyle: opts.connectorPaintStyle,
            hoverPaintStyle: opts.connectorHoverStyle,
            overlays: [
                ["Arrow", {location: 1, width:12, length:10, foldback:0.5}]
            ]
        });

        jsPlumb.registerConnectionType("error", {
            connector: ["Flowchart", {stub: [5, 5]}],
            paintStyle: opts.connectorErrorStyle,
            hoverPaintStyle: opts.connectorHoverStyle,
            overlays: [
                ["Arrow", {location: 1, width:12, length:10, foldback:0.5}],
                ["Label", {
                    location: 0.5,
                    cssClass: "aLabel glyphicon glyphicon-remove"
                }]
            ]
        });

        _initNodeEvent(jsPlumb.getSelector(".process-step"));

        var _canvas_design = function () {
            $('.process-step').each(function (i) {
                var sourceId = $(this).attr('process_id');
                var prcsto = $(this).attr('process_to');
                var toArr = prcsto.split(",");
                var processData = opts.processData;
                $.each(toArr, function (j, targetId) {

                    if (targetId != '' && targetId != 0) {
                        var is_source = false, is_target = false;
                        $.each(processData, function (i, row) {
                            if (row.id == sourceId) {
                                is_source = true;
                            } else if (row.id == targetId) {
                                is_target = true;
                            }
                            if (is_source && is_target)
                                return true;
                        });

                        if (is_source && is_target) {
                            jsPlumb.connect({
                                source: "window" + sourceId,
                                target: "window" + targetId
                            });
                            return;
                        }
                    }
                })
            });

            _canvas.prepend("<div id='canvas-bg'></div>");
        };

        _canvas_design();

        var Flowdesign = {
            addProcess: function (row) {
                if (row.id <= 0) {
                    return false;
                }
                var nodeDiv = _createNode(row);
                _canvas.append(nodeDiv);
                _initNodeEvent($(nodeDiv));
                return nodeDiv;
            },
            selectNode:function(p){
                _activeNode(p);
            },
            validate:function(){
                _validate();
            },
            delProcess: function (p) {
                return _delProcess(p);
            },
            isSubProcess: function (p) {
                return _isSubProcess(p);
            },
            delProcesses: function (p) {
                var hasError = true;
                $.each(p,function (i,v) {
                    hasError = hasError === false ? false : _delProcess(v);
                });
                return hasError;
            },
            getActiveId: function () {
                return opts.activeId;
            },
            copy: function (active_id) {
                if (!active_id)
                    active_id = opts.activeId;
                opts.copyId = active_id;
                return true;
            },
            paste: function () {
                return opts.copyId;
            },
            // resizeCanvas: function () {
            //     _resizeCanvas();
            // },
            getProcessInfo: function () {
                return _processInfo();
            },
            getProcessRelation: function () {
                return _nodeRelation();
            },
            clear: function () {
                try {
                    jsPlumb.detachEveryConnection();
                    jsPlumb.deleteEveryEndpoint();
                    opts.conn = [];
                    jsPlumb.repaintEverything();
                    $(".process-step").remove();
                    return true;
                } catch (e) {
                    return false;
                }
            },
            clearAllLink: function () {
                try {
                    jsPlumb.detachEveryConnection();
                    jsPlumb.deleteEveryEndpoint();
                    opts.conn = [];
                    jsPlumb.repaintEverything();
                    return true;
                } catch (e) {
                    return false;
                }
            },
            linkClear: function (activeId) {
                if (activeId <= 0) return false;
                jsPlumb.detachAllConnections("window" + activeId);
                return true;
            },
            refresh: function () {
                try {
                    this.clear();
                    _canvas_design();
                    return true;
                } catch (e) {
                    return false;
                }
            }
        };

        return Flowdesign;
    }
})(jQuery);