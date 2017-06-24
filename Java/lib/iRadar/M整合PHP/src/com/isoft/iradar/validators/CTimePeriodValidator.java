package com.isoft.iradar.validators;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.types.CArray.array;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTimePeriodValidator extends CValidator<String> {
	
	/**
	 * Assume that period string contains multiple periods separated by ';'.
	 *
	 * @var bool
	 */
	public boolean allowMultiple = true;

	/**
	 * Validate multiple time periods.
	 * Time periods is a string with format:
	 *   'day1-day2,time1-time2;interval2;interval3;...' (day2 and last ';' are optional)
	 * Examples:
	 *   5-7,00:00-09:00;1,10:00-20:00;
	 *   5,0:0-9:0
	 *
	 * @param string periods
	 *
	 * @throws InvalidArgumentException
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, String period) {
		if(empty(period)) {
			this.setError(_("Empty time period."));
			return false;
		}

		String[] periods;
		if (this.allowMultiple) {
			// remove one last ';' 
			if (period.endsWith(";")) {
				period = period.substring(0, period.length()-1);
			}
			periods = explode(";", period);
		}
		else {
			periods = new String[] {period};
		}

		for(String periodStr :periods) {
			if (!this.validateSinglePeriod(periodStr)) {
				return false;
			}
		}
		return true;
	}
	
	
		
	/**
	 * Validate single time period.
	 * Time period is a string with format:
	 *   'day1-day2,time1-time2;' (day2 and ';' are optional)
	 * Examples:
	 *   5-7,00:00-09:00
	 *   5,0:00-9:00
	 *
	 * @param string period
	 *
	 * @return bool
	 */
	protected boolean validateSinglePeriod(String period) {
		String daysRegExp = "(?<day1>[1-7])(-(?<day2>[1-7]))?";
		String time1RegExp = "(?<hour1>20|21|22|23|24|[0-1]\\d|\\d):(?<min1>[0-5]\\d)";
		String time2RegExp = "(?<hour2>20|21|22|23|24|[0-1]\\d|\\d):(?<min2>[0-5]\\d)";

		String regExp = "^"+daysRegExp+","+time1RegExp+"-"+time2RegExp+"$";
		CArray<String> matches = array();
		if (0==preg_match(regExp, period, matches)) {
			this.setError(_s("Incorrect time period \"%1$s\".", period));
			return false;
		}

		if (24== Nest.value(matches,"hour2").asInteger() && 0!=Nest.value(matches,"min2").asInteger()) {
			this.setError(_s("Incorrect time period \"%1$s\".", period));
			return false;
		}

		if (!empty(matches.get("day2")) && matches.get("day1").compareTo(matches.get("day2"))>0) {
			this.setError(_s("Incorrect time period \"%1$s\" start day must be less or equal to end day.", period));
			return false;
		}

		if ((matches.get("hour1").compareTo(matches.get("hour2"))>0)
				|| ((matches.get("hour1").equals(matches.get("hour2"))) && (matches.get("min1").compareTo(matches.get("min2"))>=0))) {
			this.setError(_s("Incorrect time period \"%1$s\" start time must be less than end time.", period));
			return false;
		}

		return true;
	}
}
