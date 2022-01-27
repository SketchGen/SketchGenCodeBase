one sig FSM {
  start: set State,
  stop: set State
}

sig State {
  transition: set State
}

// Part (a)
fact OneStartAndStop {
  // FSM only has one start state.
  one FSM.start
  // FSM only has one stop state.
  one FSM.stop
}

// Part (b)
fact ValidStartAndStop {
  // A state cannot be both a start state and a stop state.
  no FSM.start & FSM.stop
  // No transition ends at the start state.
  no transition.(FSM.start)
  // No transition begins at the stop state.
  no (FSM.stop).transition
}

// Part (c)
pred Reachability {
  // All states are reachable from the start state.
  //all n: State | n in FSM.start.*transition and FSM.stop in n.*transition 
  // The stop state is reachable from any state.
  all n: State | n in FSM.start.*transition and FSM.stop in n.*transition 
}

pred test1 {
some disj FSM0: FSM {some disj State0, State1: State {
FSM = FSM0
start = FSM0->State1
stop = FSM0->State0
State = State0 + State1
transition = State1->State0
Reachability[]
}}
}
run test1 for 3 

pred test2 {
some disj FSM0: FSM {some disj State0, State1, State2: State {
FSM = FSM0
start = FSM0->State1
stop = FSM0->State0
State = State0 + State1 + State2
transition = State1->State2 + State2->State0 + State2->State2
Reachability[]
}}
}
run test2 for 3 

pred test3 {
some disj FSM0: FSM {some disj State0, State1: State {
FSM = FSM0
start = FSM0->State0
stop = FSM0->State1
State = State0 + State1
transition = State0->State1
Reachability[]
}}
}
run test3 for 3 


pred test4 {
some disj FSM0: FSM {some disj State0, State1, State2: State {
FSM = FSM0
start = FSM0->State2
stop = FSM0->State1
State = State0 + State1 + State2
transition = State0->State0 + State2->State0 + State2->State1
!Reachability[]
}}
}
run test4 for 3 

pred Test5{
	 some disj FSM0 : FSM |  some disj State0, State1 : State |  {
		FSM = FSM0
		State = State0 + State1
		start = FSM0->State0
		stop = FSM0->State1
		transition = State1->State0
		!Reachability[]
	}
}

run Test5 for 3