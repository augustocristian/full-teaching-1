package tags;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ElasticityModel {
	public String elasticityID () default "DefaultId"; 
	public int elasticity () default 1;
	public double elasticityCost () default 5.0;
	

	
}
