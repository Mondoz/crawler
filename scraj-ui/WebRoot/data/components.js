/**/
var dedup = {
    "$schema": "http://json-schema.org/draft-04/schema#",
    "title": "dedup",
    "type": "object",
    "icon": "fa fa-code-fork",
    "description": "列表排重",
    "innerCommands": [],
    "properties": {
        "fields": {
            "title": "排重字段",
            "type": "array",
            "items": {
                "type": "string"
            }
        },
        "mappingFields": {
            "title": "映射字段",
            "type": "array",
            "items": {
                "type": "string"
            }
        },
        "dedupMeta": {
            "title": "排重元信息",
            "type": "object",
            "options": { "keep_oneof_values": false },
            "oneOf": [{
                "title": "mongodb",
                "type": "object",
                "properties": {
                    "engine": { "type": "string", "enum": ["mongodb"], "options": { "hidden": true } },
                    "host": {
                        "type": "string",
                        "default": "localhost"
                    },
                    "port": {
                        "type": "integer",
                        "format": "number",
                        "default": 27017
                    },
                    "user": {
                        "type": "string"
                    },
                    "password": {
                        "type": "string"
                    },
                    "database": {
                        "type": "string"
                    },
                    "collection": {
                        "type": "string"
                    }
                },
                "additionalProperties": false
            }, {
                "title": "redis",
                "type": "object",
                "properties": {
                    "engine": { "type": "string", "enum": ["redis"], "options": { "hidden": true } },
                    "host": {
                        "type": "string",
                        "default": "localhost"
                    },
                    "port": {
                        "type": "integer",
                        "format": "number",
                        "default": 6379
                    },
                    "dbIndex": {
                        "type": "integer",
                        "format": "number",
                        "default": 0
                    },
                    "key": {
                        "type": "string"
                    }
                },
                "additionalProperties": false
            }, {
                "title": "jdbc",
                "type": "object",
                "properties": {
                    "engine": { "type": "string", "enum": ["jdbc"], "options": { "hidden": true } },
                    "driver": {
                        "type": "string"
                    },
                    "url": {
                        "type": "string"
                    },
                    "user": {
                        "type": "string"
                    },
                    "password": {
                        "type": "string"
                    },
                    "table": {
                        "type": "string"
                    }
                },
                "additionalProperties": false
            }]
        }

    }
};

/**/
var store = {
    "$schema": "http://json-schema.org/draft-04/schema#",
    "title": "store",
    "type": "object",
    "icon": "fa fa-database",
    "description": "数据存储",
    "innerCommands": [],
    "properties": {
        "fields": {
            "title": "待转字段",
            "type": "array",
            "items": {
                "type": "string"
            }
        },
        "mappingFields": {
            "title": "映射字段",
            "type": "array",
            "items": {
                "type": "string"
            }
        },
        "storeMeta": {
            "title": "存储元信息",
            "type": "object",
            "options": { "keep_oneof_values": false },
            "oneOf": [{
                "title": "mongodb",
                "type": "object",
                "properties": {
                    "engine": { "type": "string", "enum": ["mongodb"], "options": { "hidden": true } },
                    "host": {
                        "type": "string",
                        "default": "localhost"
                    },
                    "port": {
                        "type": "integer",
                        "format": "number",
                        "default": "27017"
                    },
                    "user": {
                        "type": "string"
                    },
                    "password": {
                        "type": "string"
                    },
                    "database": {
                        "type": "string"
                    },
                    "collection": {
                        "type": "string"
                    }
                },
                "additionalProperties": false
            }, {
                "title": "elasticsearch",
                "type": "object",
                "properties": {
                    "engine": { "type": "string", "enum": ["elasticsearch"], "options": { "hidden": true } },
                    "host": {
                        "type": "string",
                        "default": "localhost"
                    },
                    "port": {
                        "type": "integer",
                        "format": "number",
                        "default": 9300
                    },
                    "clusterName": {
                        "type": "string"
                    },
                    "index": {
                        "type": "string"
                    },
                    "type": {
                        "type": "string"
                    }
                },
                "additionalProperties": false
            }, {
                "title": "jdbc",
                "type": "object",
                "properties": {
                    "engine": { "type": "string", "enum": ["jdbc"], "options": { "hidden": true } },
                    "driver": {
                        "type": "string"
                    },
                    "url": {
                        "type": "string"
                    },
                    "user": {
                        "type": "string"
                    },
                    "password": {
                        "type": "string"
                    },
                    "table": {
                        "type": "string"
                    }
                },
                "additionalProperties": false
            }, {
                "title": "api",
                "type": "object",
                "properties": {
                    "engine": { "type": "string", "enum": ["api"], "options": { "hidden": true } },
                    "url": {
                        "type": "string"
                    },
                    "params": {
                        "type": "string"
                    },
                    "query": {
                        "type": "string",
                        "format": "textarea"
                    }
                },
                "additionalProperties": false
            }]
        }

    }
};

