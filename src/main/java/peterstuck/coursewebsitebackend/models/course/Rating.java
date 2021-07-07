package peterstuck.coursewebsitebackend.models.course;

public enum Rating {

    ONE(1),
    ONE_AND_HALF(1.5),
    TWO(2),
    TWO_AND_HALF(2.5),
    THREE(3),
    THREE_AND_HALF(3.5),
    FOUR(4),
    FOUR_AND_HALF(4.5),
    FIVE(5);


    public final double starValue;

    Rating(double v) {
        this.starValue = v;
    }
}
