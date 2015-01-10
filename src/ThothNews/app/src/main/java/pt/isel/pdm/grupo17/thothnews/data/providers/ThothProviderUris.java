package pt.isel.pdm.grupo17.thothnews.data.providers;

public class ThothProviderUris {
    /** provider uri paths **/
    public static final String URI_API_ROOT = "http://thoth.cc.e.ipl.pt/api/v1";
    public static final String URI_CLASSES_LIST = URI_API_ROOT + "/classes";
    public static final String URI_CLASS_NEWS_ITEMS = URI_API_ROOT + "/classes/%d/newsitems";
    public static final String URI_NEW_INFO = URI_API_ROOT + "/newsitems/%d";
    public static final String URI_CLASS_PARTICIPANTS = URI_API_ROOT + "/classes/%d/participants";
    public static final String URI_CLASS_WORK_ITEMS = URI_API_ROOT + "/classes/%d/workitems";
    public static final String URI_CLASS_INFO = URI_API_ROOT + "/classes/%d";
    public static final String URI_TEACHER_INFO = URI_API_ROOT + "/teachers/%d";
}
