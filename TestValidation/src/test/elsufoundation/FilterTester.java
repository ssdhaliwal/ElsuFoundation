package test.elsufoundation;

import java.util.*;

import elsu.support.*;
import filters.*;

public class FilterTester {
	public FilterTester() {

	}

	public void createFilter() {
		String jsonString = 
				"{  " +
				"   \"mapExtent\":{  " +
				"      \"ignore\":false," +
				"      \"southWest\":{  " +
				"         \"longitude\":10011.0," +
				"         \"latitude\":10012.0" +
				"      }," +
				"      \"northEast\":{  " +
				"         \"longitude\":10021.0," +
				"         \"latitude\":10022.0" +
				"      }" +
				"   }," +
				"   \"filterPage\":{  " +
				"      \"orderByColumns\":\"NAME,COUNTRYCODE\"," +
				"      \"pageIndex\":5," +
				"      \"pageSize\":25" +
				"   }," +
				"   \"filterSets\":[  " +
				"      {  " +
				"         \"filters\":[  " +
				"            {  " +
				"               \"column\":\"TEST1\"," +
				"               \"operator\":\"LEQ\"," +
				"               \"values\":[  " +
				"                  \"20120\"" +
				"               ]," +
				"               \"negate\":false," +
				"               \"numeric\":false," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }," +
				"            {  " +
				"               \"column\":\"TEST2\"," +
				"               \"operator\":\"EQ\"," +
				"               \"values\":[  " +
				"                  \"120\"" +
				"               ]," +
				"               \"negate\":false," +
				"               \"numeric\":true," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"                  {  " +
				"                     \"filters\":[  " +
				"                        {  " +
				"                           \"column\":\"FIELD1\"," +
				"                           \"operator\":\"LEQ\"," +
				"                           \"values\":[  " +
				"                              \"30103\"" +
				"                           ]," +
				"                           \"negate\":false," +
				"                           \"numeric\":false," +
				"                           \"logic\":\"OR\"," +
				"                           \"filterSets\":[  " +
				"" +
				"                           ]" +
				"                        }," +
				"                        {  " +
				"                           \"column\":\"FIELD2\"," +
				"                           \"operator\":\"EQ\"," +
				"                           \"values\":[  " +
				"                              \"301\"" +
				"                           ]," +
				"                           \"negate\":true," +
				"                           \"numeric\":false," +
				"                           \"logic\":\"OR\"," +
				"                           \"filterSets\":[  " +
				"                              {  " +
				"                                 \"filters\":[  " +
				"                                    {  " +
				"                                       \"column\":\"TEST4\"," +
				"                                       \"operator\":\"LIKE\"," +
				"                                       \"values\":[  " +
				"                                          \"20%\"" +
				"                                       ]," +
				"                                       \"negate\":false," +
				"                                       \"numeric\":false," +
				"                                       \"logic\":\"OR\"," +
				"                                       \"filterSets\":[  " +
				"" +
				"                                       ]" +
				"                                    }," +
				"                                    {  " +
				"                                       \"column\":\"FIELD1\"," +
				"                                       \"operator\":\"LEQ\"," +
				"                                       \"values\":[  " +
				"                                          \"30103\"" +
				"                                       ]," +
				"                                       \"negate\":false," +
				"                                       \"numeric\":false," +
				"                                       \"logic\":\"OR\"," +
				"                                       \"filterSets\":[  " +
				"" +
				"                                       ]" +
				"                                    }" +
				"                                 ]," +
				"                                 \"logic\":\"AND\"" +
				"                              }," +
				"                              {  " +
				"                                 \"filters\":[  " +
				"                                    {  " +
				"                                       \"column\":\"TEST5\"," +
				"                                       \"operator\":\"IN\"," +
				"                                       \"values\":[  " +
				"                                          \"A\"," +
				"                                          \"B\"," +
				"                                          \"C\"," +
				"                                          \"D\"," +
				"                                          \"E\"" +
				"                                       ]," +
				"                                       \"negate\":false," +
				"                                       \"numeric\":false," +
				"                                       \"logic\":\"OR\"," +
				"                                       \"filterSets\":[  " +
				"" +
				"                                       ]" +
				"                                    }" +
				"                                 ]," +
				"                                 \"logic\":\"AND\"" +
				"                              }" +
				"                           ]" +
				"                        }," +
				"                        {  " +
				"                           \"column\":\"FIELD3\"," +
				"                           \"operator\":\"GT\"," +
				"                           \"values\":[  " +
				"                              \"584\"" +
				"                           ]," +
				"                           \"negate\":true," +
				"                           \"numeric\":true," +
				"                           \"logic\":\"OR\"," +
				"                           \"filterSets\":[  " +
				"" +
				"                           ]" +
				"                        }" +
				"                     ]," +
				"                     \"logic\":\"AND\"" +
				"                  }" +
				"               ]" +
				"            }," +
				"            {  " +
				"               \"column\":\"TEST3\"," +
				"               \"operator\":\"GT\"," +
				"               \"values\":[  " +
				"                  \"454\"" +
				"               ]," +
				"               \"negate\":true," +
				"               \"numeric\":false," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }," +
				"            {  " +
				"               \"column\":\"TEST4\"," +
				"               \"operator\":\"LIKE\"," +
				"               \"values\":[  " +
				"                  \"20%\"" +
				"               ]," +
				"               \"negate\":false," +
				"               \"numeric\":false," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }," +
				"            {  " +
				"               \"column\":\"TEST5\"," +
				"               \"operator\":\"IN\"," +
				"               \"values\":[  " +
				"                  \"A\"," +
				"                  \"B\"," +
				"                  \"C\"," +
				"                  \"D\"," +
				"                  \"E\"" +
				"               ]," +
				"               \"negate\":false," +
				"               \"numeric\":false," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }" +
				"         ]," +
				"         \"logic\":\"OR\"" +
				"      }," +
				"      {  " +
				"         \"filters\":[  " +
				"            {  " +
				"               \"column\":\"FIELD1\"," +
				"               \"operator\":\"LEQ\"," +
				"               \"values\":[  " +
				"                  \"30103\"" +
				"               ]," +
				"               \"negate\":false," +
				"               \"numeric\":false," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }," +
				"            {  " +
				"               \"column\":\"FIELD2\"," +
				"               \"operator\":\"EQ\"," +
				"               \"values\":[  " +
				"                  \"301\"" +
				"               ]," +
				"               \"negate\":true," +
				"               \"numeric\":false," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }," +
				"            {  " +
				"               \"column\":\"FIELD3\"," +
				"               \"operator\":\"GT\"," +
				"               \"values\":[  " +
				"                  \"584\"" +
				"               ]," +
				"               \"negate\":true," +
				"               \"numeric\":true," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }," +
				"            {  " +
				"               \"column\":\"FIELD4\"," +
				"               \"operator\":\"LIKE\"," +
				"               \"values\":[  " +
				"                  \"%20\"" +
				"               ]," +
				"               \"negate\":false," +
				"               \"numeric\":false," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }," +
				"            {  " +
				"               \"column\":\"FIELD5\"," +
				"               \"operator\":\"IN\"," +
				"               \"values\":[  " +
				"                  \"1\"," +
				"                  \"2\"," +
				"                  \"3\"," +
				"                  \"4\"," +
				"                  \"5\"," +
				"                  \"6\"," +
				"                  \"7\"," +
				"                  \"8\"," +
				"                  \"9\"," +
				"                  \"0\"" +
				"               ]," +
				"               \"negate\":true," +
				"               \"numeric\":true," +
				"               \"logic\":\"OR\"," +
				"               \"filterSets\":[  " +
				"" +
				"               ]" +
				"            }" +
				"         ]," +
				"         \"logic\":\"OR\"" +
				"      }" +
				"   ]," +
				"   \"logic\":\"AND\"" +
				"}";
		String result = "";

		try {
			FilterGroup fg = new FilterGroup();
			fg.setPageSize(25);
			fg.setOrderByColumns("NAME,COUNTRYCODE");
			fg.setPageIndex(5);

			fg.setMapExtentSouthWest(10011.0, 10012.0);
			fg.setMapExtentNorthEast(10021.0, 10022.0);

			fg.setLogic("AND");
			FilterSet fgs= new FilterSet();
			fgs.addFilter("TEST1", "LEQ", "20120", false, false);
			FilterType ft = fgs.addFilter("TEST2", "EQ", "120", false, true);
				ft.setLogic("OR");
					FilterSet fts = new FilterSet();
					fts.setLogic("AND");
					fts.addFilter("FIELD1", "LEQ", "30103", false, false);
						FilterType ft2 = fts.addFilter("FIELD2", "EQ", "301", true, false);
						ft2.setLogic("OR");
						FilterSet ft2s = new FilterSet();
							ft2s.setLogic("AND");
							ft2s.addFilter("TEST4", "LIKE", "20%", false, false);
							ft2s.addFilter("FIELD1", "LEQ", "30103", false, false);
						ft2.addFilterSets(ft2s);
						ft2s = new FilterSet();
							ft2s.setLogic("AND");
							ft2s.addFilter("TEST5", "IN", Arrays.asList("A", "B", "C", "D", "E"), false, false);
						ft2.addFilterSets(ft2s);
					fts.addFilter("FIELD3", "GT", "584", true, true);
				ft.addFilterSets(fts);
			fgs.addFilter("TEST3", "GT", "454", true, false);
			fgs.addFilter("TEST4", "LIKE", "20%", false, false);
			fgs.addFilter("TEST5", "IN", Arrays.asList("A", "B", "C", "D", "E"), false, false);
			fg.addFilterSets(fgs);

			fgs = new FilterSet();
			fgs.addFilter("FIELD1", "LEQ", "30103", false, false);
			fgs.addFilter("FIELD2", "EQ", "301", true, false);
			fgs.addFilter("FIELD3", "GT", "584", true, true);
			fgs.addFilter("FIELD4", "LIKE", "%20", false, false);
			fgs.addFilter("FIELD5", "IN", Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"), true, true);
			fg.addFilterSets(fgs);

			result = JsonXMLUtils.Object2JSon(fg);
			System.out.println(result);

			fg = (FilterGroup) JsonXMLUtils.JSon2Object(result, FilterGroup.class);
			System.out.println(fg.toSQLWhereClause());
		} catch (Exception ex) {
			result = "getTracks(), \n" + ex.getMessage() + "\n" + ex.getStackTrace();
			System.out.println(result);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		FilterTester ft = new FilterTester();
		ft.createFilter();
	}
}
