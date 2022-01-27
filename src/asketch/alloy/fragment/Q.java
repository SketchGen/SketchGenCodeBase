package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.Q;
import static asketch.alloy.etc.Operators.ALL;
import static asketch.alloy.etc.Operators.LONE;
import static asketch.alloy.etc.Operators.NO;
import static asketch.alloy.etc.Operators.ONE;
import static asketch.alloy.etc.Operators.SOME;

import java.util.Arrays;

/**
 * This class represents Quantifier holes.
 */
public class Q extends Hole {

  public Q(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber, begin, end);
    this.cands = Arrays.asList(ALL, NO, SOME, LONE, ONE);
  }

  @Override
  public void resetContent() {
    setContent(Q);
  }
}
