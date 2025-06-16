package xyz.playground.stl_web_app.Constants;

public class StringConstants {

    public static final String PAGE_TITLE = "pageTitle";
    public static final String ACTIVE_TAB = "activeTab";
    public static final String VIEW_NAME = "viewName";
    public static final String MAIN_LAYOUT = "layout/main";

    //
    public static final String VAR_SUCCESS_MESSAGE = "successMessage";
    public static final String VAR_ERROR_MESSAGE = "errorMessage";

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String DISPATCHER_ROLE = "DISPATCHER";
    public static final String COLLECTOR_ROLE = "COLLECTOR";

    //
    public static final String ROLE_HAS_ADMIN = "hasRole('" + ADMIN_ROLE + "')";
    public static final String ROLE_HAS_DISPATCHER = "hasRole('" + DISPATCHER_ROLE + "')";
    public static final String ROLE_HAS_COLLECTOR = "hasRole('" + COLLECTOR_ROLE + "')";

    public static final String ROLE_HAS_ANY_COLLECTOR_AND_DISPATCHER = "hasAnyRole('ROLE_" + DISPATCHER_ROLE + "', 'ROLE_" + COLLECTOR_ROLE + "')";

}
