package com.phonecleaner.icecleaner.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

public class PackagesInfo {

	private List appList;

	public PackagesInfo(Context context) {
		this.appList = context.getApplicationContext().getPackageManager()
				.getInstalledApplications(0);
	}

	public PackagesInfo(Context context, String s) {
		this.appList = context.getApplicationContext().getPackageManager()
				.getInstalledApplications(128);
	}

	public ApplicationInfo getInfo(String s) {
		
		ApplicationInfo applicationInfo = null;
		if (s != null) {
			for (Iterator iterator = appList.iterator(); iterator.hasNext();) {
				applicationInfo = (ApplicationInfo) iterator.next();
				String s1 = applicationInfo.processName;
				if (s.equals(s1)){
					Log.d("SHORT: ", s+" "+s1);
					break;
				}	
			}
		}

		return applicationInfo;
	}
}
