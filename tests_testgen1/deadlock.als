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
