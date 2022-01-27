package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.CO;
import static asketch.alloy.etc.Operators.EQ;
import static asketch.alloy.etc.Operators.IN;
import static asketch.alloy.etc.Operators.NEQ;
import static asketch.alloy.etc.Operators.NIN;

import java.util.Arrays;

/**
 * This class represents Compare Operator holes.
 */
public class CO extends Hole {

  public CO(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber, begin, end);
    this.cands = Arrays.asList(EQ, IN, NEQ, NIN);
  }

  @Override
  public void resetContent() {
    setContent(CO);
  }
}
