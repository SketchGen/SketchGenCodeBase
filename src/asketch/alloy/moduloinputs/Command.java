package asketch.alloy.moduloinputs;
import java.util.ArrayList;

public class Command {
	public String func;
	public boolean valid;
	public ArrayList<String> param_values;
	public ArrayList<String> param_labels;
	
	public Command(String func, boolean valid){
		this.func = func;
		this.valid = valid;
		param_values = new ArrayList<String>();
		param_labels = new ArrayList<String>();
	}
	
	public Command(String func, boolean valid, ArrayList<String> param_labels, ArrayList<String> param_values){
		this.func = func;
		this.valid = valid;
		this.param_values = new ArrayList<String>();
		this.param_labels = new ArrayList<String>();
		this.param_values.addAll(param_values);
		this.param_labels.addAll(param_labels);
	}
}
