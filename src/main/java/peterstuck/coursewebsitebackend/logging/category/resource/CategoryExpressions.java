package peterstuck.coursewebsitebackend.logging.category.resource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
class CategoryExpressions {

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.category.CategoryResource.createCategory(..))")
    public void forAddNewCategory() {}

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.category.CategoryResource.updateCategory(..))")
    public void forUpdateCategory() {}

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.category.CategoryResource.deleteCategory(..))")
    public void forDeleteCategory() {}

    @Pointcut("forAddNewCategory() || forUpdateCategory() || forDeleteCategory()")
    public void forAddUpdateAndDeleteCategory() {}

}
