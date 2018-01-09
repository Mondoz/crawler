数据格式化：
	1.过滤
	2.查找
	3.替换
	4.截取

#### formatter
{
	defaults: list # 使用一组默认格式器
	customs: list # 使用一组自定义格式器
}

## filter
name: string
type: string # 固定值sfe4j.filter
regex: string #
useMulti: boolean #

## finder
name: string
type: string # 固定值sfe4j.finder
regex: string 
group: integer
useMulti: boolean
multiSeparator: string

## replacer
name: string
type: string # 固定值sfe4j.replacer
regex: string 
replacement: string
useMulti: boolean

## substring 
name: string
type: string # 固定值sfe4j.substring
beginIndex: integer # 从0开始 包含
endIndex: integer # 不包含
beginStr: string # 包含
includeBeginStr: string # 是否包含这个开始的字符串
useLastBeginStr: boolean # 是否使用beginStr的最后一个 类似indexOf 和 lastIndexOf
endStr: string # 不包含
includeEndStr: string # 是否包含这个结束的字符串
useLastEndStr: boolean # 是否使用useLastEndStr的最后一个 类似indexOf 和 lastIndexOf

## time
name: string
type: string # 固定值sfe4j.time
timeType: string # longTime/stringTime

## complete
type: string # 固定值sfe4j.complete
prefix: string # 前缀
prefixStd: string # 标准前缀
suffix: string # 后缀
suffixStd: string # 标准后缀



sample:



