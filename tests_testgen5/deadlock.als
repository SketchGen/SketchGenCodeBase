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

pred Test4{
  some disj Process0, Process1, Process2 : Process |  some disj Mutex0, Mutex1, Mutex2: Mutex |  some disj State0, State1, State2 : State {
    Process = Process0 + Process1 + Process2
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    holds = State0->Process2->Mutex0 + State0->Process2->Mutex1 + State0->Process2->Mutex2 + State1->Process2->Mutex0 + State1->Process2->Mutex1 + State1->Process2->Mutex2 + State2->Process2->Mutex0 + State2->Process2->Mutex1 + State2->Process2->Mutex2
    no waits
    !Deadlock[]
  }
}

pred Test5{
  some disj Process0, Process1 : Process |  some disj Mutex0, Mutex1, Mutex2 : Mutex |  some disj State0, State1, State2 : State {
    Process = Process0 + Process1
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    holds = State2->Process0->Mutex0 + State2->Process0->Mutex1 + State2->Process0->Mutex2
    waits = State0->Process0->Mutex1 + State1->Process0->Mutex0 + State1->Process1->Mutex2
    Deadlock[]
  }
}

