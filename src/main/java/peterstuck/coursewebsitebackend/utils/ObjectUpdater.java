package peterstuck.coursewebsitebackend.utils;

import peterstuck.coursewebsitebackend.models.course.Category;
import peterstuck.coursewebsitebackend.models.course.Course;
import peterstuck.coursewebsitebackend.models.course.CourseDescription;
import peterstuck.coursewebsitebackend.models.user.User;

import java.util.Date;

public class ObjectUpdater {

    /**
     * It should not be possible to override courseFeedback
     * @param original course to update
     * @param updated course with updated data
     */
    public static void updateCourse(Course original, Course updated) {
        original.setTitle(updated.getTitle());
        original.setSubtitles(updated.getSubtitles());
        original.setLanguages(updated.getLanguages());
        original.setCategories(updated.getCategories());
        original.setLastUpdate(new Date().getTime());
        original.setPrice(updated.getPrice());
        original.setAuthors(updated.getAuthors());

        updateCourseDescription(original.getCourseDescription(), updated.getCourseDescription());
    }

    /**
     * @param original course description to update
     * @param updated course description with updated data
     */
    private static void updateCourseDescription(CourseDescription original, CourseDescription updated) {
        original.setDuration(updated.getDuration());
        original.setShortDescription(updated.getShortDescription());
        original.setLongDescription(updated.getLongDescription());
        original.setMainTopics(updated.getMainTopics());
        original.setRequirements(updated.getRequirements());
    }

    /**
     * It should not be possible to update course list associated to category during update.
     * @param original category to update
     * @param updated category with updated data
     */
    public static void updateCategory(Category original, Category updated) {
        original.setName(updated.getName());
        original.setParentCategoryId(updated.getParentCategoryId());
    }

    /**
     * It should not be possible to override user roles and activities.
     * User can change password with special API operation only for this purpose.
     * @param original user to update
     * @param updated user with updated data
     */
    public static void updateUser(User original, User updated) {
        original.setEmail(updated.getEmail());
        original.setFirstName(updated.getFirstName());
        original.setLastName(updated.getLastName());
        original.setUserDetail(updated.getUserDetail());
    }

}
