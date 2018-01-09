package com.hiekn.sfe4j.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 * DateFormat多线程不安全   要求每个线程拥有一个单独的DateFormat对象
 * 
 * 
 * @author pzn
 *
 */
public final class TimeUtils {
	
	public static void main(String[] args) {
		
//		String time = "Posted by Daniel Bryant on Nov 22, 2017. Estimated reading time: 7 minutes";
		String time = "12. 30, 17";
		System.out.println(new TimeUtils().formatStringTime(time));
	}
	
	public final String formatStringTime(String time) {
		//
		long currentMillis = System.currentTimeMillis();
		if (null == time || "".equals(time)) return STD_TIME_FORMATOR.format(currentMillis);
		
		//
		Matcher matcher;
		//英文时间处理
		for (String monthKey : MONTHMAP.keySet()) {
			if (time.contains(monthKey)) {
				Calendar cal = Calendar.getInstance(); 
				String year = cal.get(Calendar.YEAR) + "";
				String month = cal.get(Calendar.MONTH) + "";
				String day = cal.get(Calendar.DAY_OF_MONTH) + "";
				String yearRegex = "\\d{4}";
				String dayRegex = "\\d{1,2}";
				month = MONTHMAP.get(monthKey);
				Pattern yearPattern = Pattern.compile(yearRegex);
				Matcher yearMatcher = yearPattern.matcher(time);
				while (yearMatcher.find()) {
					year = yearMatcher.group();
					if (year.length() < 4) year = cal.get(Calendar.YEAR) + "";
				}
				Pattern dayPattern = Pattern.compile(dayRegex);
				time = time.replace(year, "");
				Matcher dayMatcher = dayPattern.matcher(time);
				if (dayMatcher.find()) {
					day = dayMatcher.group(0);
				}
				time = year +  "." + month + "." + day;
			}
		}
		
		
		//
		// 处理语意上的时间
		//
		if (time.indexOf(MONTHS_LEFT_SUFFIX) > -1) {//
			matcher = Pattern.compile(MONTHS_LEFT_REGEX).matcher(time);
			if (matcher.find()) {
				int monthsLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - monthsLeft * MONTH_OF_MILLI_SECONDS);
			}
		} else if (time.indexOf(MONTHS_LEFT_EXTRA_SUFFIX) > -1) {
			matcher = Pattern.compile(MONTHS_LEFT_EXTRA_REGEX).matcher(time);
			if (matcher.find()) {
				int monthsLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - monthsLeft * MONTH_OF_MILLI_SECONDS);
			}
		}
		if (time.indexOf(DAYS_LEFT_SUFFIX) > -1) {//
			matcher = Pattern.compile(DAYS_LEFT_REGEX).matcher(time);
			if (matcher.find()) {
				int daysLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - daysLeft * DAY_OF_MILLI_SECONDS);
			}
		} else if (time.indexOf(DAYS_LEFT_EXTRA_SUFFIX) > -1) {
			matcher = Pattern.compile(DAYS_LEFT_EXTRA_REGEX).matcher(time);
			if (matcher.find()) {
				int daysLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - daysLeft * DAY_OF_MILLI_SECONDS);
			}
		}
		if (time.indexOf(HOURS_LEFT_SUFFIX) > -1) {
			matcher = Pattern.compile(HOURS_LEFT_REGEX).matcher(time);
			if (matcher.find()) {
				int hoursLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - hoursLeft * HOUR_OF_MILLI_SECONDS);
			}
			
		} else if (time.indexOf(HOURS_LEFT_EXTRA_SUFFIX) > -1) {
			matcher = Pattern.compile(HOURS_LEFT_EXTRA_REGEX).matcher(time);
			if (matcher.find()) {
				int hoursLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - hoursLeft * HOUR_OF_MILLI_SECONDS);
			}
		} 
		if (time.indexOf(MINUTES_LEFT_SUFFIX) > -1) {
			matcher = Pattern.compile(MINUTES_LEFT_REGEX).matcher(time);
			if (matcher.find()) {
				int minutesLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - minutesLeft * MINUTE_OF_MILLI_SECONDS);
			}
		} else if (time.indexOf(MINUTES_LEFT_EXTRA_SUFFIX) > -1) {
			matcher = Pattern.compile(MINUTES_LEFT_EXTRA_REGEX).matcher(time);
			if (matcher.find()) {
				int minutesLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - minutesLeft * MINUTE_OF_MILLI_SECONDS);
			}
		}
		if (time.indexOf(SECONDS_LEFT_SUFFIX) > -1) {
			matcher = Pattern.compile(SECONDS_LEFT_REGEX).matcher(time);
			if (matcher.find()) {
				int secondsLeft = Integer.parseInt(matcher.group(1));
				return STD_TIME_FORMATOR.format(System.currentTimeMillis() - secondsLeft * SECOND_OF_MILLI_SECONDS);
			}
		}
		if (time.indexOf(TODAY_PREFIX) > -1) {
			time = time.replaceFirst(TODAY_PREFIX, STD_DATE_FORMATOR.format(currentMillis) + " ");
		}
		if (time.indexOf(YESTERDAY_PREFIX) > -1) {
			time = time.replaceFirst(YESTERDAY_PREFIX, STD_DATE_FORMATOR.format(currentMillis - DAY_OF_MILLI_SECONDS) + " ");
		}
		
		//
		// 带中文时间替换
		// 以及使用[./]分隔的日期
		//
		matcher = Pattern.compile("(\\d+)年(\\d+)月(\\d+)日").matcher(time);
		if (matcher.find()) {
			time = matcher.replaceFirst("$1-$2-$3");
		} else {
			matcher = Pattern.compile("(\\d+)月(\\d+)日").matcher(time);
			if (matcher.find()) {
				time = matcher.replaceFirst("$1-$2");
			}
		}
		matcher = Pattern.compile("(\\d+)(时|点)(\\d+)分(\\d+)秒").matcher(time);
		if (matcher.find()) {
			time = matcher.replaceFirst("$1:$3:$4");
		} else {
			matcher = Pattern.compile("(\\d+)(时|点)(\\d+)分").matcher(time);
			if (matcher.find()) {
				time = matcher.replaceFirst("$1:$3");
			}
		}
		matcher = Pattern.compile("(\\d+)/").matcher(time);
		if (matcher.find()) {
			time = time.replaceAll("/", "-");
		}
		matcher = Pattern.compile("(\\d+)\\.").matcher(time);
		if (matcher.find()) {
			time = time.replaceAll("\\.", "-");
		}
		
		
		//
		// 过滤掉所有非时间字符  
		// 去掉两端的空白字符
		//
		time = time.replaceAll(EXCLUDE_FROM_TIME_REGEX, "").trim();
		
		// 
		matcher = Pattern.compile(STD_TIME_REGEX).matcher(time);
		if (matcher.find()) return matcher.group();
		
		//
		// 匹配时间并提取
		//
		matcher = Pattern.compile(YMDHMS_TIME_REGEX).matcher(time);
		if (matcher.find()) {
			time = matcher.group();
		} else {
			matcher = Pattern.compile(YMDHM_TIME_REGEX).matcher(time);
			if (matcher.find()) {
				time = matcher.group();
			} else {
				matcher = Pattern.compile(YMDH_TIME_REGEX).matcher(time);
				if (matcher.find()) {
					time = matcher.group();
				} else {
					matcher = Pattern.compile(YMD_TIME_REGEX).matcher(time);
					if (matcher.find()) {
						time = matcher.group();
					} else {
						matcher = Pattern.compile(YM_TIME_REGEX).matcher(time);
						if (matcher.find()) {
							time = matcher.group();;
						} else {
							matcher = Pattern.compile(MDHMS_TIME_REGEX).matcher(time);
							if (matcher.find()) {
								time = matcher.group();
							} else {
								matcher = Pattern.compile(MDHM_TIME_REGEX).matcher(time);
								if (matcher.find()) {
									time = matcher.group();
								} else {
									matcher = Pattern.compile(MDH_TIME_REGEX).matcher(time);
									if (matcher.find()) {
										time = matcher.group();
									} else {
										matcher = Pattern.compile(MD_TIME_REGEX).matcher(time);
										if (matcher.find()) time = matcher.group();;
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		//
		// 时间规整化: 补全
		//
		String year = "";
		String month = "";
		String day = "";
		String hour = "";
		String minute = "";
		String second = "";
		String[] dts = time.split("\\s+");
		if (dts.length == 2) {// 时间和日期都有
			String[] ymds = dts[0].split("-");
			if (ymds.length == 3) {// 年月日
				year = ymds[0];
				month = ymds[1];
				day = ymds[2];
			} else if (ymds.length == 2) {// 月日
				month = ymds[0];
				day = ymds[1];
			}
			
			String[] hmss = dts[1].split(":");
			if (hmss.length == 3) {// 时分秒
				hour = hmss[0];
				minute = hmss[1];
				second = hmss[2];
			} else if (hmss.length == 2) {// 时分
				hour = hmss[0];
				minute = hmss[1];
			}
			
		} else if (dts.length == 1) {// 只有日期或者只有时间
			String[] ds = dts[0].split("-");
			if (ds.length > 1) {// 属于日期
				if (ds.length == 3) {// 年月日
					year = ds[0];
					month = ds[1];
					day = ds[2];
				} else if (ds.length == 2) {// 月日
					month = ds[0];
					day = ds[1];
				}
			} else {
				String[] ts = dts[0].split(":");
				if (ts.length > 1) {// 属于时间
					if (ts.length == 3) {// 时分秒
						hour = ts[0];
						minute = ts[1];
						second = ts[2];
					} else if (ts.length == 2) {// 时分
						hour = ts[0];
						minute = ts[1];
					}
				}
			}
		}
		
		Calendar nowTime = Calendar.getInstance();
		String currentYear = nowTime.get(Calendar.YEAR) + "";
		String currentMonth = nowTime.get(Calendar.MONTH) + 1 + "";
		if (currentMonth.length() < 2) currentMonth = "0" + currentMonth;;
		String currentDay = nowTime.get(Calendar.DAY_OF_MONTH) + "";
		if (currentDay.length() < 2) currentDay = "0" + currentDay;
		time = new StringBuilder(20)
//									.append(year.length() == 0 ? currentYear : year.length() == 2 ? currentYear.substring(0, 2) + year : year)// year
									.append(year.length() == 4 ? year : year.length() == 2 ? currentYear.substring(0, 2) + year : currentYear)// year
									.append("-")// 
//									.append(month.length() == 0 ? currentMonth : month.length() == 1 ? "0" + month : month)// month
									.append(month.length() == 2 ? month : month.length() == 1 ? "0" + month : currentMonth)// month
									.append("-")// 
//									.append(day.length() == 0 ? currentDay : day.length() == 1 ? "0" + day : day)// day
									.append(day.length() == 2 ? day : day.length() == 1 ? "0" + day : currentDay)// day
									.append(" ")
//									.append(hour.length() == 0 ? "00" : hour.length() == 1 ? "0" + hour : hour)// hour
									.append(hour.length() == 2 ? hour : hour.length() == 1 ? "0" + hour : "00")// hour
									.append(":")
//									.append(minute.length() == 0 ? "00" : minute.length() == 1 ? "0" + minute : minute)// minute
									.append(minute.length() == 2 ? minute : minute.length() == 1 ? "0" + minute : "00")// minute
									.append(":")
//									.append(second.length() == 0 ? "00" : second.length() == 1 ? "0" + second : second)// second
									.append(second.length() == 2 ? second : second.length() == 1 ? "0" + second : "00")// second
									.toString();
		
		return time;
	}
	
	/**
	 * 
	 * long 毫秒类型的时间字符串  格式化成标准时间字符串
	 * 
	 * @param longMillis
	 * @return
	 */
	public final String formatLongTime(String longMillis) {
		long timeMillis = Long.parseLong(longMillis, 10);
		return STD_TIME_FORMATOR.format(timeMillis);
	}
	
	//
	private static final long MONTH_OF_MILLI_SECONDS = 0x9a7ec800L;
	private static final long DAY_OF_MILLI_SECONDS = 0x5265c00L;
	private static final long HOUR_OF_MILLI_SECONDS = 0x36ee80L;
	private static final long MINUTE_OF_MILLI_SECONDS = 0xea60L;
	private static final long SECOND_OF_MILLI_SECONDS = 0x3e8L;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//
	//	语义上的时间字符串
	//
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static final String TODAY_PREFIX = "今天";
	private static final String YESTERDAY_PREFIX = "昨天";
	private static final String MONTHS_LEFT_SUFFIX = "个月前";
	private static final String MONTHS_LEFT_EXTRA_SUFFIX = "个月";
	private static final String DAYS_LEFT_SUFFIX = "天前";
	private static final String DAYS_LEFT_EXTRA_SUFFIX = "天";
	private static final String HOURS_LEFT_SUFFIX = "小时前";
	private static final String HOURS_LEFT_EXTRA_SUFFIX = "小时";
	private static final String MINUTES_LEFT_SUFFIX = "分钟前";
	private static final String MINUTES_LEFT_EXTRA_SUFFIX = "分钟";
	private static final String SECONDS_LEFT_SUFFIX = "秒前";
	private static final String MONTHS_LEFT_REGEX = "(\\d+)\\s*个月前";
	private static final String MONTHS_LEFT_EXTRA_REGEX = "(\\d+)\\s*个月";
	private static final String DAYS_LEFT_REGEX = "(\\d+)\\s*天前";
	private static final String DAYS_LEFT_EXTRA_REGEX = "(\\d+)\\s*天";
	private static final String HOURS_LEFT_REGEX = "(\\d+)\\s*小时前";
	private static final String HOURS_LEFT_EXTRA_REGEX = "(\\d+)\\s*小时";
	private static final String MINUTES_LEFT_REGEX = "(\\d+)\\s*分钟前";
	private static final String MINUTES_LEFT_EXTRA_REGEX = "(\\d+)\\s*分钟";
	private static final String SECONDS_LEFT_REGEX = "(\\d+)\\s*秒前";
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//
	//	带月份的时间字符串
	//
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// 月日时分秒
	private static final String MDHMS_TIME_REGEX = "\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}";
	// 月日时分
	private static final String MDHM_TIME_REGEX = "\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}";
	// 月日时
	private static final String MDH_TIME_REGEX = "\\d{1,2}-\\d{1,2}\\s+\\d{1,2}";
	// 月日
	private static final String MD_TIME_REGEX = "\\d{1,2}-\\d{1,2}";
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//
	//	带年份的时间字符串
	//
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// 年月日时分秒
	private static final String YMDHMS_TIME_REGEX = "\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}";
	// 年月日时分
	private static final String YMDHM_TIME_REGEX = "\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}";
	// 年月日
	private static final String YMDH_TIME_REGEX = "\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}";
	// 年月日
	private static final String YMD_TIME_REGEX = "\\d{2,4}-\\d{1,2}-\\d{1,2}";
	// 年月
	private static final String YM_TIME_REGEX = "\\d{2,4}-\\d{1,2}";
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//
	//	标准时间字符串
	//
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// 标准时间  yyyy-MM-dd HH:mm:ss
	private static final String STD_TIME_REGEX = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//
	//	非时间表示字符
	//
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static final String EXCLUDE_FROM_TIME_REGEX = "[^\\d:\\s-]";
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static final Map<String,String> MONTHMAP = getMonthMap();
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//
	//	标准时间/日期格式器
	//  DateFormat多线程不安全   要求每个线程拥有一个单独的DateFormat对象
	//
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private final DateFormat STD_TIME_FORMATOR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final DateFormat STD_DATE_FORMATOR = new SimpleDateFormat("yyyy-MM-dd");
	
	private static Map<String,String> getMonthMap() {
		Map<String,String> yearMap = new HashMap<String,String>();
		yearMap.put("Jan", "01");
		yearMap.put("January", "01");
		yearMap.put("Feb", "02");
		yearMap.put("February", "02");
		yearMap.put("Mar", "03");
		yearMap.put("March", "03");
		yearMap.put("Apr", "04");
		yearMap.put("April", "04");
		yearMap.put("May", "05");
		yearMap.put("Jun", "06");
		yearMap.put("June", "06");
		yearMap.put("Jul", "07");
		yearMap.put("July", "07");
		yearMap.put("Aug", "08");
		yearMap.put("August", "08");
		yearMap.put("Sep", "09");
		yearMap.put("September", "09");
		yearMap.put("Oct", "10");
		yearMap.put("October", "10");
		yearMap.put("Nov", "11");
		yearMap.put("November", "11");
		yearMap.put("Dec", "12");
		yearMap.put("December", "12");
		return yearMap;
	}
}
