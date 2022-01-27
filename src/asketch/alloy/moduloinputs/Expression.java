package asketch.alloy.moduloinputs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asketch.alloy.cand.Relation;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;

/**This class represents Alloy expressions.**/
public class Expression implements Comparable{
	public String value;
	public String [] evaluations;
	public HashMap<Integer, String> evals;
	public String [] rep;
	public boolean has_var;
	public ArrayList<Expression> subExprs;
	public String op;
	public int arity;
	public Relation rel;
	
	public Expression(String value, int num_tests, boolean has_var, int arity, Relation rel){
		this.value = value;
		this.has_var = has_var;
		this.arity = arity;
		this.rel = rel;
		evaluations = new String[num_tests];
		rep = new String[num_tests];
		subExprs = new ArrayList<Expression>();
		evals = new HashMap<Integer,String>();
	}
	
	public Expression(String value, int num_tests, boolean has_var,  String op, int arity, Relation rel){
		this.value = value;
		this.has_var = has_var;
		this.op = op;
		this.arity = arity;
		this.rel = rel;
		evaluations = new String[num_tests];
		rep = new String[num_tests];
		subExprs = new ArrayList<Expression>();
		evals = new HashMap<Integer,String>();
	}
	
	public void addSubExpressions(List<Relation> subs, CompModule model){
		/**Set up subexpressions**/
		if(subs != null){
			for(Relation sub : subs){ 
				boolean sub_has_var = false;
				try{
	        		CompUtil.parseOneExpression_fromString(model, sub.getValue());
	        		sub_has_var = false;
	        	}
	        	catch(Exception e){ sub_has_var = true; }
				subExprs.add(new Expression(sub.getValue(), evaluations.length, sub_has_var, sub.getOp().getValue(), sub.getArity(), null)); 
			}
		}
	}
	
	  @Override
	  public boolean equals(Object obj) {
	    if (this == obj) {
	      return true;
	    }
	    if (obj instanceof Expression) {
	      Expression that = (Expression) obj;
	      return value.equals(that.value);
	    }
	    return false;
	  }


	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		 return value.compareTo(((Expression)o).value);
	}

}