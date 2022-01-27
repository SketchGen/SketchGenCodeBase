module deadlock

sig Process {}
sig Mutex {}

sig State {
  holds, waits: Process -> Mutex
} -- due to ordering, tests do not define State sig valuation

pred Deadlock() {
  some Process
  some s : State | all p : Process | some p.(s.waits)
}

pred Test0{
	 some disj Process0 : Process |  some disj Mutex0, Mutex1, Mutex2 : Mutex |  some disj State0, State1 : State |  {
Process=Process0
Mutex=Mutex0 + Mutex1 + Mutex2
State=State0 + State1
holds=State0->Process0->Mutex0 + State0->Process0->Mutex1 + State0->Process0->Mutex2 + State1->Process0->Mutex0 + State1->Process0->Mutex1 + State1->Process0->Mutex2
waits=State0->Process0->Mutex2 + State1->Process0->Mutex2
		Deadlock[]
	}
}

pred Test1{
	 some disj Process0, Process1: Process |  some disj Mutex0 : Mutex |  some disj State0, State1 : State |  {
Process=Process0 + Process1
Mutex=Mutex0
State=State0 + State1
holds=State1->Process1->Mutex0
waits=State0->Process0->Mutex0
!Deadlock[]
}}

pred Test2{
	 some disj Process0, Process1: Process |  some disj Mutex0 : Mutex |  some disj State0, State1 : State |  {
Process=Process0 + Process1
Mutex=Mutex0
State=State0 + State1
holds=State0->Process1->Mutex0 + State1->Process0->Mutex0 + State1->Process1->Mutex0
waits=State0->Process0->Mutex0 + State1->Process1->Mutex0
!Deadlock[]
}}

pred Test3{
  some disj Process0 : Process |  some disj Mutex0 : Mutex |  some disj State0, State1, State2: State {
    Process = Process0
    Mutex = Mutex0
    State = State0 + State1 + State2
    holds = State0->Process0->Mutex0 + State1->Process0->Mutex0
    waits = State0->Process0->Mutex0
    Deadlock[]
  }
}

pred Test4{
  some disj Process0 : Process |  some disj Mutex0, Mutex1, Mutex2 : Mutex |  some disj State0, State1, State2 : State {
    Process = Process0
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    waits = State0->Process0->Mutex0
    no holds
    Deadlock[]
  }
}

pred Test5{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0 : Process {
    Process = Process0
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    waits = none -> none -> none
    holds = State0->Process0->Mutex2 + State1->Process0->Mutex2 + State2->Process0->Mutex2
    !Deadlock[]
  }
}

pred Test6{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1 : Process {
    Process = Process0 + Process1
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    holds = none -> none -> none
    waits = State0->Process0->Mutex2 + State0->Process1->Mutex0 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State1->Process0->Mutex1 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process1->Mutex0 + State2->Process1->Mutex1
    Deadlock[]
  }
}

pred Test7{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1 : Process {
    Process = Process0 + Process1
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    holds = State0->Process0->Mutex2 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process1->Mutex0 + State2->Process1->Mutex1 + State2->Process1->Mutex2
    waits = State0->Process1->Mutex2 + State1->Process0->Mutex2 + State1->Process1->Mutex2 + State2->Process1->Mutex2
    Deadlock[]
  }
}

pred Test8{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1, Process2 : Process {
    Process = Process0 + Process1 + Process2
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    holds = State0->Process0->Mutex2 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State0->Process2->Mutex1 + State0->Process2->Mutex2 + State1->Process2->Mutex0 + State1->Process2->Mutex1
    waits = State0->Process0->Mutex2 + State0->Process1->Mutex1 + State1->Process0->Mutex2 + State1->Process2->Mutex1 + State2->Process1->Mutex1 + State2->Process2->Mutex0 + State2->Process2->Mutex2
    !Deadlock[]
  }
}

pred Test9{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1 : Process {
    Process = Process0 + Process1
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    holds = State0->Process0->Mutex2 + State0->Process1->Mutex1 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process1->Mutex0 + State2->Process1->Mutex1 + State2->Process1->Mutex2
    waits = State0->Process1->Mutex0 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process0->Mutex0 + State2->Process0->Mutex1
    !Deadlock[]
  }
}