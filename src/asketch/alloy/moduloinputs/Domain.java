package asketch.alloy.moduloinputs;
import java.util.ArrayList;

public class Domain {
	String variable;
	String domain;
	ArrayList<String> elements;
	
	public Domain(String variable, String domain, ArrayList<String> elements){
		this.variable = variable;
		this.domain = domain;
		this.elements = new ArrayList<String>();
		this.elements.addAll(elements);
	}
	
}
