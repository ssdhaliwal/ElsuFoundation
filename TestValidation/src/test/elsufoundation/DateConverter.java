package test.elsufoundation;

import java.util.*;

import elsu.common.*;

public class DateConverter {

	public static void main(String[] args) {
		String format = "yyyyMMddHHmmssS";
		String data = "1479491714000";
		
		Calendar local = Calendar.getInstance();
		
		//local = DateUtils.convertString2Date(data, format);
		local.setTimeInMillis(Long.valueOf(data));
		System.out.println(local.getTime().toString());
		
		System.out.println(local.getTimeInMillis() + ", " + new java.sql.Timestamp(local.getTimeInMillis()));
		try {
			System.in.read();
		} catch (Exception exi) { }
	}
}
