package com.example.demo;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntNumController {

	@Autowired
	private IntNumRepository numRepository;
	
	//This function parses parameter "ids" by filtering duplicate Ids.
	//"ids" parameter should be in format:  <number>,<number>,...
	private long[] parseIds (String ids) {
		String[] ids_arr = ids.split(",");
		long[] ids_long_arr = new long[ids_arr.length];
		
		//Check if provides Ids are of long type
		for (int i=0; i<ids_arr.length; i++) {
			try {
				ids_long_arr[i] = Long.parseLong(ids_arr[i]);
			} catch (NumberFormatException e) {
				throw e;
			}
		}
		//Remove duplicate Ids entries if any
		Set<Long> int_set = new HashSet<Long>();
		for(int i = 0; i < ids_long_arr.length; i++){
			int_set.add(ids_long_arr[i]);
		}
		//Produce result array with unique Ids
		long[] ids_res = new long[int_set.size()];
		int k = 0;
		for (Long n : int_set) {
			ids_res[k++] = n; 
		}
		return ids_res;
	}
	
	//REST API test method - produces test data and returns numbers sum for all records.
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public Integer generateTestData() {
		// save few customers
		numRepository.save(new IntNum(10));
		numRepository.save(new IntNum(20));
		numRepository.save(new IntNum(30));
		
		return getSum(null);
	}
	
	//This REST API method for adding new number entry, returns Id of created record.
	@RequestMapping(value="/addNum", method=RequestMethod.POST, consumes="application/json")
	public Long addNum(@RequestBody IntNum num) {
		num = numRepository.save(num);
		return num.getId();
	}
	
	/*The REST API method for calculating aggregation function (default scope is all records).
	  If ids parameter is specified, then it calculation is performed only for affected records.
	  It supports 4 aggregation functions (parameter operation): Sum, Avg, Min, and Max.*/ 
	@RequestMapping(value="/getAggregation", method=RequestMethod.GET)
	public Float getAggrValue(@RequestParam(value="operation", required=true) String operation,
			                  @RequestParam(value="ids", required=false) String ids) {
		Float AggrValue = null;
		Integer IntValue = null;

		if (operation.toLowerCase().equals("sum")) {
			IntValue  = getSum(ids);
		} else if (operation.toLowerCase().equals("avg")) {
			AggrValue = getAvg(ids);
		} else if (operation.toLowerCase().equals("min")) {
			IntValue  = getMin(ids);
		} else if (operation.toLowerCase().equals("max")) {
			IntValue  = getMax(ids);
		} else {
			throw new java.lang.Error("Wrong operation name!");
		}
		if (IntValue != null) {
			AggrValue = (float)IntValue;
		}
		return AggrValue;
	}
	
	//Function for calculating sum of numbers (default scope is all records).
	//If ids parameter is specified, then it calculates numbers sum only for affected records.
	private Integer getSum(String ids) {
		Integer l_Sum = null;
		
		if (ids != null && !ids.isEmpty()) {
			long[] ids_arr = parseIds(ids);

			for (int i=0; i<ids_arr.length; i++) {
				IntNum num = numRepository.findById(ids_arr[i]);
				if (num != null) {
					if (l_Sum == null) { l_Sum = 0; }
					l_Sum += num.getNumber();
				}
			}
		} else {
		    for (IntNum num : numRepository.findAll()) {
		    	if (l_Sum == null) { l_Sum = 0; }
		    	l_Sum += num.getNumber();
		    }
		}
		return l_Sum;
	}

	//Function for calculating arithmetical mean of number series (default scope is all records).
	//If ids parameter is specified, then it calculates the average only for affected records.
	private Float getAvg(String ids) {
		Float l_Avg = null, l_Sum = null;
		int l_Cnt = 0;
		if (ids != null && !ids.isEmpty()) {
			long[] ids_arr = parseIds(ids);
			for (int i=0; i<ids_arr.length; i++) {
				IntNum num = numRepository.findById(ids_arr[i]);
				if (num != null) {
					if (l_Sum == null) { l_Sum = 0.0f; }
					l_Sum += num.getNumber();
					++l_Cnt;
				}
			}
		} else {
		    for (IntNum num : numRepository.findAll()) {
		    	if (l_Sum == null) { l_Sum = 0.0f; }
		    	l_Sum += num.getNumber();
		    	++l_Cnt;
		    }
		}
		if (l_Cnt > 0) { 
		    l_Avg = l_Sum / l_Cnt;
		}
		return l_Avg;
	}
	
	//Function for getting minimum number (default scope is all records).
	//If ids parameter is specified, then minimum number is fetched only for affected records.
	private Integer getMin(String ids) {
		Integer l_Min = null;
		if (ids != null && !ids.isEmpty()) { 
			long[] ids_arr = parseIds(ids);
			int cnt = 1;
			for (int i=0; i<ids_arr.length; i++) {
				IntNum num = numRepository.findById(ids_arr[i]);
				if (num != null) {
					if (cnt == 1 || (cnt > 1 && l_Min > num.getNumber())) {
			    		l_Min = num.getNumber();
			    		++cnt;
			    	}				
				}
			}
		} else {
			int cnt = 1;
		    for (IntNum num : numRepository.findAll()) {
		    	if (cnt == 1 || (cnt > 1 && l_Min > num.getNumber())) {
		    		l_Min = num.getNumber();
		    		++cnt;
		    	} 
		    }
		}		
		return l_Min;
	}
	
	//Function for getting maximum number (default scope is all records).
	//If ids parameter is specified, then maximum number is fetched only for affected records.
	private Integer getMax(String ids) {
		Integer l_Max = null;
		if (ids != null && !ids.isEmpty()) {
			long[] ids_arr = parseIds(ids);
			int cnt = 1;
			for (int i=0; i<ids_arr.length; i++) {
				IntNum num = numRepository.findById(ids_arr[i]);
				if (num != null) {
					if (cnt == 1 || (cnt > 1 && l_Max < num.getNumber())) {
			    		l_Max = num.getNumber();
			    		++cnt;
			    	}				
				}
			}
		} else {
			int cnt = 1;
		    for (IntNum num : numRepository.findAll()) {
		    	if (cnt == 1 || (cnt > 1 && l_Max < num.getNumber())) {
		    		l_Max = num.getNumber();
		    		++cnt;
		    	} 
		    }
		}		
		return l_Max;	
	}
}
