

#### 
{
	sliver: {
		fields: []
		sliverMeta: {
			sliverType: string
			host: string
			port: integer
			database: string
			collection: string
			query: string 
		}
	
	},
	
	
	fliver: [{field: 'f1', formatter: {}}, {field: 'f2', formatter: {}}],
	
	eliver: {
		operation: string # update/insert
		eliverMeta: {
			eliverType: string
			host: string
			port: integer
			database: string
			collection: string
		}	
	}
}


##########sample
{
    'sliver': {
        'fields': [
            'invest_area'
        ],
        'sliverMeta': {
        	'sliverType': 'mongodb',
            'host': 'localhost',
            'port': 27017,
            'database': 'itjz',
            collection: 'itjz_tzjg',
            query: {
                
            }
        }
    },
    'fliver': [
        {
            'field': 'invest_area',
            'formatter': {
                'defaults': [
                    
                ],
                'customs': [
                    {
                        'type': 'sfe4j.replacer',
                        'regex': '<li>',
                        'replacement': '<br>',
                        useMulti: true
                    }
                ]
            }
        }
    ],
    'eliver': {
        operation: 'update',
        'eliverMeta': {
        	'eliverType': 'mongodb',
            'host': 'localhost',
            'port': 27017,
            'database': 'itjz',
            'collection': 'itjz_tzjg'
        }
    }
}











