package com.isoft;

public class Feature extends iFeature{

	private Feature() {
	}
	
	public static boolean originalStyle = true;

	public static String iradarServer = "127.0.0.1";
	public static int iradarPort = 10051;

	public static String defaultTenantId = "-1";
	public static final int defaultOsUserGroup = 7;
	
	public static boolean enableGuestUser = false;
	
	public static boolean enableLoginCheck = true;
	//public static boolean enableGlobalException = true;
	
	public static boolean enableTopo = false;

	public static boolean ignorePageFooter = false;
	public static boolean ignorePageHeader = false;

	public static boolean showFullscreenIcon = true;
	public static boolean showFavouriteIcon = true;
	public static boolean showExportCsvIcon = true;
	public static boolean showPopupMenu = true;
	
	public static boolean idsUseConnection = false;
	
	
}