/**/
function genComponents(formatter) {
    var components = {
        "static": [{
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticOpen",
                "type": "object",
                "icon": "fa fa-external-link",
                "description": "打开网址",
                "innerCommands": [],
                "properties": {
                    "urls": {
                        "title": "网址",
                        "type": "array",
                        "uniqueItems": true,
                        "items": {
                            "title": "url",
                            "type": "string"
                        }
                    },
                    "requestType": {
                        "title": "请求方式",
                        "type": "string",
                        "enum": [
                            "get", "post"
                        ],
                        "default": "get"
                    },
                    "charset": {
                        "title": "编码",
                        "type": "string",
                        "enum": ["", "gbk", "gb2312", "utf-8", "gb18030", "iso-8859-1"]
                    },
                    "params": {
                        "title": "请求参数 格式:k1=v1;k2=v2",
                        "type": "string",
                        "format": "textarea"
                    },
                    "formatter": formatter
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticPagination",
                "type": "object",
                "icon": "fa fa-repeat",
                "description": "循环翻页",
                "innerCommands": [
                    "staticFetchKV",
                    "staticFetchKVs",
                    "staticFetchList"
                ],
                "properties": {
                    "maxPage": {
                        "title": "最大页数",
                        "type": "integer",
                        "format": "number",
                        "default": 5
                    },
                    "batchPage": {
                        "title": "单次处理页数",
                        "type": "integer",
                        "format": "number",
                        "default": 5
                    },
                    "splitPage": {
                        "title": "单任务处理页数",
                        "type": "integer",
                        "format": "number",
                        "default": 20
                    },
                    "sleepMillis": {
                        "title": "每页停顿时间(单位：毫秒)",
                        "type": "integer",
                        "format": "number",
                        "default": 1000
                    },
                    "requestType": {
                        "title": "请求方式",
                        "type": "string",
                        "enum": [
                            "get",
                            "post"
                        ],
                        "default": "get"
                    },
                    "params": {
                        "title": "请求参数",
                        "type": "string"
                    },
                    "paginationMeta": {
                        "title": "翻页元信息",
                        "type": "object",
                        "options": { "keep_oneof_values": false },
                        "oneOf": [{
                            "title": "rule",
                            "properties": {
                                "strategy": { "type": "string", "enum": ["rule"], "options": { "hidden": true } },
                                "paginationUrls": { "title": "翻页url", "type": "array", "items": { "type": "string" } },
                                "paginationRule": { "title": "计算下一页码的表达式", "type": "string" },
                                "numFromPage": { "title": "从页面提取数字", "type": "string" },
                                "stdLength": { "title": "页码标准长度", "type": "integer", "format": "number" },
                                "leftAlign": { "title": "左边补全的字符", "type": "string" },
                                "rightAlign": { "title": "右边补全的字符", "type": "string" }
                            }
                        }, {
                            "title": "next",
                            "properties": {
                                "strategy": { "type": "string", "enum": ["next"], "options": { "hidden": true } },
                                "byType": { "title": "查找方式", "type": "string", "enum": ["byCss", "byRegex", "byXpath"] },
                                "typeValue": { "title": "查找表达式", "type": "string" },
                                "formatter": formatter
                            }
                        }]
                    }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticFetchKV",
                "type": "object",
                "icon": "fa fa-angle-right",
                "description": "提取单值",
                "innerCommands": [],
                "properties": {
                    "field": {
                        "title": "字段名",
                        "type": "string"
                    },
                    "byType": {
                        "title": "查找方式",
                        "type": "string",
                        "enum": [
                            "byCss",
                            "byRegex",
                            "byXpath",
                            "byConstant"
                        ]
                    },
                    "typeValue": {
                        "title": "查找表达式",
                        "type": "string"
                    },
                    "byAttr": {
                        "title": "提取属性[innerText/innerHTML/outerHTML/tagName...]",
                        "type": "string"
                    },
                    "group": {
                        "title": "group",
                        "type": "integer",
                        "format": "number",
                        "default": 0
                    },
                    "valueType": {
                        "title": "提取值的类型",
                        "type": "string",
                        "enum": [
                            "string",
                            "integer",
                            "long",
                            "float",
                            "double"
                        ]
                    },
                    "formatter": formatter
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticFetchKVs",
                "type": "object",
                "icon": "fa fa-angle-double-right",
                "description": "提取多值",
                "innerCommands": [],
                "properties": {
                    "includeSource": {
                        "title": "保存源码",
                        "type": "boolean",
                        "format": "checkbox"
                    },
                    "fetchValueWithKeys": {
                        "title": "提取键值",
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "field": {
                                    "title": "字段名",
                                    "type": "string"
                                },
                                "byType": {
                                    "title": "查找方式",
                                    "type": "string",
                                    "enum": [
                                        "byCss",
                                        "byRegex",
                                        "byXpath",
                                        "byConstant"
                                    ]
                                },
                                "typeValue": {
                                    "title": "查找表达式",
                                    "type": "string"
                                },
                                "byAttr": {
                                    "title": "提取属性[innerText/innerHTML/outerHTML/tagName...]",
                                    "type": "string"
                                },
                                "group": {
                                    "title": "group",
                                    "type": "integer",
                                    "format": "number",
                                    "default": 0
                                },
                                "valueType": {
                                    "title": "提取值的类型",
                                    "type": "string",
                                    "enum": [
                                        "string",
                                        "integer",
                                        "long",
                                        "float",
                                        "double"
                                    ]
                                },
                                "formatter": formatter
                            }
                        }
                    }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticFetchList",
                "type": "object",
                "icon": "fa fa-clone",
                "description": "提取列表",
                "innerCommands": [
                    "staticFetchKV",
                    "staticFetchKVs"
                ],
                "properties": {
                    "field": { "title": "字段名", "type": "string" },
                    "byType": { "title": "查找方式", "type": "string", "enum": ["byCss", "byRegex", "byXpath"] },
                    "typeValue": { "title": "查找表达式", "type": "string" }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticHandleList",
                "type": "object",
                "icon": "fa fa-list",
                "description": "处理列表",
                "innerCommands": [
                    "staticFetchKV",
                    "staticFetchKVs",
                    "staticFetchList"
                ],
                "properties": {
                    "sleepMillis": {
                        "title": "每条之间停顿时间",
                        "type": "integer",
                        "format": "number",
                        "default": 1000
                    }
                }
            },


            dedup,

            store
        ],

        /* */
        "dynamic": [{
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserOpen",
                "type": "object",
                "icon": "fa fa-chain",
                "description": "动态打开网址",
                "innerCommands": [],
                "properties": {
                    "urls": {
                        "title": "网址",
                        "type": "array",
                        "uniqueItems": true,
                        "items": {
                            "title": "url",
                            "type": "string"
                        }
                    },
                    "formatter": formatter
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticOpen",
                "type": "object",
                "icon": "fa fa-external-link",
                "description": "静态打开网址",
                "innerCommands": [],
                "properties": {
                    "urls": {
                        "title": "网址",
                        "type": "array",
                        "uniqueItems": true,
                        "items": {
                            "title": "url",
                            "type": "string"
                        }
                    },
                    "requestType": {
                        "title": "请求方式",
                        "type": "string",
                        "enum": [
                            "get", "post"
                        ],
                        "default": "get"
                    },
                    "params": {
                        "title": "请求参数",
                        "type": "string",
                        "format": "textarea"
                    },
                    "formatter": formatter
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserTypeText",
                "type": "object",
                "icon": "fa fa-terminal",
                "description": "输入文本",
                "innerCommands": [],
                "properties": {
                    "byType": {
                        "title": "查找方式",
                        "type": "string",
                        "enum": [
                            "byCss", "byClass", "byId", "byTagName",
                            "byName", "byLinkText", "byPartialLinkText",
                            "byXpath", "byJavaScript"
                        ],
                        "default": "byCss"
                    },
                    "typeValue": {
                        "title": "查找表达式",
                        "type": "string"
                    },
                    "text": {
                        "title": "待输入的文本",
                        "type": "string"
                    },
                    "waitMillis": {
                        "title": "等待完成时间(单位：毫秒)",
                        "type": "integer",
                        "format": "number",
                        "default": 1000
                    }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserClick",
                "type": "object",
                "icon": "fa fa-mouse-pointer",
                "description": "单击元素",
                "innerCommands": [],
                "properties": {
                    "byType": {
                        "title": "查找方式",
                        "type": "string",
                        "enum": [
                            "byCss", "byId", "byClass", "byTagName",
                            "byName", "byLinkText", "byPartialLinkText",
                            "byXpath", "byJavaScript"
                        ],
                        "default": "byCss"
                    },
                    "typeValue": {
                        "title": "查找表达式",
                        "type": "string"
                    },
                    "waitMillis": {
                        "title": "等待完成时间(单位：毫秒)",
                        "type": "integer",
                        "format": "number",
                        "default": 1000
                    }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserScroll",
                "type": "object",
                "icon": "fa fa-arrow-down",
                "description": "滚动页面",
                "innerCommands": [],
                "properties": {
                    "scrollScript": {
                        "title": "js滚动脚本",
                        "type": "string"
                    },
                    "arguments": {
                        "title": "脚本参数",
                        "type": "string"
                    },
                    "waitMillis": {
                        "title": "等待完成时间(单位：毫秒)",
                        "type": "integer",
                        "format": "number",
                        "default": 1000
                    }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserPagination",
                "type": "object",
                "icon": "fa fa-repeat",
                "description": "循环翻页",
                "innerCommands": [
                    "browserFetchKV",
                    "browserFetchKVs",
                    "browserFetchList"
                ],
                "properties": {
                    "maxPage": {
                        "title": "最大页数",
                        "type": "integer",
                        "format": "number"
                    },
                    "sleepMillis": {
                        "title": "每页之间停顿时间",
                        "type": "integer",
                        "format": "number",
                        "default": 1000
                    },
                    "waitMillis": {
                        "title": "等待完成时间(单位：毫秒)",
                        "type": "integer",
                        "format": "number",
                        "default": 1000
                    },
                    "strategy": {
                        "title": "翻页方式",
                        "type": "string",
                        "enum": ["more", "scroll", "next"]
                    },
                    "byType": {
                        "title": "查找方式",
                        "type": "string",
                        "enum": [
                            "byCss", "byId", "byClass", "byTagName",
                            "byName", "byLinkText", "byPartialLinkText",
                            "byXpath", "byJavaScript"
                        ]
                    },
                    "typeValue": {
                        "title": "查找表达式",
                        "type": "string"
                    }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserFetchKV",
                "type": "object",
                "icon": "fa fa-play",
                "description": "动态提取单值",
                "innerCommands": [],
                "properties": {
                    "field": {
                        "title": "字段名",
                        "type": "string"
                    },
                    "byType": {
                        "title": "查找方式",
                        "type": "string",
                        "enum": [
                            "byCss", "byId", "byClass", "byTagName",
                            "byName", "byLinkText", "byPartialLinkText",
                            "byXpath", "byJavaScript"
                        ],
                        "default": "byCss"
                    },
                    "typeValue": {
                        "title": "查找表达式",
                        "type": "string"
                    },
                    "byAttr": {
                        "title": "提取属性[innerText/innerHTML/outerHTML/tagName...]",
                        "type": "string"
                    },
                    "group": {
                        "title": "group",
                        "type": "integer",
                        "format": "number"
                    },
                    "valueType": {
                        "title": "提取值的类型",
                        "type": "string",
                        "enum": [
                            "string", "integer", "long", "float", "double"
                        ]
                    },
                    "formatter": formatter
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticFetchKV",
                "type": "object",
                "icon": "fa fa-angle-right",
                "description": "静态提取单值",
                "innerCommands": [],
                "properties": {
                    "field": {
                        "title": "字段名",
                        "type": "string"
                    },
                    "byType": {
                        "title": "查找方式",
                        "type": "string",
                        "enum": [
                            "byCss", "byXpath", "byRegex", "byConstant"
                        ],
                        "default": "byCss"
                    },
                    "typeValue": {
                        "title": "查找表达式",
                        "type": "string"
                    },
                    "byAttr": {
                        "title": "提取属性[innerText/innerHTML/outerHTML/tagName...]",
                        "type": "string"
                    },
                    "group": {
                        "title": "group",
                        "type": "integer",
                        "format": "number"
                    },
                    "valueType": {
                        "title": "提取值的类型",
                        "type": "string",
                        "enum": [
                            "string",
                            "integer",
                            "long",
                            "float",
                            "double"
                        ]
                    },
                    "formatter": formatter
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserFetchKVs",
                "type": "object",
                "icon": "fa fa-forward",
                "description": "动态提取多值",
                "innerCommands": [],
                "properties": {
                    "fetchValueWithKeys": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "field": {
                                    "title": "字段名",
                                    "type": "string"
                                },
                                "byType": {
                                    "title": "查找方式",
                                    "type": "string",
                                    "enum": [
                                        "byCss", "byId", "byClass", "byTagName",
                                        "byName", "byLinkText", "byPartialLinkText",
                                        "byXpath", "byJavaScript"
                                    ],
                                    "default": "byCss"
                                },
                                "typeValue": {
                                    "title": "查找表达式",
                                    "type": "string"
                                },
                                "byAttr": {
                                    "title": "提取属性[innerText/innerHTML/outerHTML/tagName...]",
                                    "type": "string"
                                },
                                "group": {
                                    "title": "group",
                                    "type": "integer",
                                    "format": "number"
                                },
                                "valueType": {
                                    "title": "提取值的类型",
                                    "type": "string",
                                    "enum": [
                                        "string",
                                        "integer",
                                        "long",
                                        "float",
                                        "double"
                                    ]
                                },
                                "formatter": formatter
                            }
                        }
                    }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticFetchKVs",
                "type": "object",
                "icon": "fa fa-angle-double-right",
                "description": "静态提取多值",
                "innerCommands": [],
                "properties": {
                    "includeSource": {
                        "title": "保存源码",
                        "type": "boolean",
                        "format": "checkbox"
                    },
                    "fetchValueWithKeys": {
                        "title": "提取键值",
                        "type": "array",
                        "items": {
                            "title": "键值对",
                            "type": "object",
                            "properties": {
                                "field": {
                                    "title": "字段名",
                                    "type": "string"
                                },
                                "byType": {
                                    "title": "查找方式",
                                    "type": "string",
                                    "enum": [
                                        "byCss",
                                        "byRegex",
                                        "byXpath",
                                        "byConstant"
                                    ]
                                },
                                "typeValue": {
                                    "title": "查找表达式",
                                    "type": "string"
                                },
                                "byAttr": {
                                    "title": "提取属性[innerText/innerHTML/outerHTML/tagName...]",
                                    "type": "string"
                                },
                                "group": {
                                    "title": "group",
                                    "type": "integer",
                                    "format": "number"
                                },
                                "valueType": {
                                    "title": "提取值的类型",
                                    "type": "string",
                                    "enum": [
                                        "string",
                                        "integer",
                                        "long",
                                        "float",
                                        "double"
                                    ]
                                },
                                "formatter": formatter
                            }
                        }
                    }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserFetchList",
                "type": "object",
                "icon": "fa fa-object-ungroup",
                "description": "动态提取列表",
                "innerCommands": [
                    "browserFetchKV",
                    "browserFetchKVs"
                ],
                "properties": {
                    "field": { "title": "字段名", "type": "string" },
                    "byType": {
                        "title": "查找方式",
                        "type": "string",
                        "enum": ["byCss", "byId", "byClass", "byTagName",
                            "byName", "byLinkText", "byPartialLinkText",
                            "byXpath", "byJavaScript"
                        ]
                    },
                    "typeValue": { "title": "查找表达式", "type": "string" }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "staticFetchList",
                "type": "object",
                "icon": "fa fa-clone",
                "description": "静态提取列表",
                "innerCommands": [
                    "staticFetchKV",
                    "staticFetchKVs"
                ],
                "properties": {
                    "field": { "title": "字段名", "type": "string" },
                    "byType": { "title": "查找方式", "type": "string", "enum": ["byCss", "byRegex", "byXpath"] },
                    "typeValue": { "title": "查找表达式", "type": "string" }
                }
            },

            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "browserHandleList",
                "type": "object",
                "icon": "fa fa-list",
                "description": "处理列表",
                "innerCommands": [
                    "staticFetchKV",
                    "staticFetchKVs",
                    "staticFetchList"
                ],
                "properties": {
                    "sleepMillis": {
                        "title": "每条之间停顿时间",
                        "type": "integer",
                        "format": "number"
                    }
                }
            },

            dedup,

            store
        ]
    };

    return components;
}


/**/
function genFormatter(defaultFormatters) {
    var formatter = {
        "title": "格式化",
        "type": "object",
        "properties": {
            "defaults": {
                "title": "系统格式器",
                "type": "array",
                "format": "tabs",
                "items": {
                    "title": "格式器",
                    "type": "string",
                    "enumSource": [{
                        "source": defaultFormatters,
                        "title": "{{item.title}}",
                        "value": "{{item.value}}"
                    }]
                }
            },
            "customs": {
                "title": "自定义格式",
                "type": "array",
                "format": "tabs",
                "items": {
                    "title": "格式器",
                    "type": "object",
                    "options": { "keep_oneof_values": false },
                    "oneOf": [{
                        "title": "过滤",
                        "properties": {
                            "type": { "type": "string", "enum": ["sfe4j.filter"], "options": { "hidden": true } },
                            "name": { "title": "名字", "type": "string" },
                            "regex": { "title": "正则表达式", "type": "string" },
                            "useMulti": { "title": "过滤全部", "type": "boolean", "format": "checkbox" }
                        }
                    }, {
                        "title": "查找",
                        "properties": {
                            "type": { "type": "string", "enum": ["sfe4j.finder"], "options": { "hidden": true } },
                            "name": { "title": "名字", "type": "string" },
                            "regex": { "title": "正则表达式", "type": "string" },
                            "group": { "title": "group", "type": "integer", "default": 0, "format": "number" },
                            "useMulti": { "title": "查找全部", "type": "boolean", "format": "checkbox" },
                            "multiSeparator": { "title": "结果分隔符", "type": "string", "default": ";" }
                        }
                    }, {
                        "title": "替换",
                        "properties": {
                            "type": { "type": "string", "enum": ["sfe4j.replacer"], "options": { "hidden": true } },
                            "name": { "title": "名字", "type": "string" },
                            "regex": { "title": "正则表达式", "type": "string" },
                            "replacement": { "title": "用来替换的字符串", "type": "string" },
                            "useMulti": { "title": "替换全部", "type": "boolean", "format": "checkbox" }
                        }
                    }, {
                        "title": "截取",
                        "properties": {
                            "type": { "type": "string", "enum": ["sfe4j.substring"], "options": { "hidden": true } },
                            "name": { "title": "名字", "type": "string", "default": "" },
                            "beginIndex": { "title": "开始位置从0开始", "type": "integer", "default": 0, "format": "number" },
                            "endIndex": { "title": "结束位置", "type": "integer", "default": 0, "format": "number" },
                            "beginStr": { "title": "开始字符串", "type": "string" },
                            "includeBeginStr": { "title": "包含开始字符串", "type": "boolean", "format": "checkbox" },
                            "useLastBeginStr": { "title": "开始字符串使用最后一个位置", "type": "boolean", "format": "checkbox" },
                            "endStr": { "title": "结束字符串", "type": "string" },
                            "includeEndStr": { "title": "包含结束字符串", "type": "boolean", "format": "checkbox" },
                            "useLastEndStr": { "title": "结束字符串使用最后一个位置", "type": "boolean", "format": "checkbox" }
                        }
                    }, {
                        "title": "补全",
                        "properties": {
                            "type": { "type": "string", "enum": ["sfe4j.complete"], "options": { "hidden": true } },
                            "prefix": { "title": "前缀", "type": "string", "default": "" },
                            "prefixStd": { "title": "标准前缀", "type": "string"},
                            "suffix": { "title": "后缀", "type": "string", "default": "" },
                            "suffixStd": { "title": "标准后缀", "type": "string" }
                        }
                    }]
                }
            }
        }
    };

    return formatter;
}
