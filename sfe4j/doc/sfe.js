
//
function getSfeSchema(enumSources) {
	var sfe = 
	{
	    "title": "Sfe4j Config",
	    "type": "object",
	    "properties": {
	        "sliver": {
	            "title": "sliver",
	            "type": "object",
	            "properties": {
	                "fields": {
	                    "type": "array",
	                    "format": "tabs",
	                    "uniqueItems": true,
	                    "items": {
	                        "type": "string",
	                        "title": "field"
	                    }
	                },
	                "sliverMeta": {
	                    "title": "sliverMeta",
	                    "type": "object",
	                    "options": {"keep_oneof_values": false},
	                    "oneOf": [
		                    {
		                        "title": "mongodb",
		                        "properties": {
		                            "sliverType": {"type": "string", "enum": ["mongodb"], "options": {"hidden": true}},
		                            "host": { "type": "string", "default": "localhost"},
		                            "port": {"type": "integer", "default": 27017, "format": "number"},
		                            "database": {"type" : "string"},
		                            "collection": {"type": "string"},
		                            "query": {"type": "string", "format": "textarea"}
		                        },
		                        "required": ["sliverType", "host", "port"]
		                    }, 
		                    {
		                        "title": "elasticsearch",
		                        "properties": {
		                        	"sliverType": {"type": "string", "enum": ["elasticsearch"], "options": {"hidden": true}},
		                            "host": { "type": "string", "default": "localhost"},
		                            "port": {"type": "integer", "default": 9300, "format": "number"},
		                            "name": {"type": "string", "default": "elasticsearch"},
		                            "index": {"type": "string", "default": ""},
		                            "type": {"type": "string", "default": ""},
		                            "query": {"type": "string", "format": "textarea"}
		                        },
		                        "required": ["sliverType", "host"]
		                    }, 
		                    {
		                        "title": "mysql",
		                        "properties": {
		                        	"sliverType": {"type": "string", "enum": ["mysql"], "options": {"hidden": true}},
		                            "driver": {"type": "string", "default": "com.mysql.jdbc.Driver"},
		                            "url": {"type": "string", "default": "jdbc:mysql://loclahost:3306/test"},
		                            "username": {"type": "string", "default": ""},
		                            "password": {"type": "string", "default": ""},
		                            "query": {"type": "string", "format": "textarea"}
		                        },
		                        "required": ["sliverType", "url"]
		                    }
	                    ]
	                }
	            }
	        },


	        "fliver": {
	            "title": "fliver",
	            "type": "array",
	            "minItems": 1,
	            "format": "tabs",
	            "items": {
	            	"title": "field formatter",
	            	"type": "object",
	            	"properties": {
	            		"field": {
	            			"type": "string"
	            		},
	            		"formatter": {
	            			"type": "object",
	            			"properties": {
	            				"defaults": {
	            					"type": "array",
	            					"format": "tabs",
	            					"items": {
	            						"title": "formatter",
	            						"type": "string",
	            						"enumSource": [{
	            							"source": enumSources,
	            							"title": "{{item.title}}",
	            							"value": "{{item.value}}"
	            						}]
	            					}
	            				},
	            				"customs": {
	            					"type": "array",
	            					"format": "tabs",
	            					"items": {
	            						"title": "formatter",
	            						"type": "object",
	            						"options": {"keep_oneof_values": false},
	            						"oneOf": [
	            							{
						                        "title": "filter",
						                        "properties": {
						                        	"type": {"type": "string", "enum": ["sfe4j.filter"], "options": {"hidden": true}},
						                            "name": {"type": "string"},
						                            "regex": {"type": "string"},
						                            "useMulti": {"type": "boolean", "format": "checkbox"}
						                        }
						                    },
						                    {
						                        "title": "finder",
						                        "properties": {
						                            "type": {"type": "string", "enum": ["sfe4j.finder"], "options": {"hidden": true}},
						                            "name": {"type": "string"},
						                            "regex": {"type": "string"},
						                            "group": {"type": "integer", "default": 0, "format": "number"},
						                            "useMulti": {"type": "boolean", "format": "checkbox"},
						                            "multiSeparator": {"type": "string", "default": ";"}
						                        }
						                    },
						                    {
						                        "title": "replacer",
						                        "properties": {
						                        	"type": {"type": "string", "enum": ["sfe4j.replacer"], "options": {"hidden": true}},
						                            "name": {"type": "string"},
						                            "regex": {"type": "string"},
						                            "replacement": {"type": "string"},
						                            "useMulti": {"type": "boolean", "format": "checkbox"}
						                        }
						                    },
						                    {
						                        "title": "substring",
						                        "properties": {
						                        	"type": {"type": "string", "enum": ["sfe4j.substring"], "options": {"hidden": true}},
						                            "name": {"type": "string", "default": ""},
													"beginIndex": {"type": "integer", "default": 0, "format": "number"},
													"endIndex": {"type": "integer", "default": 0, "format": "number"},
													"beginStr": {"type": "string"},
													"includeBeginStr": {"type": "boolean", "format": "checkbox"},
													"useLastBeginStr": {"type": "boolean", "format": "checkbox"},
													"endStr": {"type": "string"},
													"includeEndStr": {"type": "boolean", "format": "checkbox"},
													"useLastEndStr": {"type": "boolean", "format": "checkbox"}			                           
						                        }
						                    },
						                    {
						                        "title": "complete",
						                        "properties": {
						                            "type": { "type": "string", "enum": ["sfe4j.complete"], "options": { "hidden": true } },
						                            "prefix": { "title": "前缀", "type": "string", "default": "" },
						                            "prefixStd": { "title": "标准前缀", "type": "string"},
						                            "suffix": { "title": "后缀", "type": "string", "default": "" },
						                            "suffixStd": { "title": "标准后缀", "type": "string" }
						                        }
						                    }
	            						]
	            					}
	            				}
	            			}
	            		}
	            	}
	            }
	        },


	        "eliver": {
	            "title": "eliver",
	            "type": "object",
	            "properties": {
	            	"operation": {
	            		"type": "string",
	            		"enum": ["update", "insert"],
	            		"default": "update"
	            	},
	            	"eliverMeta": {
	                    "type": "object",
	                    "options": {"keep_oneof_values": false},
	                    "oneOf": [
		                    {
		                        "title": "mongodb",
		                        "properties": {
		                        	"eliverType": {"type": "string", "enum": ["mongodb"], "options": {"hidden": true}},
		                            "host": { "type": "string", "default": "localhost"},
		                            "port": {"type": "integer", "default": 27017},
		                            "database": {"type" : "string"},
		                            "collection": {"type": "string"}
		                        },
		                        "required": ["eliverType", "host", "port"]
		                    }, 
		                    {
		                        "title": "elasticsearch",
		                        "properties": {
		                        	"eliverType": {"type": "string", "enum": ["elasticsearch"], "options": {"hidden": true}},
		                            "host": { "type": "string", "default": "localhost"},
		                            "port": {"type": "integer", "default": 9300},
		                            "name": {"type": "string", "default": "elasticsearch"},
		                            "index": {"type": "string", "default": ""},
		                            "type": {"type": "string", "default": ""}
		                        },
		                        "required": ["eliverType", "host"]
		                    }, 
		                    {
		                        "title": "mysql",
		                        "properties": {
		                        	"eliverType": {"type": "string", "enum": ["mysql"], "options": {"hidden": true}},
		                            "driver": {"type": "string", "default": "com.mysql.jdbc.Driver"},
		                            "url": {"type": "string", "default": "jdbc:mysql://loclahost:3306/test"},
		                            "username": {"type": "string", "default": ""},
		                            "password": {"type": "string", "default": ""}
		                        },
		                        "required": ["eliverType", "url"]
		                    }
	                    ]
	                }
	            }
	        }

	    }

	};

	return sfe;
}
