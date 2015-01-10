package pt.isel.pdm.grupo17.thothnews.services;

/**
 * Created by Kadete on 09/01/2015.
 */
class JsonModels {

//    class JsonThothLink{
//        public static final String SELF = "self";
//    }

        static class JsonThothAvatar{
            //        public static final String SIZE24 = "size24";
//        public static final String SIZE32 = "size32";
            public static final String SIZE64 = "size64";
//        public static final String SIZE128 = "size128";
        }

        static class JsonThothTeacher{
            /** Props **/
            public static final String ID = "id";
            public static final String NUMBER = "number";
            public static final String FULL_NAME = "fullName";
            public static final String SHORT_NAME = "shortName";
            public static final String ACADEMIC_EMAIL = "academicEmail";
            /** Object JsonThothAvatar **/
            public static final String AVATAR = "avatarUrl";
            /** Object JsonThothLink **/
            public static final String LINKS = "_links";
        }

        static class JsonThothClass{
            /** Array **/
            public static final String ARRAY_CLASSES = "classes";
            /** Props **/
            public static final String ID = "id";
            public static final String FULL_NAME = "fullName";
            public static final String COURSE_NAME = "courseUnitShortName";
            public static final String LECTIVE_SEMESTER = "lectiveSemesterShortName";
            public static final String NAME = "className";
            public static final String TEACHER = "mainTeacherShortName";
            /** Object JsonThothAvatar **/
            public static final String LINKS = "_links";
        }

        static class JsonThothFullClass{
            public static final String TEACHER_ID = "mainTeacherId";
        }

        static class JsonThothNew {
            /** Array **/
            public static final String ARRAY_NEWS_ITEMS = "newsItems";
            /** Props **/
            public static final String ID = "id";
            public static final String TITLE = "title";
            public static final String WHEN = "when";
            public static final String CONTENT = "content";
        }
        static class JsonThothParticipant{
            /** Array **/
            public static final String ARRAY_STUDENTS = "students";
            /** Props **/
            public static final String ID = "id";
            public static final String FULL_NAME = "fullName";
            public static final String ACADEMIC_EMAIL = "academicEmail";
            /** Object JsonThothAvatar **/
//        public static final String AVATAR_URL = "avatarUrl";
            public static final String ENROLL_DATE = "enrollmentDate";
            public static final String GROUP = "currentGroup";
        }
        static class JsonThothWorkItem{
            /** Array **/
            public static final String ARRAY_WORK_ITEMS = "workItems";
            /** Props **/
            public static final String ID = "id";
            public static final String TITLE = "title";
            public static final String START_DATE = "startDate";
            public static final String DUE_DATE = "dueDate";
        }
}
