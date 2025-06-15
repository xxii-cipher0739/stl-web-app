package xyz.playground.stl_web_app.Constants;

public class StringConstants {

    public static final String PAGE_TITLE = "pageTitle";
    public static final String ACTIVE_TAB = "activeTab";
    public static final String VIEW_NAME = "viewName";

    public static final String MAIN_LAYOUT = "layout/main";
    public static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    public static final String REDIRECT_USERS = "redirect:/users";

    public static final String VAR_NEXT_GAME = "nextGame";
    public static final String VAR_NEW_USER = "newUser";
    public static final String VAR_USER_LIST = "userList";

    public static final String VAR_STATUS = "status";
    public static final String VAR_ERROR = "error";
    public static final String VAR_MESSAGE = "message";

    public static final String VAR_SUCCESS_MESSAGE = "successMessage";
    public static final String VAR_ERROR_MESSAGE = "errorMessage";

    public static final String ENDPOINT_BASE = "/";
    public static final String ENDPOINT_LOGIN = "/login";
    public static final String ENDPOINT_DASHBOARD = "/dashboard";
    public static final String ENDPOINT_ERROR = "/error";

    public static final String ENDPOINT_USERS = "/users";
    public static final String ENDPOINT_USERS_ADD = "/users/add";
    public static final String ENDPOINT_USERS_EDIT = "/users/edit/{id}";
    public static final String ENDPOINT_USERS_UPDATE = "/users/update/{id}";
    public static final String ENDPOINT_USERS_DELETE = "/users/delete/{id}";

    public static final String ENDPOINT_USER_LIST = "users/list";
    public static final String ENDPOINT_USER_FORM = "users/form";
    //
    public static final String DASHBOARD_TITLE = "Dashboard - Game Web App";
    public static final String USERS_TITLE = "Users - View";
    public static final String USERS_ADD_TITLE = "Users - Add User";
    public static final String USERS_EDIT_TITLE = "Users - Edit User";

    //
    public static final String LOGIN = "login";
    public static final String DASHBOARD = "dashboard";
    public static final String USERS = "users";

    //
    public static final String INVALID_USER_ID = "Invalid user id:";
    public static final String ERROR_ADD_USER = "Error creating user: ";
    public static final String ERROR_UPDATE_USER = "Error updating user: ";
    public static final String ERROR_DELETE_USER = "Error deleting user: ";
    public static final String SUCCESSFUL_ADD_USER = "User created successfully";
    public static final String SUCCESSFUL_UPDATE_USER = "User updated successfully";
    public static final String SUCCESSFUL_DELETE_USER = "User deleted successfully";

    //
    public static final String PAGE_NOT_FOUND = "Page Not Found";
    public static final String PAGE_NOT_FOUND_MESSAGE = "The page you are looking for does not exist.";

    public static final String ACCESS_DENIED = "Access Denied";
    public static final String ACCESS_DENIED_MESSAGE = "You don't have permission to access this resource.";

    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong on our end. Please try again later.";

    //
    public static final String ROLE_HAS_ADMIN = "hasRole('ADMIN')";
}
