package tags;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {

	public String resourceID () default "Some URL"; 
	public enum type {PHYSICAL,LOGICAL,COMPUTATIONAL};
	public String hierarchyParent () default "None";
	public String [] reemplazable () default "None";
	public ElasticityModel elasticityModel () ;
	

	
}
