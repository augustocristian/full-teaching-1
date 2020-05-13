package tags;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessMode {
	public enum type {READONLY,READWRITE,WRITEONLY,DYNAMIC,NOACCESS};
	public boolean sharing() default false;
}
